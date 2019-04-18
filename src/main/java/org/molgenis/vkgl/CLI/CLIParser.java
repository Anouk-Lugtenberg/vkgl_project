package org.molgenis.vkgl.CLI;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.IO.RawFileProcessor;

import java.io.File;

public class CLIParser {
    private static final Logger LOGGER = LogManager.getLogger(CLIParser.class.getName());
    private static File outputDirectory;
    private static File normalizedDataDirectory;
    private String[] args;
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
        File inputDirectory;
        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        RawFileProcessor rawFileProcessor = new RawFileProcessor();
        String dirNameNormalizedData = "NormalizedData";

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                help();
            }
            if (cmd.hasOption("i")) {
                try {
                    inputDirectory = pathValidDirectory(cmd.getOptionValue("i"));
                    if (cmd.hasOption("n")) {
                        normalizedDataDirectory = pathValidDirectory(cmd.getOptionValue("n"));
                    } else {
                        normalizedDataDirectory = new File(inputDirectory + File.separator + dirNameNormalizedData);
                        createNewDirectory(normalizedDataDirectory);
                    }
                    if (cmd.hasOption("o")) {
                        outputDirectory = pathValidDirectory(cmd.getOptionValue("o"));
                    } else {
                        outputDirectory = new File(inputDirectory + File.separator + dirNameNormalizedData);
                        createNewDirectory(outputDirectory);
                    }
                    rawFileProcessor.processRawFiles(inputDirectory);
                } catch (IllegalArgumentException e) {
                    LOGGER.error(e.getMessage());
            }
            } else {
                LOGGER.error("Missing directory for input files");
                help();
            }
        } catch(ParseException e){
            LOGGER.error("Something went wrong while parsing the command line arguments" + e.getMessage());
        }
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }

    /**
     * Checks if the path given by the user is a valid directory.
     * @param path a String representation of a path.
     * @return a File object from the given path.
     * @throws IllegalArgumentException if path is not a valid directory.
     */
    private File pathValidDirectory(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(path + " is not a (existing) directory.");
        }
        return file;
    }

    private void createNewDirectory(File file) {
        if (file.mkdir()) {
            LOGGER.info("New directory created for normalized files: " + file);
        } else {
            LOGGER.warn("Something went wrong while creating the directory for the output files.");
        }
    }

    public static File getOutputDirectory() {
        return outputDirectory;
    }

    public static File getNormalizedDataDirectory() {
        return normalizedDataDirectory;
    }
}
