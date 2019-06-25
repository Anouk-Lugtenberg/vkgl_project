package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.RadboudVariant;
import org.molgenis.vkgl.model.VCFVariant;

public class RadboudToVCFConverter implements VCFConverter {
    private RadboudVariant radboudVariant;
    private VCFVariant VCFVariant;
    private String chromosome;
    private String chromosomeWithChr;
    private int start;
    private int stop;
    private String REF;
    private String ALT;
    private ClassificationType classification;

    public RadboudToVCFConverter(RadboudVariant radboudVariant) {
        this.radboudVariant = radboudVariant;
        this.chromosome = radboudVariant.getChromosome();
        this.chromosomeWithChr = "chr" + radboudVariant.getChromosome();
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
            String referenceGenomeBuild = VCFConverter.getBasesFromPosition(chromosomeWithChr, start, stop);
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
            position = moveNucleotidesMostLeftPosition(ALT);
        } else if (ALT.chars().allMatch(c -> c == ALT.charAt(0))) {
            //Substring from first base of ALT, because all the nucleotides are the same in the ALT.
            position = moveNucleotidesMostLeftPosition(ALT.substring(0, 1));
        } else {
            position = moveDifferentNucleotidesMostLeftPosition();
        }

        if (position != start) {
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Variant could be placed more to the left, position changed from {} to {}", start, position);
        }
        String referenceGenomeBuild = VCFConverter.getBasesFromPosition(chromosomeWithChr, position, position);
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, referenceGenomeBuild, newALT, classification, radboudVariant);
        vcfVariant.setValidVariant(validateInsertion());
        return vcfVariant;
    }

    private int moveNucleotidesMostLeftPosition(String ALT) {
        //As long as the nucleotide in the position more to the left is the same, the position should be shuffled to the left
        //e.g. AATTCC, insertion of T at position 5 AATT-T-CC should actually be insertion of T at position 3
        //AA-T-TTCC.
        int position = start;
        //Only need one base for the anchor, which is the same as the ALT. That's why position is used instead of start/stop.
        while (VCFConverter.getBasesFromPosition(chromosomeWithChr, position, position).equals(ALT)) {
            position = position - 1;
        }
        return position;
    }

    private int moveDifferentNucleotidesMostLeftPosition() {
        int position = start;
        StringBuilder newALT = new StringBuilder(ALT);

        //Position of the last nucleotide from the ALT
        int positionLastNucleotide = ALT.length() - 1;
        //Get last nucleotide from ALT
        String lastNucleotide = ALT.substring(positionLastNucleotide);
        String nucleotideThreeSideALT = VCFConverter.getBasesFromPosition(chromosomeWithChr, position, position);

        while (nucleotideThreeSideALT.equals(lastNucleotide)) {
            //If nucleotides are equal: insert nucleotide to the front of the ALT and remove from the end of the ALT.
            newALT.insert(0, lastNucleotide).setLength(newALT.length() - 1);

            //Re-do while loop for position one step more to the 3' side of the sequence.
            lastNucleotide = ALT.substring(positionLastNucleotide - 1, positionLastNucleotide);
            positionLastNucleotide = positionLastNucleotide - 1;
            position = position - 1;
            nucleotideThreeSideALT = VCFConverter.getBasesFromPosition(chromosomeWithChr, position, position);
        }
        ALT = newALT.toString();
        return position;
    }

    //TODO: Validate variants with type insertion.
    private boolean validateInsertion() {
        return true;
    }

    @Override
    public VCFVariant convertDeletion() {
        //in VCF the REF with deletions is one position BEFORE the actual deletion.
        int startPosition = start -1;

        String newREF = VCFConverter.getBasesFromPosition(chromosomeWithChr, startPosition, stop);
        String newALT = VCFConverter.getBasesFromPosition(chromosomeWithChr, startPosition, startPosition);
        VCFVariant vcfVariant = new VCFVariant(chromosome, startPosition, newREF, newALT, classification, radboudVariant);
        vcfVariant.setValidVariant(validateDeletion());
        return vcfVariant;
    }

    private boolean validateDeletion() {
        boolean deletionValid = true;
        if (REF.length() > 0) {
            String GRChREF = VCFConverter.getBasesFromPosition(chromosomeWithChr, start, stop);
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
            String GRChREF = VCFConverter.getBasesFromPosition(chromosomeWithChr, start, stop);
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
