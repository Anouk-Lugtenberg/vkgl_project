package org.molgenis.vkgl.IO;

import org.molgenis.vkgl.model.VCFVariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;

public class VCFFileProcessor {
    public ArrayList<VCFVariant> processVCFFile(File file) {
        ArrayList<VCFVariant> VCFVariants = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                VCFVariants.add(createVCFSingleLineModel(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return VCFVariants;
    }

    private VCFVariant createVCFSingleLineModel(String line) {
        VCFVariant VCFVariant = new VCFVariant();
        String[] items = line.split("\t");
        VCFVariant.setChromosome(items[0]);
        VCFVariant.setStart(Integer.parseInt(items[1]));
        VCFVariant.setStop(Integer.parseInt(items[2]));
        VCFVariant.setREF(items[3]);
        VCFVariant.setALT(items[4]);
        VCFVariant.setGeneName(items[5]);
        VCFVariant.setcDNANotation(items[6]);
        VCFVariant.setTranscript(items[7]);
        VCFVariant.setProteinNotation(items[8]);
        VCFVariant.setExon(items[11]);
        VCFVariant.setClassification(items[13]);
        return VCFVariant;
    }
}
