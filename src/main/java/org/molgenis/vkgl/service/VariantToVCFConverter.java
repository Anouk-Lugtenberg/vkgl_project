package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.molgenis.vkgl.IO.VariantParser;
import org.molgenis.vkgl.IO.VariantWriter;
import org.molgenis.vkgl.model.CartageniaVariant;
import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.RadboudVariant;
import org.molgenis.vkgl.model.VCFVariant;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VariantToVCFConverter {
    private static Logger LOGGER = LogManager.getLogger(VariantToVCFConverter.class.getName());
    private Map<String, ArrayList<VCFVariant>> VCFVariantsPerUMC = new HashMap<>();

    public void convertVariants(VariantParser variants, String outputDirectory) {
        convertRadboudVariants(variants.getRadboudVariants());
        convertCartageniaVariants(variants.getCartageniaVariants());
        convertHGVSVariants(variants.getHGVSVariants());
        validateVariants();
        writeVCFVariantsToFile(outputDirectory);
    }

    private void convertRadboudVariants(Map<String, ArrayList<RadboudVariant>> radboudVariantsPerUMC) {
        for (Map.Entry<String, ArrayList<RadboudVariant>> entry : radboudVariantsPerUMC.entrySet()) {
            String nameUMC = entry.getKey();
            ArrayList<RadboudVariant> radboudVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (RadboudVariant variant : radboudVariants) {
                if (variant.getChromosome().equals("1")) {
                    RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(variant);
                    vcfVariants.add(radboudToVCFConverter.convertToVCF());
                }
            }
            addToVCFList(nameUMC, vcfVariants);
        }
    }

    private void convertCartageniaVariants(Map<String, ArrayList<CartageniaVariant>> cartageniaVariantsPerUMC) {
        for (Map.Entry<String, ArrayList<CartageniaVariant>> entry : cartageniaVariantsPerUMC.entrySet()) {
            String nameUMC = entry.getKey();
            ArrayList<CartageniaVariant> cartageniaVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (CartageniaVariant variant : cartageniaVariants) {
                if (variant.getChromosome().equals("1")) {
                    RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(variant);
                    vcfVariants.add(radboudToVCFConverter.convertToVCF());
                }
            }
            addToVCFList(nameUMC, vcfVariants);
        }
    }

    private void convertHGVSVariants(Map<String, ArrayList<HGVSVariant>> HGVSVariantsPerUMC) {
        for (Map.Entry<String, ArrayList<HGVSVariant>> entry : HGVSVariantsPerUMC.entrySet()) {
            String nameUMC = entry.getKey();
            ArrayList<HGVSVariant> HGVSVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (HGVSVariant variant : HGVSVariants) {
                if (variant.getChromosome().equals("1")) {
                    HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(variant);
                    vcfVariants.add(hgvsToVCFConverter.convertToVCF());
                }
            }
            addToVCFList(nameUMC, vcfVariants);
        }
    }

    private void addToVCFList(String nameUMC, ArrayList<VCFVariant> vcfVariants) {
        VCFVariantsPerUMC.put(nameUMC, vcfVariants);
    }

    private void validateVariants() {
        VariantValidator variantValidator = new VariantValidator(VCFVariantsPerUMC);
        variantValidator.startValidating();
    }

    private void writeVCFVariantsToFile(String outputDirectory) {
        try {
            VariantWriter variantWriter = new VariantWriter(outputDirectory);
            variantWriter.writeVCFVariantsToFile(VCFVariantsPerUMC);
        } catch (IOException e) {
            LOGGER.error("Something went wrong while writing to: " + outputDirectory);
            e.printStackTrace();
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
}
