package org.molgenis.vkgl.IO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.model.Variant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class VariantWriter {
    private static final Logger LOGGER = LogManager.getLogger(CLIParser.class.getName());
    private Map<String, ArrayList<Variant>> variants;
    private Path directoryToWrite;
    private DirectoryHandler directoryHandler = new DirectoryHandler();

    public VariantWriter(Map<String, ArrayList<Variant>> variantsForUMCs, String directory) throws IOException {
        this.variants = variantsForUMCs;
        CLIParser cliParser = new CLIParser();
        Path inputDirectory = cliParser.getInputDirectory();

        //This throws IOException if directory can not be created.
        directoryToWrite = directoryHandler.createDirectory(inputDirectory + File.separator + directory);
    }

    public void writeDifferenceInVariantTypesToFile() throws IOException {
        //Create files for different kinds of variants.
        ArrayList<File> files = new ArrayList<>();

        File SNPs = directoryHandler.createFile("snps.txt", directoryToWrite);
        files.add(SNPs);
        File insertions = directoryHandler.createFile("insertions.txt", directoryToWrite);
        files.add(insertions);
        File deletions = directoryHandler.createFile("deletions.txt", directoryToWrite);
        files.add(deletions);
        File duplications = directoryHandler.createFile("duplications.txt", directoryToWrite);
        files.add(duplications);
        File deletionsInsertions = directoryHandler.createFile("delins.txt", directoryToWrite);
        files.add(deletionsInsertions);
        File notClassified = directoryHandler.createFile("notclassified.txt", directoryToWrite);
        files.add(notClassified);

        for (Map.Entry<String, ArrayList<Variant>> entry : variants.entrySet()) {
            String nameUMC = entry.getKey();
            for (File file : files) {
                String line = "################## " + nameUMC + " ##################\n";
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.append(line);
                writer.close();
            }
            ArrayList<Variant> variantList = entry.getValue();
            Collections.sort(variantList, Variant.VariantComparator);

            for (Variant variant : variantList) {
                String line = variant.getRawInformation() + "\n";
                switch (variant.getVariantType()) {
                    case SNP:
                        BufferedWriter writerSNPs = new BufferedWriter(new FileWriter(SNPs, true));
                        writerSNPs.append(line);
                        writerSNPs.close();
                        break;
                    case DELETION:
                        BufferedWriter writerDeletions = new BufferedWriter(new FileWriter(deletions, true));
                        writerDeletions.append(line);
                        writerDeletions.close();
                        break;
                    case INSERTION:
                        BufferedWriter writerInsertions = new BufferedWriter(new FileWriter(insertions, true));
                        writerInsertions.append(line);
                        writerInsertions.close();
                    case DUPLICATION:
                        BufferedWriter writerDuplications = new BufferedWriter(new FileWriter(duplications, true));
                        writerDuplications.append(line);
                        writerDuplications.close();
                        break;
                    case DELETION_INSERTION:
                        BufferedWriter writerDeletionInsertions = new BufferedWriter(new FileWriter(deletionsInsertions, true));
                        writerDeletionInsertions.append(line);
                        writerDeletionInsertions.close();
                        break;
                    case NOT_CLASSIFIED:
                        BufferedWriter writerNotClassified = new BufferedWriter(new FileWriter(notClassified, true));
                        writerNotClassified.append(line);
                        writerNotClassified.close();
                        break;
                }
            }
        }
    }
}
