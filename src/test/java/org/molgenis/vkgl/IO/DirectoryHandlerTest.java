package org.molgenis.vkgl.IO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryHandlerTest {

    private DirectoryHandler directoryHandler = new DirectoryHandler();

    @Test
    void validDirectoryReturnsPathIfValid(@TempDir Path expectedPath) {
        String stringDirectory = expectedPath.toString();
        Path actualPath = directoryHandler.validDirectory(stringDirectory);
        assertEquals(expectedPath, actualPath);
    }

    @Test
    void validDirectoryThrowsIllegalArgumentIfPathInvalid() {
        String invalidDirectory = "/this/is/not/valid";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> directoryHandler.validDirectory(invalidDirectory));
        assertTrue(thrown.getMessage().contains(invalidDirectory + " is not a valid directory."));
    }

    @Test
    void emptyDirectorySunny(@TempDir Path directoryToEmpty) throws IOException {
        //Creates file in directory
        Path file = Files.createFile(directoryToEmpty.resolve("test.txt"));
        //Check that the file exits
        assertTrue(Files.exists(file));

        directoryHandler.emptyDirectory(directoryToEmpty);
        //Check that file doesn't exist
        assertFalse(Files.exists(file));
    }

    @Test
    void emptyDirectoryInvalidDirectory() {
        Path invalidDirectory = new File("/this/is/not/valid").toPath();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> directoryHandler.emptyDirectory(invalidDirectory));
        assertTrue(thrown.getMessage().contains(invalidDirectory + " does not exist"));
    }

    @Test
    void createFileSunny(@TempDir Path directory) {
        File expectedFile = new File(directory.toString() + File.separator + "createdFile");
        File actualFile = directoryHandler.createFile("createdFile", directory);
        assertEquals(expectedFile, actualFile);
    }


}
