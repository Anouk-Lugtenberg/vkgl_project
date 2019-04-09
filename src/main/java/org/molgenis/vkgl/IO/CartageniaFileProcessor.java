package org.molgenis.vkgl.IO;

import org.molgenis.vkgl.model.CartageniaVariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CartageniaFileProcessor {
    public ArrayList<CartageniaVariant> processFile(File file) {
        ArrayList<CartageniaVariant> cartageniaVariants = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                cartageniaVariants.add(createCartageniaSingleLineModel(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cartageniaVariants;
    }

    private CartageniaVariant createCartageniaSingleLineModel(String line) {
        CartageniaVariant cartageniaVariant = new CartageniaVariant();
        String[] items = line.split("\t");
        cartageniaVariant.setTimestamp(items[0]);
        cartageniaVariant.setId(items[1]);
        cartageniaVariant.setChromosome(items[2]);
        cartageniaVariant.setStart(Integer.parseInt(items[3]));
        cartageniaVariant.setStop(Integer.parseInt(items[4]));
        cartageniaVariant.setREF(items[5]);
        cartageniaVariant.setALT(items[6]);
        cartageniaVariant.setGeneName(items[7]);
        cartageniaVariant.setTranscript(items[8]);
        cartageniaVariant.setcDNANotation(items[9]);
        cartageniaVariant.setProteinNotation(items[10]);
        cartageniaVariant.setExon(items[11]);
        cartageniaVariant.setVariantType(items[12]);
        cartageniaVariant.setLocation(items[13]);
        cartageniaVariant.setEffect(items[14]);
        cartageniaVariant.setClassification(items[15]);
        cartageniaVariant.setLastUpdatedOn(items[16]);
        return cartageniaVariant;
    }
}
