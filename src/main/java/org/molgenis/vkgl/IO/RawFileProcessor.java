package org.molgenis.vkgl.IO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.service.VariantToVCFConverter;
import org.molgenis.vkgl.service.VariantTypeCounter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class RawFileProcessor {
    private static final Logger LOGGER = LogManager.getLogger(RawFileProcessor.class.getName());
    private CLIParser CLIParser = new CLIParser();
    private VariantParser variantParser = new VariantParser();
    private VariantToVCFConverter variantToVCFConverter = new VariantToVCFConverter();

    /**
     * Starts the processing of command line arguments.
     * Gets the input directory from the user, and starts processing the files in this directory.
     * @param args arguments from the command line.
     */
    public void start(String args[]) {
        CLIParser.parseCLI(args);
        Path inputDirectory = CLIParser.getInputDirectory();
        processFiles(inputDirectory);

        if (CLIParser.getWriteVariantTypesToFile()) {
            startVariantWriter(inputDirectory);
        }

        if (CLIParser.getCountVariantTypes()) {
            startVariantCounter();
        }

        if (CLIParser.convertVariants()) {
            variantToVCFConverter.convertVariants(variantParser, CLIParser.getStringOutputDirectory());
        }
    }

    /**
     * Starts the processing of the variant files in a directory.
     * @param filePath File - which contains the input files.
     */
    private void processFiles(Path filePath) {
        String[] fileExtensions = new String[3];
        //File extensions from the raw files.
        fileExtensions[0] = "csv";
        fileExtensions[1] = "txt";
        Iterator<File> iterator = FileUtils.iterateFiles(new File(filePath.toString()), fileExtensions, false);
        while (iterator.hasNext()) {
            File file = iterator.next();
            LOGGER.info("Processing file: {}", file);
            variantParser.parseFile(file);
        }
    }

    /**
     * Starts the variant writer.
     */
    private void startVariantWriter(Path directory) {
        try {
            VariantWriter variantWriter = new VariantWriter(directory.toString() + "/differenceInVariants");
            variantWriter.writeDifferenceInVariantTypesToFile(variantParser);
        } catch (IOException e) {
            LOGGER.warn("Something went wrong while writing variants to file. Continuing program.");
        }
    }

    /**
     * Starts the variant counter.
     */
    private void startVariantCounter() {
        new VariantTypeCounter(variantParser.getAllVariants());
    }
}
