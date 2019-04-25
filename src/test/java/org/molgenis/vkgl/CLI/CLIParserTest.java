package org.molgenis.vkgl.CLI;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CLIParserTest {

    //Used for the test of log4j.
    private LogAppenderResource appender = new LogAppenderResource(LogManager.getLogger(CLIParser.class.getName()));
    private CLIParser CLIParser = new CLIParser();

    @TempDir
    static Path inputDirectory;

    @TempDir
    static Path outputDirectory;

    @BeforeAll
    static void setUp() {
        assertTrue(Files.isDirectory(inputDirectory));
        assertTrue(Files.isDirectory(outputDirectory));
    }

    @BeforeEach
    void init() {
        appender.before();
    }

    @AfterEach
    void close() {
        appender.after();
    }

    @Test
    void noCLIArgumentsPresent() {
        String[] args = new String[0];
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> CLIParser.parseCLI(args),
                "Expected parseCLI() to throw IllegalArgumentException, but it didn't.");
        assertTrue(thrown.getMessage().contains("Missing directory for input files."));
    }

    @Test
    void setInputDirectorySunny() {
        String[] args = new String[2];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        CLIParser.parseCLI(args);
        assertEquals(CLIParser.getInputDirectory(), new File(inputDirectory.toString()));
    }

    @Test
    void setInputDirectoryPathNotValid() {
        String[] args = new String[2];
        args[0] = "-i";
        args[1] = "/this/directory/does/not/exist";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> CLIParser.parseCLI(args),
                "Expected parseCLI() to throw IllegalArgumentException when input directory" +
                        "is not valid, but it didn't.");
        assertTrue(thrown.getMessage().contains(args[1] + " is not a (existing) directory."));
    }

    @Test
    void setOutputDirectoryGivenByUserSunny() {
        String[] args = new String[4];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        args[2] = "-o";
        args[3] = outputDirectory.toString();
        CLIParser.parseCLI(args);
        assertEquals(CLIParser.getOutputDirectory(), new File(outputDirectory.toString()));
    }

    @Test
    void setOutputDirectoryGivenByUserPathNotValid() {

    }

    @Test
    void setStandardOutputDirectory() {
        String[] args = new String[2];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        CLIParser.parseCLI(args);
        assertEquals(CLIParser.getOutputDirectory(), new File(inputDirectory.toString() +
                File.separator + "normalizedData"));
    }
}
