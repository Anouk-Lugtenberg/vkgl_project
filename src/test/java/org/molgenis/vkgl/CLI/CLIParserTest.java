package org.molgenis.vkgl.CLI;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.molgenis.vkgl.LogAppenderResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CLIParserTest {

    //Used for testing the output of Log4j2.
    private LogAppenderResource appender = new LogAppenderResource(LogManager.getLogger(CLIParser.class.getName()));
    private CLIParser CLIParser = new CLIParser();

    @BeforeEach
    void init() { appender.before(); }

    @AfterEach
    void close() {
        appender.after();
    }

    @Test
    void throwsErrorWhenNoCLIArgumentsPresent() {
        String[] args = new String[0];
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> CLIParser.parseCLI(args),
                "Expected parseCLI() to throw IllegalArgumentException, but it didn't.");
        assertTrue(thrown.getMessage().contains("Missing directory for input files."));
    }

    @Test
    void setInputDirectorySunny(@TempDir Path inputDirectory, @TempDir Path fastaDirectory) {
        String[] args = new String[4];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        args[2] = "-f";
        args[3] = fastaDirectory.toString();
        CLIParser.parseCLI(args);
        assertEquals(CLIParser.getInputDirectory(), inputDirectory);
    }

    @Test
    void throwsErrorWhenInputDirectoryPathNotValid(@TempDir Path fastaDirectory) {
        String[] args = new String[4];
        args[0] = "-i";
        args[1] = "/this/directory/does/not/exist";
        args[2] = "-f";
        args[3] = fastaDirectory.toString();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> CLIParser.parseCLI(args),
                "Expected parseCLI() to throw IllegalArgumentException when input directory" +
                        "is not valid, but it didn't.");
        assertTrue(thrown.getMessage().contains(args[1] + " is not a valid directory."));
    }

    @Test
    void setOutputDirectoryGivenByUserSunny(@TempDir Path inputDirectory, @TempDir Path fastaDirectory, @TempDir Path outputDirectory) {
        String[] args = new String[6];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        args[2] = "-o";
        args[3] = outputDirectory.toString();
        args[4]= "-f";
        args[5] = fastaDirectory.toString();
        CLIParser.parseCLI(args);
        Path actualOutputDirectory = CLIParser.getOutputDirectory();
        assertEquals(actualOutputDirectory, outputDirectory);
    }

    @Test
    void throwsErrorWhenOutputDirectoryGivenByUserPathNotValid(@TempDir Path inputDirectory, @TempDir Path fastaDirectory) {
        String[] args = new String[6];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        args[2] = "-o";
        args[3] = "/not/a/directory";
        args[4] = "-f";
        args[5] = fastaDirectory.toString();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> CLIParser.parseCLI(args),
                "Expected parseCLI() to throw IllegalArgumentException when input directory" +
                        "is not valid, but it didn't.");
        assertTrue(thrown.getMessage().contains(args[3] + " is not a valid directory."));
    }

    @Test
    void setOutputDirectoryIfNotGivenByUser(@TempDir Path inputDirectory, @TempDir Path fastaDirectory) {
        String[] args = new String[4];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        args[2] = "-f";
        args[3] = fastaDirectory.toString();
        CLIParser.parseCLI(args);
        Path expectedOutputDirectory = new File(inputDirectory.toString() + File.separator + "normalizedData").toPath();
        Path actualOutputDirectory = CLIParser.getOutputDirectory();
        assertEquals(actualOutputDirectory, expectedOutputDirectory);
    }

    @Test
    void emptyNormalizedDataDirectoryWithCleanRun(@TempDir Path inputDirectory, @TempDir Path outputDirectory,
                                                  @TempDir Path fastaDirectory) throws IOException {
        String[] args = new String[7];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        args[2] = "-o";
        args[3] = outputDirectory.toString();
        args[4] = "--cleanRun";
        args[5] = "-f";
        args[6] = fastaDirectory.toString();

        //Adds file to output directory to be deleted
        Path file = Files.createFile(outputDirectory.resolve("test.txt"));
        //Check that the file is added.
        assertTrue(Files.exists(file));

        CLIParser.parseCLI(args);

        //Check that file is deleted on clean run.
        assertFalse(Files.exists(file));
    }

    @Test
    void catchFileAlreadyExistsExceptionIfOutputDirectoryAlreadyExists(@TempDir Path inputDirectory,
                                                                       @TempDir Path fastaDirectory) throws IOException {
        Path file = Files.createDirectory(new File(inputDirectory.toString() + File.separator + "normalizedData").toPath());
        String[] args = new String[4];
        args[0] = "-i";
        args[1] = inputDirectory.toString();
        args[2] = "-f";
        args[3] = fastaDirectory.toString();
        CLIParser.parseCLI(args);

        assertThat(appender.getOutput(), containsString("Directory: " + file + " already exists, not creating it again."));
    }
}
