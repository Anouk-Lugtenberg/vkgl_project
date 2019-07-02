package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.PositionAndNucleotides;
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
            String referenceGenomeBuild = VCFConverter.getBasesFromPosition(chromosome, start, stop);
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
        int position;
        if (ALT.length() == 1) {
            position = VCFConverter.moveNucleotidesMostLeftPosition(ALT, start, chromosome);
        } else if (ALT.chars().allMatch(c -> c == ALT.charAt(0))) {
            //Substring from first base of ALT, because all the nucleotides are the same in the ALT.
            position = VCFConverter.moveNucleotidesMostLeftPosition(ALT.substring(0, 1), start, chromosome);
        } else {
            PositionAndNucleotides positionAndAlternative = VCFConverter.moveDifferentNucleotidesMostLeftPosition(start, ALT, chromosome);
            position = positionAndAlternative.getPosition();
            ALT = positionAndAlternative.getNucleotides();
        }

//        if (position != start) {
//            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
//            LOGGER.info("Variant could be placed more to the left, position changed from {} to {}", start, position);
//        }

        String referenceGenomeBuild = VCFConverter.getBasesFromPosition(chromosome, position, position);
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, referenceGenomeBuild, newALT, classification, radboudVariant);
        vcfVariant.setValidVariant(validateInsertion());
        return vcfVariant;
    }

    //TODO: Validate variants with type insertion.
    private boolean validateInsertion() {
        return true;
    }

    @Override
    public VCFVariant convertDeletion() {
        PositionAndNucleotides positionAndNucleotides = VCFConverter.moveDeletionMostLeftPosition(chromosome, start, stop);
        int position = positionAndNucleotides.getPosition();

//        if (position != start) {
//            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
//            LOGGER.info("Variant could be placed more to the left, position changed from {} to {}\n", start, position);
//        }

        String referenceGenomeBuild = VCFConverter.getBasesFromPosition(chromosome, position, position);
        String newREF = referenceGenomeBuild + positionAndNucleotides.getNucleotides();
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, newREF, referenceGenomeBuild, classification, radboudVariant);
        vcfVariant.setValidVariant(validateREFDeletion());
        return vcfVariant;
    }

    private boolean validateREFDeletion() {
        boolean referenceValid = true;
        if (REF.length() > 0) {
            String GRChREF = VCFConverter.getBasesFromPosition(chromosome, start, stop);
            if (!GRChREF.equals(REF)) {
                LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
                LOGGER.info("Reference genome: {} does not equal reference given for variant: {}. Flagging as invalid.\n", GRChREF, REF);
                referenceValid = false;
            }
        }
        return referenceValid;
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
            String GRChREF = VCFConverter.getBasesFromPosition(chromosome, start, stop);
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
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("No REF available for delins. Flagging as invalid.\n");
            validVariant = false;
        } else if (ALT.length() == 0) {
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("No ALT available for delins. Flagging as invalid.\n");
            validVariant = false;
        } else if (!REF.equals(GRChREF)) {
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Reference genome: {} does not equal reference given for variant: {}. Flagging as invalid.\n", GRChREF, REF);
            validVariant = false;
        } else {
            validVariant = true;
        }
        return validVariant;
    }

    @Override
    public VCFVariant convertNotClassified() {
        LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
        LOGGER.info("Variant could not be classified. Flagging as invalid.\n");
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, classification, radboudVariant);
        vcfVariant.setValidVariant(false);
        return vcfVariant;
    }
}
