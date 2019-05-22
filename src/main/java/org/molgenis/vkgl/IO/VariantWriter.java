package org.molgenis.vkgl.IO;

import org.molgenis.vkgl.model.*;
import org.molgenis.vkgl.model.variants.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VariantWriter {
    private Path directoryToWrite;
    private DirectoryHandler directoryHandler = new DirectoryHandler();

    /**
     * A writer for the variants.
     * @param directory the directory which should be written to.
     * @throws IOException is thrown when something goes wrong with the creation of the directory.
     */
    public VariantWriter(String directory) throws IOException {
        //This throws IOException if directory can not be created.
        directoryToWrite = directoryHandler.createDirectory(directory);
    }

    public void writeVCFVariantsToFile(Map<String, ArrayList<VCFVariant>> VCFVariantsPerUMC) throws IOException {
        for (Map.Entry<String, ArrayList<VCFVariant>> vcfVariantsPerUMC : VCFVariantsPerUMC.entrySet()) {
            String nameUMC = vcfVariantsPerUMC.getKey();
            ArrayList<VCFVariant> vcfVariants = vcfVariantsPerUMC.getValue();

            File vcfFile = directoryHandler.createFile(nameUMC + ".vcf", directoryToWrite);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(vcfFile));
            for (VCFVariant vcfVariant : vcfVariants) {
                String line = createVCFVariantLine(vcfVariant);
                bufferedWriter.write(line);
            }
            bufferedWriter.close();
        }
    }

    private String createVCFVariantLine(VCFVariant variant) {
        return variant.getChromosome() +
                "\t" + variant.getPosition() +
                "\t" + "." +
                "\t" + variant.getREF() +
                "\t" + variant.getALT() +
                "\t" + variant.getClassification() + "\n";
//                "\t" + variant.isValidVariant() + "\n";
    }

    /**
     * A writer for the difference in variant types per UMC. It uses the variables from Enum VariantType to determine which
     * kind of types are available. Creates files per VariantType and writes the variant to their corresponding file.
     * @param variantParser VariantParser which holds the parsed variants per format type (Cartagenia, HGVS and radboud).
     * @throws IOException is thrown when files could not be created.
     */
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
            writeVariantsToVariantTypeFile(variantList, files);
        }

        for (Map.Entry<String, ArrayList<CartageniaVariant>> cartagenia : cartageniaVariants.entrySet()) {
            String nameUMC = cartagenia.getKey();
            writeNameUMCToFile(files, nameUMC);
            ArrayList<CartageniaVariant> variantList = cartagenia.getValue();
            variantList.sort(CartageniaVariant.Comparators.CHROMOSOME_AND_START);
            writeVariantsToVariantTypeFile(variantList, files);
        }

        for (Map.Entry<String, ArrayList<HGVSVariant>> HGVS : HGVSVariants.entrySet()) {
            String nameUMC = HGVS.getKey();
            writeNameUMCToFile(files, nameUMC);
            ArrayList<HGVSVariant> variantList = HGVS.getValue();
            variantList.sort(HGVSVariant.Comparators.CHROMOSOME_AND_POSITION);
            writeVariantsToVariantTypeFile(variantList, files);
        }
    }

    /**
     * Creates files for the different variant types.
     * @return Map<VariantType, File>: A map with as key the VariantType and the corresponding File.
     */
    private Map<VariantType, File> createFilesForVariantTypes() {
        Map<VariantType, File> files = new HashMap<>();
        for (VariantType variantType : VariantType.values()) {
            files.put(variantType, directoryHandler.createFile(variantType.toString().toLowerCase() + ".txt", directoryToWrite));
        }
        return files;
    }

    /**
     * Writes the name of the UMC to files.
     * @param files the files to write to.
     * @param nameUMC the name of the UMC.
     * @throws IOException is thrown if something went wrong with writing to the file.
     */
    private void writeNameUMCToFile(Map<VariantType, File> files, String nameUMC) throws IOException {
        for (File file : files.values()) {
            String line = "################## " + nameUMC + " ##################\n";
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(line);
            writer.close();
        }
    }

    /**
     * Writes the variants to their corresponding variantType file.
     * @param variantList an ArrayList containing the Variants.
     * @param files a Map containing the files.
     * @throws IOException is thrown when something went wrong while writing to file.
     */
    private void writeVariantsToVariantTypeFile(ArrayList<? extends Variant> variantList, Map<VariantType, File> files) throws IOException {
        for (Variant variant : variantList) {
            String line = variant.getRawInformation() + "\n";
            BufferedWriter writer = new BufferedWriter(new FileWriter(files.get(variant.getVariantType()), true));
            writer.append(line);
            writer.close();
        }
    }
}
