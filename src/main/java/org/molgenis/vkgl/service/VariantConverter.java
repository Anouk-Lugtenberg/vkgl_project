package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.IO.RawDataReader;
import org.molgenis.vkgl.model.CartageniaVariant;
import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.RadboudVariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

public class VariantConverter {
    private static Logger LOGGER = LogManager.getLogger(VariantConverter.class.getName());
    private int notClassified = 0;
    private int SNPCount = 0;
    private int insertionCount = 0;
    private int deletionCount = 0;
    private int duplicationCount = 0;

    public void convertVariants(RawDataReader rawData) {
        Map<String, ArrayList<HGVSVariant>> HGVSVariants = rawData.getHGVSVariants();
        Map<String, ArrayList<RadboudVariant>> VCFVariants = rawData.getVCFVariants();
        Map<String, ArrayList<CartageniaVariant>> CartageniaVariants = rawData.getCartageniaVariants();

        File outputDirectory = CLIParser.getOutputDirectory();
        LOGGER.info("outputDirectory: " + outputDirectory);

//        HGVSVariantConverter HGVSVariantConverter = new HGVSVariantConverter();

        for (Map.Entry<String, ArrayList<HGVSVariant>> entry : HGVSVariants.entrySet()) {
            ArrayList<HGVSVariant> variants = entry.getValue();
            for (HGVSVariant variant : variants) {
                createUniqueIdentifier(variant);
//                LOGGER.info("Checking HGVS Syntax for: " + variant.getGenomicDNA());
//                checkHGVSSyntax(variant.getGenomicDNA());
//                System.out.println("variant.getVariantType() = " + variant.getVariantType());
            }
        }

        for (Map.Entry<String, ArrayList<RadboudVariant>> entry : VCFVariants.entrySet()) {
//            System.out.println("Determining SNPs/indels/deletion for VCF VARIANT: " + entry.getKey());
            ArrayList<RadboudVariant> stuff = entry.getValue();
            for (RadboudVariant variant : stuff) {
//                System.out.println("variant.getVariantType() = " + variant.getVariantType());
            }
        }

        for (Map.Entry<String, ArrayList<CartageniaVariant>> entry : CartageniaVariants.entrySet()) {
//            System.out.println("Determining SNPs/indels/deletion for Cartagenia VARIANT: " + entry.getKey());
            ArrayList<CartageniaVariant> stuff = entry.getValue();
            for (CartageniaVariant variant : stuff) {
//                System.out.println("variant.getVariantType() = " + variant.getVariantType());
            }
        }
    }

    private boolean checkHGVSSyntax(String cDNANotation) {
        String URLString = "https://mutalyzer.nl/json/checkSyntax?variant=" + cDNANotation;
        try {
            URL urlObject = new URL(URLString);
            URLConnection urlConnection = urlObject.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject json = new JSONObject(line);
                if (!(boolean) json.get("valid")) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void createUniqueIdentifier(HGVSVariant variant) {

    }
}
