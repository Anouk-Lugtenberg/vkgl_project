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
import java.util.List;
import java.util.Map;

public class VariantToVCFConverter {
    private static Logger LOGGER = LogManager.getLogger(VariantToVCFConverter.class.getName());
    private Map<String, ArrayList<VCFVariant>> VCFVariantsPerUMC = new HashMap<>();
    private BioCommonsHelper bioCommonsHelper = new BioCommonsHelper();
    private Map<String, Integer> countVariantsBeforeValidation = new HashMap<>();

    public void convertVariants(VariantParser variants, String outputDirectory) {
        convertRadboudVariants(variants.getRadboudVariants());
        convertCartageniaVariants(variants.getCartageniaVariants());
        convertHGVSVariants(variants.getHGVSVariants());
        //removes invalid variant from checks within converters
        removeInvalidVariants();
        checkVariantsWithBioCommons();
        //removes invalid variants from check with biocommons
        removeInvalidVariants();
        sortVariants();
        removeDoubleVCFVariants();
        logNumberOfValidVariants();
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
            countVariantsBeforeValidation.put(nameUMC, radboudVariants.size());
            LOGGER.info("Number of variants for UMC: {} is {} before starting validation process", nameUMC, radboudVariants.size());
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (RadboudVariant variant : radboudVariants) {
                RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(variant);
                VCFVariant vcfVariant = radboudToVCFConverter.convertToVCF();
                vcfVariant.setDnaNotation(checkDNANotation(variant.getcDNANotation()));
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
            countVariantsBeforeValidation.put(nameUMC, cartageniaVariants.size());
            LOGGER.info("Number of variants for UMC: {} is {} before starting validation process", nameUMC, cartageniaVariants.size());
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (CartageniaVariant variant : cartageniaVariants) {
                RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(variant);
                VCFVariant vcfVariant = radboudToVCFConverter.convertToVCF();
                vcfVariant.setDnaNotation(variant.getTranscript() + ":" + variant.getcDNANotation());
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
            countVariantsBeforeValidation.put(nameUMC, HGVSVariants.size());
            LOGGER.info("Number of variants for UMC: {} is {} before starting validation process", nameUMC, HGVSVariants.size());
            ArrayList<VCFVariant> vcfVariants = new ArrayList<>();
            for (HGVSVariant variant : HGVSVariants) {
                HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(variant);
                VCFVariant vcfVariant = hgvsToVCFConverter.convertToVCF();
                String[] cDNANotations = variant.getcDNANotation().split(",");
                vcfVariant.setDnaNotation(cDNANotations[0]);
                vcfVariants.add(vcfVariant);
            }
            addToVCFList(nameUMC, vcfVariants);
        }
    }

    /**
     * Replaces ',' with '.' and '::' with ':' (errors mostly found in Radboud data).
     * @param dnaNotation the dnaNotation to be checked
     * @return a String containing the dna notation with the replacements
     */
    private String checkDNANotation(String dnaNotation) {
            return dnaNotation.replace(",", ".").replace("::", ":");
    }

    /**
     * Checks the variants with the variants created by BioCommons
     */
    private void checkVariantsWithBioCommons() {
        for (Map.Entry<String, ArrayList<VCFVariant>> entry : VCFVariantsPerUMC.entrySet()) {
            String nameUMC = entry.getKey();
            ArrayList<VCFVariant> vcfVariantWithoutAnchor = new ArrayList<>();
            ArrayList<VCFVariant> vcfVariantsOtherTypes = new ArrayList<>();
            LOGGER.info("Checking created VCF Variants with BioCommons for {}", nameUMC);
            ArrayList<VCFVariant> vcfVariants = entry.getValue();
            for (VCFVariant vcfVariant : vcfVariants) {
                if (vcfVariant.getRawVariant().getVariantType() == VariantType.SNP ||
                    vcfVariant.getRawVariant().getVariantType() == VariantType.DELETION_INSERTION) {
                    vcfVariantWithoutAnchor.add(vcfVariant);
                } else {
                    vcfVariantsOtherTypes.add(vcfVariant);
                }
            }

            List<BioCommonsVCFVariant> bioCommonsVCFVariantListWithoutAnchor = bioCommonsHelper.getBioCommonsVariants(vcfVariantWithoutAnchor, true);
            List<BioCommonsVCFVariant> bioCommonsVCFVariantListOtherTypes = bioCommonsHelper.getBioCommonsVariants(vcfVariantsOtherTypes, false);

            //Compares the variants based on their position in the lists.
            for (int i = 0; i < vcfVariantWithoutAnchor.size(); i++) {
                compareVariants(bioCommonsVCFVariantListWithoutAnchor.get(i), vcfVariantWithoutAnchor.get(i));
            }
            for (int i = 0; i < vcfVariantsOtherTypes.size(); i++) {
                compareVariants(bioCommonsVCFVariantListOtherTypes.get(i), vcfVariantsOtherTypes.get(i));
            }
        }
    }

    /**
     * Compares the two variants. If they differ or an error is found, variant is flagged as invalid.
     * @param bioCommonsVCFVariant the created bio commons variant
     * @param vcfVariant the created vcf variant
     */
    private void compareVariants(BioCommonsVCFVariant bioCommonsVCFVariant, VCFVariant vcfVariant) {
        if (bioCommonsVCFVariant.getError() == null) {
            if (!sameVariant(bioCommonsVCFVariant, vcfVariant)) {
                LOGGER.info("{}: {}", vcfVariant.getRawVariant().getLineNumber(), vcfVariant.getRawVariant().getRawInformation());
                LOGGER.info("VCF variant created is not the same as the one created with bio commons");
                LOGGER.info("VCF variant:\t chrom: {},\tpos: {},\tref: {},\talt: {}", vcfVariant.getChromosome(),
                        vcfVariant.getPosition(), vcfVariant.getREF(), vcfVariant.getALT() );
                LOGGER.info("BioCommons variant:\t chrom: {},\tpos: {},\tref: {},\talt: {}\n", bioCommonsVCFVariant.getChrom(),
                        bioCommonsVCFVariant.getPos(), bioCommonsVCFVariant.getRef(), bioCommonsVCFVariant.getAlt());
                VariantErrorCounter.bioCommonsVariantNotTheSameAsVCFVariant();
                vcfVariant.setValidVariant(false);
            }
        } else {
            LOGGER.info("{}: {}", vcfVariant.getRawVariant().getLineNumber(), vcfVariant.getRawVariant().getRawInformation());
            LOGGER.info("BioCommons raised an error while creating a variant. Error: {}\n", bioCommonsVCFVariant.getError());
            VariantErrorCounter.addBioCommonsError(bioCommonsVCFVariant.getError());
            vcfVariant.setValidVariant(false);
        }
    }

    /**
     * Checks if four fields are the same (alt, ref, chrom and pos)
     * @param bioCommonsVCFVariant a bio commons variant
     * @param vcfVariant a vcf variant
     * @return true if the four fields are the same, false if one or more differ
     */
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

    private void removeDoubleVCFVariants() {
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

    private void logNumberOfValidVariants() {
        for (Map.Entry<String, ArrayList<VCFVariant>> entry : VCFVariantsPerUMC.entrySet()) {
            String nameUMC = entry.getKey();
            int variantsBeforeValidation = countVariantsBeforeValidation.get(nameUMC);
            int validVariants = entry.getValue().size();
            LOGGER.info("{} valid variants for UMC: {}. Started with {} variants. Total errors: {}", validVariants, nameUMC, variantsBeforeValidation, variantsBeforeValidation - validVariants);
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
