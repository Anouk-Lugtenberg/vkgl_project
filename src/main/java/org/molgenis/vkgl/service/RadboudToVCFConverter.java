package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.RadboudVariant;
import org.molgenis.vkgl.model.StartAndStopPosition;
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
        StartAndStopPosition newStartAndStop;
        if (ALT.length() == 1) {
            newStartAndStop = moveNucleotidesMostLeftPosition(ALT);
        } else if (ALT.chars().allMatch(c -> c == ALT.charAt(0))) {
            //Substring from first base of ALT, because all the nucleotides are the same in the ALT.
            newStartAndStop = moveNucleotidesMostLeftPosition(ALT.substring(0, 1));
        } else {
            //TODO: ALT which aren't of length 1 or are the same nucleotides aren't processed yet.
            newStartAndStop = new StartAndStopPosition(start, stop);
        }
        if (newStartAndStop.getStart() != start) {
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Variant could be placed more to the left, position changed from {} to {}", start, newStartAndStop.getStart());
            start = newStartAndStop.getStart();
            stop = newStartAndStop.getStop();
        }
        String referenceGenomeBuild = VCFConverter.getBasesFromPosition("chr1", start, stop);
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, referenceGenomeBuild, newALT, classification, radboudVariant);
        vcfVariant.setValidVariant(validateInsertion());
        return vcfVariant;
    }

    private StartAndStopPosition moveNucleotidesMostLeftPosition(String ALT) {
        //As long as the nucleotide in the position more to the left is the same, the position should be shuffled to the left
        //e.g. AATTCC, insertion of T at position 5 AATT-T-CC should actually be insertion of T at position 3
        //AA-T-TTCC.
        int newStart = start;
        int newStop = stop;
        while (VCFConverter.getBasesFromPosition("chr1", newStart, newStop).equals(ALT)) {
            newStart = newStart - 1;
            newStop = newStop - 1;
        }
        return new StartAndStopPosition(newStart, newStop);
    }

    //TODO: Validate variants with type insertion.
    private boolean validateInsertion() {
        return true;
    }

    @Override
    public VCFVariant convertDeletion() {
        //in VCF the REF with deletions is one position BEFORE the actual deletion.
        int startPosition = start -1;

        String newREF = VCFConverter.getBasesFromPosition("chr1", startPosition, stop);
        String newALT = VCFConverter.getBasesFromPosition("chr1", startPosition, startPosition);
        VCFVariant vcfVariant = new VCFVariant(chromosome, startPosition, newREF, newALT, classification, radboudVariant);
        vcfVariant.setValidVariant(validateDeletion());
        return vcfVariant;
    }

    private boolean validateDeletion() {
        boolean deletionValid = true;
        if (REF.length() > 0) {
            String GRChREF = VCFConverter.getBasesFromPosition("chr1", start, stop);
            if (!GRChREF.equals(REF)) {
                LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
                LOGGER.info("Reference genome: {} does not equal reference given for variant: {}. Flagging as invalid.\n", GRChREF, REF);
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
