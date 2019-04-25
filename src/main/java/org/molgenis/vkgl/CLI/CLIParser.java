package org.molgenis.vkgl.CLI;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CLIParser {
    private static final Logger LOGGER = LogManager.getLogger(CLIParser.class.getName());
    private Options options = new Options();
    private File inputDirectory;
    private File outputDirectory;


    public CLIParser() {
        options.addOption("h", "help", false, "Show help.");
        options.addOption("i", "inputDirectory", true, "Directory containing variant files.");
        options.addOption("o", "outputDirectory", true, "Directory for storage of the normalized output files. The program will also use these files to check" +
                "if previous runs has been done and will not execute again for variants already in this directory. If not specified the directory '/normalizedData' will be used.");
        options.addOption(null, "cleanRun", false, "Empties the directory given under option 'outputDirectory' or the standard '/normalizedData'" +
                "directory and starts a clean run.");
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

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                help();
            } else if (cmd.hasOption("i")) {
                setInputDirectory(pathValidDirectory(cmd.getOptionValue("i")));
                if (cmd.hasOption("o")) {
                    outputDirectory = pathValidDirectory(cmd.getOptionValue("o"));
                } else {
                    outputDirectory = new File(inputDirectory + File.separator + dirNameNormalizedData);
                }
                setOutputDirectory(outputDirectory);
                createNewDirectory(outputDirectory);
                if (cmd.hasOption("cleanRun")) {
                    emptyNormalizedDataDirectory(outputDirectory);
                }
            } else {
                throw new IllegalArgumentException("Missing directory for input files.");
            }
        } catch(ParseException e){
            LOGGER.error("Something went wrong while parsing the command line arguments" + e.getMessage());
        }
    }

    /**
     * Prints the help to the user.
     */
    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
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

    /**
     * Creates new directory. If the directory already exists, it is not overwritten.
     * @param directory the directory to create.
     */
    private void createNewDirectory(File directory) {
        LOGGER.debug("Creating new directory: " + directory);
        try {
            Files.createDirectory(Paths.get(directory.toString()));
        } catch (FileAlreadyExistsException e) {
            LOGGER.debug("Directory: " + directory + " already exists, not creating it again.");
        } catch (IOException e) {
            LOGGER.warn("Something went wrong while creating directory for: " + directory);
            LOGGER.warn(e.getStackTrace());
        }
    }

    /**
     * Empties the data directory if there are files present.
     * @param directory the directory to clean.
     */
    private void emptyNormalizedDataDirectory(File directory) {
        LOGGER.info("Emptying: " + directory);
        try {
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            LOGGER.warn("Something went wrong while emptying the normalized data directory: " + directory);
            LOGGER.debug(e.getStackTrace());
        }
    }

    /**
     * Returns the output directory, which is set via the cli parser.
     * @return File outputDirectory
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    private void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    private void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
    }
}
