package org.molgenis.vkgl.IO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.model.CartageniaVariant;
import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.RadboudVariant;
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
    private Path directoryToWrite;
    private DirectoryHandler directoryHandler = new DirectoryHandler();

    public VariantWriter(String directory) throws IOException {
        CLIParser cliParser = new CLIParser();
        Path inputDirectory = cliParser.getInputDirectory();

        //This throws IOException if directory can not be created.
        directoryToWrite = directoryHandler.createDirectory(inputDirectory + File.separator + directory);
    }

    public void writeDifferenceInVariantTypesToFile(VariantParser variantParser) throws IOException {
        //Create files for different kinds of variants.
        File[] files = createFilesForVariantTypes();

        Map<String, ArrayList<RadboudVariant>> radboudVariants = variantParser.getRadboudVariants();
        Map<String, ArrayList<CartageniaVariant>> cartageniaVariants = variantParser.getCartageniaVariants();
        Map<String, ArrayList<HGVSVariant>> HGVSVariants = variantParser.getHGVSVariants();

        for (Map.Entry<String, ArrayList<RadboudVariant>> radboud : radboudVariants.entrySet()) {
            String nameUMC = radboud.getKey();
            writeNameUMCToFile(files, nameUMC);
            ArrayList<RadboudVariant> variantList = radboud.getValue();
            Collections.sort(variantList, RadboudVariant.Comparators.START);

//        for (Map.Entry<String, ArrayList<Variant>> entry : variants.entrySet()) {
//            String nameUMC = entry.getKey();
//            ArrayList<Variant> variantList = entry.getValue();
//            Collections.sort(variantList, Variant.VariantComparator);

            for (Variant variant : variantList) {
                String line = variant.getRawInformation() + "\n";
                switch (variant.getVariantType()) {
                    case SNP:
                        BufferedWriter writerSNPs = new BufferedWriter(new FileWriter(files[0], true));
                        writerSNPs.append(line);
                        writerSNPs.close();
                        break;
                    case DELETION:
                        BufferedWriter writerDeletions = new BufferedWriter(new FileWriter(files[1], true));
                        writerDeletions.append(line);
                        writerDeletions.close();
                        break;
                    case INSERTION:
                        BufferedWriter writerInsertions = new BufferedWriter(new FileWriter(files[2], true));
                        writerInsertions.append(line);
                        writerInsertions.close();
                    case DUPLICATION:
                        BufferedWriter writerDuplications = new BufferedWriter(new FileWriter(files[3], true));
                        writerDuplications.append(line);
                        writerDuplications.close();
                        break;
                    case DELETION_INSERTION:
                        BufferedWriter writerDeletionInsertions = new BufferedWriter(new FileWriter(files[4], true));
                        writerDeletionInsertions.append(line);
                        writerDeletionInsertions.close();
                        break;
                    case NOT_CLASSIFIED:
                        BufferedWriter writerNotClassified = new BufferedWriter(new FileWriter(files[5], true));
                        writerNotClassified.append(line);
                        writerNotClassified.close();
                        break;
                }
            }
        }
    }

    private File[] createFilesForVariantTypes() {
        File[] files = new File[6];
        File SNPs = directoryHandler.createFile("snps.txt", directoryToWrite);
        files[0] = SNPs;
        File insertions = directoryHandler.createFile("insertions.txt", directoryToWrite);
        files[1] = insertions;
        File deletions = directoryHandler.createFile("deletions.txt", directoryToWrite);
        files[2] = deletions;
        File duplications = directoryHandler.createFile("duplications.txt", directoryToWrite);
        files[3] = duplications;
        File deletionsInsertions = directoryHandler.createFile("delins.txt", directoryToWrite);
        files[4] = deletionsInsertions;
        File notClassified = directoryHandler.createFile("notclassified.txt", directoryToWrite);
        files[5] = notClassified;

        return files;
    }

    private void writeNameUMCToFile(File[] files, String nameUMC) throws IOException {
        for (File file : files) {
            String line = "################## " + nameUMC + " ##################\n";
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(line);
            writer.close();
        }
    }
}
