package org.molgenis.vkgl.IO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.CLI.CLIParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryHandler {

    private static final Logger LOGGER = LogManager.getLogger(CLIParser.class.getName());

    /**
     * Checks if the path given by the user is a valid directory.
     * @param directory a String representation of a path.
     * @return a Path object from the given path.
     * @throws IllegalArgumentException if path is not a valid directory.
     */
    public Path validDirectory(String directory) {
        Path path = new File(directory).toPath();
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException(path + " is not a valid directory.");
        }
        return path;
    }

    public File validFile(String directory) {
        File file = new File(directory);
        if (!file.exists()) {
            throw new IllegalArgumentException(file + " is not a valid file.");
        }
        return file;
    }

    /**
     * Creates new directory. If the directory already exists, it is not overwritten.
     * @param directory String: the directory to create.
     */
    public Path createDirectory(String directory) throws IOException {
        LOGGER.debug("Creating new directory: " + directory);
        try {
            return Files.createDirectory(Paths.get(directory));
        } catch (FileAlreadyExistsException e) {
            LOGGER.debug("Directory: {} already exists, not creating it again.", directory);
            return Paths.get(directory);
        }
    }

    /**
     * Empties the data directory if there are files present.
     * @param directory the directory to clean.
     */
    public void emptyDirectory(Path directory) {
        LOGGER.debug("Emptying: {}", directory);
        try {
            FileUtils.cleanDirectory(directory.toFile());
        } catch (IOException e) {
            LOGGER.warn("Something went wrong while emptying the normalized data directory: {}", directory);
            LOGGER.debug(e.getStackTrace());
        }
    }

    File createFile(String file, Path directory) {
        LOGGER.debug("Creating file: " + directory + File.separator + file);
        return new File(directory + File.separator + file);
    }
}
