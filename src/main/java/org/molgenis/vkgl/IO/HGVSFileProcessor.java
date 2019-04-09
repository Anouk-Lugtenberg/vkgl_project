package org.molgenis.vkgl.IO;

import org.molgenis.vkgl.model.HGVSVariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HGVSFileProcessor {
    public ArrayList<HGVSVariant> processHGVSFile(File file) {
        ArrayList<HGVSVariant> HGVSVariants = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                HGVSVariants.add(createHGVSModelSingleLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HGVSVariants;
    }

    private HGVSVariant createHGVSModelSingleLine(String line) {
        HGVSVariant HGVSvariant = new HGVSVariant();
        String[] items = line.split("\t");
        HGVSvariant.setReferenceSequence(items[0]);
        HGVSvariant.setChromosome(items[1]);
        HGVSvariant.setGenomicDNA(items[2]);
        HGVSvariant.setGenomicDNANormalized(items[3]);
        HGVSvariant.setClassification(items[4]);
        HGVSvariant.setGeneName(items[5]);
        HGVSvariant.setcDNANotation(items[6]);
        HGVSvariant.setProteinNotation(items[7]);
        return HGVSvariant;

    }
}
