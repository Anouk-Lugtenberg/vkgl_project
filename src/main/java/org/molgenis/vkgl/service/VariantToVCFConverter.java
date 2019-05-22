package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.IO.VariantParser;
import org.molgenis.vkgl.IO.VariantWriter;
import org.molgenis.vkgl.model.variants.CartageniaVariant;
import org.molgenis.vkgl.model.variants.HGVSVariant;
import org.molgenis.vkgl.model.variants.RadboudVariant;
import org.molgenis.vkgl.model.variants.VCFVariant;

import java.io.*;
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
        removeInvalidVariants();
        sortVariants();
        validateVariants();
        writeVCFVariantsToFile(outputDirectory);
    }

    /**
     * Converts the radboud variants to VCF variants.
     * @param radboudVariantsPerUMC a Map<String, ArrayList<RadboudVariant>> which contains all the variants flagged as
     *                              Radboud variants.
     */
    private void convertRadboudVariants(Map<String, ArrayList<RadboudVariant>> radboudVariantsPerUMC) {
        for (Map.Entry<String, ArrayList<RadboudVariant>> entry : radboudVariantsPerUMC.entrySet()) {
            String nameUMC = entry.getKey();
            LOGGER.info("Converting variants from UMC: " + nameUMC + " to VCFVariants.");
            ArrayList<RadboudVariant> radboudVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (RadboudVariant variant : radboudVariants) {
                //TODO for now only the variants which have chromosome 1 are processed.
                if (variant.getChromosome().equals("1")) {
                    RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(variant);
                    vcfVariants.add(radboudToVCFConverter.convertToVCF());
                }
            }
            addToVCFList(nameUMC, vcfVariants);
        }
    }

    /**
     * Converts the Cartagenia variants to VCF Variants.
     * @param cartageniaVariantsPerUMC a Map<String, ArrayList<CartageniaVariant> which contains all the variants flagged
     *                                 as Cartagenia variants.
     */
    private void convertCartageniaVariants(Map<String, ArrayList<CartageniaVariant>> cartageniaVariantsPerUMC) {
        for (Map.Entry<String, ArrayList<CartageniaVariant>> entry : cartageniaVariantsPerUMC.entrySet()) {
            String nameUMC = entry.getKey();
            LOGGER.info("Converting variants from UMC: " + nameUMC + " to VCFVariants.");
            ArrayList<CartageniaVariant> cartageniaVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (CartageniaVariant variant : cartageniaVariants) {
                //TODO for now only the variants which have chromosome 1 are processed.
                if (variant.getChromosome().equals("1")) {
                    RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(variant);
                    vcfVariants.add(radboudToVCFConverter.convertToVCF());
                }
            }
            addToVCFList(nameUMC, vcfVariants);
        }
    }

    /**
     * Converts HGVS Variants to VCF Variants.
     * @param HGVSVariantsPerUMC a Map<String, ArrayList<HGVSVariant> which contains all the variants flagged as
     *                           HGVS variants.
     */
    private void convertHGVSVariants(Map<String, ArrayList<HGVSVariant>> HGVSVariantsPerUMC) {
        for (Map.Entry<String, ArrayList<HGVSVariant>> entry : HGVSVariantsPerUMC.entrySet()) {
            String nameUMC = entry.getKey();
            LOGGER.info("Converting variants from UMC: " + nameUMC + " to VCFVariants.");
            ArrayList<HGVSVariant> HGVSVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (HGVSVariant variant : HGVSVariants) {
                //TODO for now only the variants which have chromosome 1 are processed.
                if (variant.getChromosome().equals("1")) {
                    HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(variant);
                    vcfVariants.add(hgvsToVCFConverter.convertToVCF());
                }
            }
            addToVCFList(nameUMC, vcfVariants);
        }
    }

    /**
     * Adds the variants and from which UMC they are to a list containing all the variants.
     * @param nameUMC name of the UMC
     * @param vcfVariants ArrayList containing VCF variants
     */
    private void addToVCFList(String nameUMC, ArrayList<VCFVariant> vcfVariants) {
        VCFVariantsPerUMC.put(nameUMC, vcfVariants);
    }

    private void validateVariants() {
        VariantValidator variantValidator = new VariantValidator(VCFVariantsPerUMC);
        variantValidator.checkDoubleVCFNotations();
        removeInvalidVariants();
    }

    /**
     * Filters the variants based on their isValidVariant parameter.
     */
    private void removeInvalidVariants() {
        for (Map.Entry<String, ArrayList<VCFVariant>> entry : VCFVariantsPerUMC.entrySet()) {
            VCFVariantsPerUMC.get(entry.getKey()).removeIf(variant -> (!variant.isValidVariant()));
        }
    }

    /**
     * Sorts the variants based on chromosome number and start position.
     */
    private void sortVariants() {
        for (Map.Entry<String, ArrayList<VCFVariant>> entry : VCFVariantsPerUMC.entrySet()) {
            entry.getValue().sort(VCFVariant.Comparators.CHROMOSOME_AND_POSITION);
        }
    }

    /**
     * Starts the variant writer to write variants to a file.
     * @param outputDirectory directory where the files will be put.
     */
    private void writeVCFVariantsToFile(String outputDirectory) {
        try {
            VariantWriter variantWriter = new VariantWriter(outputDirectory);
            variantWriter.writeVCFVariantsToFile(VCFVariantsPerUMC);
        } catch (IOException e) {
            LOGGER.error("Something went wrong while writing to: " + outputDirectory);
            e.printStackTrace();
        }
    }
}
