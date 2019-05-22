package org.molgenis.vkgl.IO;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.model.variants.CartageniaVariant;
import org.molgenis.vkgl.model.variants.HGVSVariant;
import org.molgenis.vkgl.model.variants.RadboudVariant;
import org.molgenis.vkgl.model.variants.Variant;
import org.molgenis.vkgl.service.VariantFormat;
import org.molgenis.vkgl.service.VariantFormatDeterminer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VariantParser {
    private static final Logger LOGGER = LogManager.getLogger(VariantParser.class.getName());
    private Map<String, ArrayList<RadboudVariant>> radboudVariants = new HashMap<>();
    private Map<String, ArrayList<HGVSVariant>> HGVSVariants = new HashMap<>();
    private Map<String, ArrayList<CartageniaVariant>> cartageniaVariants = new HashMap<>();
    private Map<String, ArrayList<Variant>> allVariants = new HashMap<>();

    void parseFile(File file) {
        String nameUMC = FilenameUtils.removeExtension(file.getName());

        //Get the variant format.
        VariantFormatDeterminer variantFormatDeterminer = new VariantFormatDeterminer(file.toString());
        VariantFormat variantFormat = variantFormatDeterminer.getVariantFormat();

        //Create the lists for the different formats.
        ArrayList<RadboudVariant> listRadboudVariants = new ArrayList<>();
        ArrayList<HGVSVariant> listHGVSVariants = new ArrayList<>();
        ArrayList<CartageniaVariant> listCartageniaVariants = new ArrayList<>();

        //Create a list which contains all the variants.
        ArrayList<Variant> listAllVariants = new ArrayList<>();

        int lineCount = 0;
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                lineCount++;
                try {
                    switch(variantFormat) {
                        case RADBOUD:
                            RadboudVariant radboudVariant = createRadboudVariant(line, lineCount);
                            listRadboudVariants.add(radboudVariant);
                            listAllVariants.add(radboudVariant);
                            break;
                        case HGVS:
                            HGVSVariant HGVSVariant = createHGVSVariant(line, lineCount);
                            listHGVSVariants.add(HGVSVariant);
                            listAllVariants.add(HGVSVariant);
                            break;
                        case CARTAGENIA:
                            CartageniaVariant cartageniaVariant = createCartageniaVariant(line, lineCount);
                            listCartageniaVariants.add(cartageniaVariant);
                            listAllVariants.add(cartageniaVariant);
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    if (lineCount != 1) {
                        LOGGER.error("Line " + lineCount + " of " + file + " could not be processed. Please check the syntax.");
                        LOGGER.error(line);
                    } else {
                        //Some of the files contain headers, those should be skipped.
                        LOGGER.info("Line " + lineCount + " of " + file + " could not be processed. Probably header, skipping line.");
                        LOGGER.info(line);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Something went wrong while parsing file: " + file);
            LOGGER.info(e.getMessage());
        }
        switch(variantFormat) {
            case RADBOUD:
                radboudVariants.put(nameUMC, listRadboudVariants);
                break;
            case HGVS:
                HGVSVariants.put(nameUMC, listHGVSVariants);
                break;
            case CARTAGENIA:
                cartageniaVariants.put(nameUMC, listCartageniaVariants);
        }

        allVariants.put(nameUMC, listAllVariants);
    }

    private RadboudVariant createRadboudVariant(String line, int lineCount) {
        RadboudVariant RadboudVariant = new RadboudVariant();
        String[] columns = line.split("\t");
        RadboudVariant.setChromosome(columns[0]);
        RadboudVariant.setStart(Integer.parseInt(columns[1]));
        RadboudVariant.setStop(Integer.parseInt(columns[2]));
        RadboudVariant.setREF(columns[3]);
        RadboudVariant.setALT(columns[4]);
        RadboudVariant.setGeneName(columns[5]);
        RadboudVariant.setcDNANotation(columns[6]);
        RadboudVariant.setTranscript(columns[7]);
        RadboudVariant.setProteinNotation(columns[8]);
        RadboudVariant.setExon(columns[11]);
        RadboudVariant.setClassification(columns[13]);
        RadboudVariant.setVariantType(RadboudVariant.getcDNANotation());
        RadboudVariant.setIdentifier();
        RadboudVariant.setRawInformation(line);
        RadboudVariant.setLineNumber(lineCount);
        return RadboudVariant;
    }

    private HGVSVariant createHGVSVariant(String line, int lineCount) {
        HGVSVariant HGVSVariant = new HGVSVariant();
        String[] columns = line.split("\t");
        HGVSVariant.setReferenceSequence(columns[0]);
        HGVSVariant.setChromosome(columns[1]);
        HGVSVariant.setGenomicDNA(columns[2]);
        HGVSVariant.setGenomicDNANormalized(columns[3]);
        HGVSVariant.setClassification(columns[4]);
        HGVSVariant.setGeneName(columns[5]);
        HGVSVariant.setcDNANotation(columns[6]);
        HGVSVariant.setProteinNotation(columns[7]);
        HGVSVariant.setVariantType(HGVSVariant.getGenomicDNA());
        HGVSVariant.setRawInformation(line);
        HGVSVariant.setLineNumber(lineCount);
        return HGVSVariant;
    }

    private CartageniaVariant createCartageniaVariant(String line, int lineCount) {
        CartageniaVariant cartageniaVariant = new CartageniaVariant();
        String[] columns = line.split("\t");
        cartageniaVariant.setTimestamp(columns[0]);
        cartageniaVariant.setId(columns[1]);
        cartageniaVariant.setChromosome(columns[2]);
        cartageniaVariant.setStart(Integer.parseInt(columns[3]));
        cartageniaVariant.setStop(Integer.parseInt(columns[4]));
        cartageniaVariant.setREF(columns[5]);
        cartageniaVariant.setALT(columns[6]);
        cartageniaVariant.setGeneName(columns[7]);
        cartageniaVariant.setTranscript(columns[8]);
        cartageniaVariant.setcDNANotation(columns[9]);
        cartageniaVariant.setProteinNotation(columns[10]);
        cartageniaVariant.setExon(columns[11]);
        cartageniaVariant.setVariantType(columns[12]);
        cartageniaVariant.setLocation(columns[13]);
        cartageniaVariant.setEffect(columns[14]);
        cartageniaVariant.setClassification(columns[15]);
        cartageniaVariant.setLastUpdatedOn(columns[16]);
        cartageniaVariant.setRawInformation(line);
        cartageniaVariant.setLineNumber(lineCount);
        return cartageniaVariant;
    }

    public Map<String, ArrayList<RadboudVariant>> getRadboudVariants() {
        return radboudVariants;
    }

    public Map<String, ArrayList<HGVSVariant>> getHGVSVariants() {
        return HGVSVariants;
    }

    public Map<String, ArrayList<CartageniaVariant>> getCartageniaVariants() {
        return cartageniaVariants;
    }

    public Map<String, ArrayList<Variant>> getAllVariants() {
        return allVariants;
    }
}
