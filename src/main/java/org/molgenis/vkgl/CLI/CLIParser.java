package org.molgenis.vkgl.CLI;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.IO.DirectoryHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class CLIParser {
    private static final Logger LOGGER = LogManager.getLogger(CLIParser.class.getName());
    private Options options = new Options();
    private static Path inputDirectory;
    private Path outputDirectory;
    private boolean writeVariantTypesToFile = false;
    private boolean countVariantTypes = false;


    public CLIParser() {
        options.addOption("h", "help", false, "Show help.");
        options.addOption("i", "inputDirectory", true, "Directory containing variant files.");
        options.addOption("o", "outputDirectory", true, "Directory for storage of the normalized output files. The program will also use these files to check" +
                "if previous runs has been done and will not execute again for variants already in this directory. If not specified the directory '/normalizedData' will be used.");
        options.addOption(null, "cleanRun", false, "Empties the directory given under option 'outputDirectory' or the standard '/normalizedData'" +
                "directory and starts a clean run.");
        options.addOption("writeVariants", "writeVariantTypesPerUMC", false, "Writes the difference of the variant types (SNPs/del/ins/delins/subs) to a file.");
        options.addOption("count", "countVariantTypeOccurrences", false, "Writes the occurrences of the variants to the screen");
    }

    /**
     * Parses the CLI commands.
     * Four options are available:
     * - 'help': Shows the help of the program to the user.
     * - 'inputDirectory': The directory of the input files.
     * - 'outputDirectory': The directory of the output files.
     * - 'cleanRun': A clean run of the program, meaning: the output directory will be emptied and the program runs for all the variants.
     */
    public void parseCLI(String[] args) {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        String dirNameNormalizedData = "normalizedData";
        DirectoryHandler directoryHandler = new DirectoryHandler();

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                help();
            } else if (cmd.hasOption("i")) {
                setInputDirectory(directoryHandler.validDirectory(cmd.getOptionValue("i")));
                if (cmd.hasOption("o")) {

                    outputDirectory = directoryHandler.validDirectory(cmd.getOptionValue("o"));
                } else {
                    //Creates new directory for normalized data if directory doesn't already exist.
                    try {
                        outputDirectory = directoryHandler.createDirectory(inputDirectory + File.separator + dirNameNormalizedData);
                    } catch (IOException e) {
                        LOGGER.error("Something went wrong while creating: " + outputDirectory);
                    }
                }
                setOutputDirectory(outputDirectory);
                if (cmd.hasOption("cleanRun")) {
                    directoryHandler.emptyDirectory(outputDirectory);
                }
                if (cmd.hasOption("writeVariantTypesPerUMC")) {
                    this.writeVariantTypesToFile = true;
                }
                if (cmd.hasOption("count")) {
                    this.countVariantTypes = true;
                }
            } else {
                throw new IllegalArgumentException("Missing directory for input files.");
            }
        } catch (ParseException e){
            LOGGER.error("Something went wrong while parsing the command line arguments " + e.getMessage());
            help();
        }
    }

    /**
     * Prints the help to the user.
     */
    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Program to check the variants between different UMCs. Program only accepts .csv and .txt files.", options);
        System.exit(0);
    }

    /**
     * Returns the output directory, which is set via the cli parser.
     * @return File outputDirectory
     */
    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public String getStringOutputDirectory() {
        return outputDirectory.toString();
    }

    private void setOutputDirectory(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Path getInputDirectory() {
        return inputDirectory;
    }

    private void setInputDirectory(Path inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public boolean getWriteVariantTypesToFile() { return writeVariantTypesToFile; }
    public boolean getCountVariantTypes() { return countVariantTypes; }

}
