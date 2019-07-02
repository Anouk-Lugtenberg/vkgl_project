package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.IO.VariantParser;
import org.molgenis.vkgl.IO.VariantWriter;
import org.molgenis.vkgl.biocommons.BioCommonsHelper;
import org.molgenis.vkgl.biocommons.BioCommonsVCFVariant;
import org.molgenis.vkgl.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VariantToVCFConverter {
    private static Logger LOGGER = LogManager.getLogger(VariantToVCFConverter.class.getName());
    private Map<String, ArrayList<VCFVariant>> VCFVariantsPerUMC = new HashMap<>();
    private BioCommonsHelper bioCommonsHelper = new BioCommonsHelper();

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
            LOGGER.info("Converting variants from UMC: {} to VCFVariants.", nameUMC);
            ArrayList<RadboudVariant> radboudVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (RadboudVariant variant : radboudVariants) {
                RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(variant);
                VCFVariant vcfVariant = radboudToVCFConverter.convertToVCF();
                checkVariantWithBioCommons(variant.getcDNANotation(), variant, vcfVariant);
                vcfVariants.add(vcfVariant);
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
            LOGGER.info("Converting variants from UMC: {} to VCFVariants.", nameUMC);
            ArrayList<CartageniaVariant> cartageniaVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (CartageniaVariant variant : cartageniaVariants) {
                RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(variant);
                VCFVariant vcfVariant = radboudToVCFConverter.convertToVCF();
                String cDNANotation = variant.getTranscript() + ":" + variant.getcDNANotation();
                checkVariantWithBioCommons(cDNANotation, variant, vcfVariant);
                vcfVariants.add(vcfVariant);
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
            LOGGER.info("Converting variants from UMC: {} to VCFVariants.", nameUMC);
            ArrayList<HGVSVariant> HGVSVariants = entry.getValue();
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (HGVSVariant variant : HGVSVariants) {
                HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(variant);
                VCFVariant vcfVariant = hgvsToVCFConverter.convertToVCF();
                checkVariantWithBioCommons(variant.getGenomicDNA(), variant, vcfVariant);
                vcfVariants.add(vcfVariant);
            }
            addToVCFList(nameUMC, vcfVariants);
        }
    }

    private void checkVariantWithBioCommons(String dnaNotation, Variant variant, VCFVariant vcfVariant) {
        BioCommonsVCFVariant bioCommonsVCFVariant;
        if (variant.getVariantType() == VariantType.SNP) {
            bioCommonsVCFVariant = bioCommonsHelper.postDNANotation(dnaNotation, true);
        } else {
            bioCommonsVCFVariant = bioCommonsHelper.postDNANotation(dnaNotation, false);
        }

        if (bioCommonsVCFVariant.getError() == null) {
            if (!sameVariant(bioCommonsVCFVariant, vcfVariant)) {
                LOGGER.info("{}: {}", variant.getLineNumber(), variant.getRawInformation());
                LOGGER.info("VCF variant created is not the same as the one created with bio commons");
                LOGGER.info("VCF variant:\t chrom: {},\tpos: {},\tref: {},\talt: {}", vcfVariant.getChromosome(), vcfVariant.getPosition(), vcfVariant.getREF(), vcfVariant.getALT() );
                LOGGER.info("BioCommons variant:\t chrom: {},\tpos: {},\tref: {},\talt: {}\n", bioCommonsVCFVariant.getChrom(), bioCommonsVCFVariant.getPos(), bioCommonsVCFVariant.getRef(), bioCommonsVCFVariant.getAlt());
            }
        } else {
            LOGGER.info("{}: {}", variant.getLineNumber(), variant.getRawInformation());
            LOGGER.info("BioCommons raised an error while creating a variant");
            LOGGER.info("Error: {}\n", bioCommonsVCFVariant.getError());
        }
    }

    boolean sameVariant(BioCommonsVCFVariant bioCommonsVCFVariant, VCFVariant vcfVariant) {
        return bioCommonsVCFVariant.getAlt().equals(vcfVariant.getALT()) &&
                bioCommonsVCFVariant.getRef().equals(vcfVariant.getREF()) &&
                bioCommonsVCFVariant.getChrom().equals(vcfVariant.getChromosome()) &&
                bioCommonsVCFVariant.getPos() == vcfVariant.getPosition();
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
            LOGGER.error("Something went wrong while writing to: {}", outputDirectory);
            e.printStackTrace();
        }
    }
}
