package org.molgenis.vkgl.IO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
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
        Map<VariantType, File> files = createFilesForVariantTypes();

        Map<String, ArrayList<RadboudVariant>> radboudVariants = variantParser.getRadboudVariants();
        Map<String, ArrayList<CartageniaVariant>> cartageniaVariants = variantParser.getCartageniaVariants();
        Map<String, ArrayList<HGVSVariant>> HGVSVariants = variantParser.getHGVSVariants();

        for (Map.Entry<String, ArrayList<RadboudVariant>> radboud : radboudVariants.entrySet()) {
            String nameUMC = radboud.getKey();
            writeNameUMCToFile(files, nameUMC);
            ArrayList<RadboudVariant> variantList = radboud.getValue();
            variantList.sort(RadboudVariant.Comparators.CHROMOSOME_AND_START);
            writeVariantListToFile(variantList, files);
        }

        for (Map.Entry<String, ArrayList<CartageniaVariant>> cartagenia : cartageniaVariants.entrySet()) {
            String nameUMC = cartagenia.getKey();
            writeNameUMCToFile(files, nameUMC);
            ArrayList<CartageniaVariant> variantList = cartagenia.getValue();
            variantList.sort(CartageniaVariant.Comparators.CHROMOSOME_AND_START);
            writeVariantListToFile(variantList, files);
        }

        for (Map.Entry<String, ArrayList<HGVSVariant>> HGVS : HGVSVariants.entrySet()) {
            String nameUMC = HGVS.getKey();
            writeNameUMCToFile(files, nameUMC);
            ArrayList<HGVSVariant> variantList = HGVS.getValue();
            variantList.sort(HGVSVariant.Comparators.CHROMOSOME_AND_POSITION);
            writeVariantListToFile(variantList, files);
        }
    }

    private Map<VariantType, File> createFilesForVariantTypes() {
        Map<VariantType, File> files = new HashMap<>();
        for (VariantType variantType : VariantType.values()) {
            files.put(variantType, directoryHandler.createFile(variantType.toString().toLowerCase() + ".txt", directoryToWrite));
        }
        return files;
    }

    private void writeNameUMCToFile(Map<VariantType, File> files, String nameUMC) throws IOException {
        for (File file : files.values()) {
            String line = "################## " + nameUMC + " ##################\n";
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(line);
            writer.close();
        }
    }

    private void writeVariantListToFile(ArrayList<? extends Variant> variantList, Map<VariantType, File> files) throws IOException {
        for (Variant variant : variantList) {
            String line = variant.getRawInformation() + "\n";
            BufferedWriter writer = new BufferedWriter(new FileWriter(files.get(variant.getVariantType()), true));
            writer.append(line);
            writer.close();
        }
    }
}
