package org.molgenis.vkgl.IO;

import org.molgenis.vkgl.model.CartageniaVariant;
import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.RadboudVariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class VariantParser {
    ArrayList<RadboudVariant> parseRadboud(File file) {
        ArrayList<RadboudVariant> RadboudVariants = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                RadboudVariants.add(createRadboudVariant(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return RadboudVariants;
    }

    private RadboudVariant createRadboudVariant(String line) {
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
        return RadboudVariant;
    }

    ArrayList<HGVSVariant> parseHGVS(File file) {
        ArrayList<HGVSVariant> HGVSVariants = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                HGVSVariants.add(createHGVSVariant(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HGVSVariants;
    }

    private HGVSVariant createHGVSVariant(String line) {
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
        HGVSVariant.setVariantType();
        return HGVSVariant;
    }

    ArrayList<CartageniaVariant> parseCartagenia(File file) {
        ArrayList<CartageniaVariant> CartageniaVariants = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                CartageniaVariants.add(createCartageniaVariant(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CartageniaVariants;
    }

    private CartageniaVariant createCartageniaVariant(String line) {
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
        return cartageniaVariant;
    }
}
