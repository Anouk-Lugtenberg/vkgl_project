package org.molgenis.vkgl.CLI;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.IO.RawFileProcessor;

import java.io.File;

public class CLIParser {
    private static final Logger LOGGER = LogManager.getLogger(CLIParser.class.getName());
    private static String inputDirectory;
    private static String outputDirectory;
    private static String normalizedDataDirectory;
    private String[] args = null;
    private Options options = new Options();

    public CLIParser(String[] args) {
        this.args = args;
        options.addOption("h", "help", false, "show help");
        options.addOption("i", "inputDirectory", true, "directory containing variant files");
        options.addOption("n", "normalizedDataDirectory", true, "directory containing (already) formatted files. " +
                "if not specified the program will look for the files in <inputDirectory>/normalizedData");
        options.addOption("o", "outputDirectory", true, "directory for storage of the normalized output files, " +
                "if not specified it falls back on <inputDirectory>/normalizedData. If this doesn't contain any data, the program will start fresh.");
    }

    public void parseCLI() {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        RawFileProcessor rawFileProcessor = new RawFileProcessor();

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                help();
            }
            if (cmd.hasOption("i")) {
                inputDirectory = cmd.getOptionValue("i");
                try {
                    pathValidDirectory(inputDirectory);
                    rawFileProcessor.processRawFiles(inputDirectory);
                } catch (IllegalArgumentException e) {
                    LOGGER.error(e.getMessage());
                }
            } else {
                LOGGER.error("Missing directory for input files");
                help();
            }

            if (cmd.hasOption("n")) {
                normalizedDataDirectory = cmd.getOptionValue("n");
                try {
                    pathValidDirectory(normalizedDataDirectory);
                } catch (IllegalArgumentException e) {
                    LOGGER.error(e.getMessage());
                }
            }
            if (cmd.hasOption("o")) {
                outputDirectory = cmd.getOptionValue("o");
                try {
                    pathValidDirectory(outputDirectory);
                } catch (IllegalArgumentException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        } catch (ParseException e) {
            LOGGER.error("Something went wrong while parsing the command line arguments" + e.getMessage());
        }
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }

    /**
     *
     * @param path
     * @return
     * @throws IllegalArgumentException
     */
    private void pathValidDirectory(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(path + " is not a (existing) directory.");
        }
    }
}
