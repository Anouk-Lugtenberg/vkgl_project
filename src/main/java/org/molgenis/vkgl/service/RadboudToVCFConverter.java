package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.RadboudVariant;
import org.molgenis.vkgl.model.VCFVariant;

public class RadboudToVCFConverter implements VCFConverter {
    private RadboudVariant radboudVariant;
    private VCFVariant VCFVariant;
    private String chromosome;
    private int start;
    private int stop;
    private String REF;
    private String ALT;
    private ClassificationType classification;

    public RadboudToVCFConverter(RadboudVariant radboudVariant) {
        this.radboudVariant = radboudVariant;
        this.chromosome = radboudVariant.getChromosome();
        this.start = radboudVariant.getStart();
        this.stop = radboudVariant.getStop();
        this.REF = radboudVariant.getREF();
        this.ALT = radboudVariant.getALT();
        this.classification = radboudVariant.getClassification();
    }

    @Override
    public VCFVariant convertToVCF() {
        switch (radboudVariant.getVariantType()) {
            case SNP:
                VCFVariant = convertSNP();
                break;
            case INSERTION:
                VCFVariant = convertInsertion();
                break;
            case DELETION:
                VCFVariant = convertDeletion();
                break;
            case DUPLICATION:
                VCFVariant = convertDuplication();
                break;
            case DELETION_INSERTION:
                VCFVariant = convertDeletionInsertion();
                break;
            case NOT_CLASSIFIED:
                VCFVariant = convertNotClassified();
                break;
        }
        return VCFVariant;
    }

    @Override
    public VCFVariant convertSNP() {
        boolean isValidVariant;
        try {
            String referenceGenomeBuild = VCFConverter.getBasesFromPosition("chr1", start, stop);
            isValidVariant = VCFConverter.validateSNP(referenceGenomeBuild, REF, ALT, radboudVariant);
        } catch (IllegalArgumentException e) {
            isValidVariant = false;
        }
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, classification, radboudVariant);
        vcfVariant.setValidVariant(isValidVariant);
        return vcfVariant;
    }

    @Override
    public VCFVariant convertInsertion() {
        String referenceGenomeBuild = VCFConverter.getBasesFromPosition("chr1", start, stop);
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, referenceGenomeBuild, newALT, classification, radboudVariant);
        vcfVariant.setValidVariant(validateInsertion());
        return vcfVariant;
    }

    //TODO: Validate variants with type insertion.
    private boolean validateInsertion() {
        return true;
    }

    @Override
    public VCFVariant convertDeletion() {
        //in VCF the REF with deletions is one position BEFORE the actual deletion.
        int startPosition = start -1;

        String newALT = VCFConverter.getBasesFromPosition("chr1", startPosition, startPosition);
        String newREF = VCFConverter.getBasesFromPosition("chr1", startPosition, stop);
        VCFVariant vcfVariant = new VCFVariant(chromosome, startPosition, newREF, newALT, classification, radboudVariant);
        vcfVariant.setValidVariant(validateDeletion());
        return vcfVariant;
    }

    private boolean validateDeletion() {
        boolean deletionValid = true;
        if (REF.length() > 0) {
            String GRChREF = VCFConverter.getBasesFromPosition("chr1", start, stop);
            if (!GRChREF.equals(REF)) {
                LOGGER.info(radboudVariant.getLineNumber() + ": " + radboudVariant.getRawInformation());
                LOGGER.info("Reference genome: " + GRChREF + " does not equal reference given for variant:" + REF + ". Flagging as invalid.\n");
                deletionValid = false;
            }
        }
        return deletionValid;
    }

    @Override
    public VCFVariant convertDuplication() {
        //Duplication is the same conversion as for an insertion for Radboud variants.
        return convertInsertion();
    }

    @Override
    public VCFVariant convertDeletionInsertion() {
        boolean validVariant;
        try {
            String GRChREF = VCFConverter.getBasesFromPosition("chr1", start, stop);
            validVariant = validateDeletionInsertion(GRChREF);
        } catch (IllegalArgumentException e) {
            validVariant = false;
        }
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, classification, radboudVariant);
        vcfVariant.setValidVariant(validVariant);
        return vcfVariant;
    }

    private boolean validateDeletionInsertion(String GRChREF) {
        boolean validVariant;
        if (REF.length() == 0) {
            LOGGER.info(radboudVariant.getLineNumber() + ": " + radboudVariant.getRawInformation());
            LOGGER.info("No REF available for delins. Flagging as invalid.\n");
            validVariant = false;
        } else if (ALT.length() == 0) {
            LOGGER.info(radboudVariant.getLineNumber() + ": " + radboudVariant.getRawInformation());
            LOGGER.info("No ALT available for delins. Flagging as invalid.\n");
            validVariant = false;
        } else if (!REF.equals(GRChREF)) {
            LOGGER.info(radboudVariant.getLineNumber() + ": " + radboudVariant.getRawInformation());
            LOGGER.info("Reference genome: " + GRChREF + " does not equal reference given for variant: " + REF +". Flagging as invalid.\n");
            validVariant = false;
        } else {
            validVariant = true;
        }
        return validVariant;
    }

    @Override
    public VCFVariant convertNotClassified() {
        LOGGER.info(radboudVariant.getLineNumber() + ": " + radboudVariant.getRawInformation());
        LOGGER.info("Variant could not be classified. Flagging as invalid.\n");
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, classification, radboudVariant);
        vcfVariant.setValidVariant(false);
        return vcfVariant;
    }
}
