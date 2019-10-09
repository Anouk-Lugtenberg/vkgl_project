package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.*;

public class RadboudToVCFConverter extends VCFConverter {
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
                if (checkStartAndStopPositionSNP(start, stop)) {
                    VCFVariant = convertSNP();
                } else {
                    VCFVariant = new VCFVariant();
                    VCFVariant.setValidVariant(false);
                }
                break;
            case INSERTION:
                if (checkStartAndStopPositions(start, stop)) {
                    VCFVariant = convertInsertion();
                } else {
                    VCFVariant = new VCFVariant();
                    VCFVariant.setValidVariant(false);
                }
                break;
            case DELETION:
                if (checkStartAndStopPositions(start, stop)) {
                    VCFVariant = convertDeletion();
                } else {
                    VCFVariant = new VCFVariant();
                    VCFVariant.setValidVariant(false);
                }
                break;
            case DUPLICATION:
                if (checkStartAndStopPositions(start, stop)) {
                    VCFVariant = convertDuplication();
                } else {
                    VCFVariant = new VCFVariant();
                    VCFVariant.setValidVariant(false);
                }
                break;
            case DELETION_INSERTION:
                if (checkStartAndStopPositions(start, stop)) {
                    VCFVariant = convertDeletionInsertion();
                } else {
                    VCFVariant = new VCFVariant();
                    VCFVariant.setValidVariant(false);
                }
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
            String referenceGenomeBuild = getBasesFromPosition(chromosome, start, stop);
            if (referenceGenomeBuild == null ) {
                isValidVariant = false;
            } else {
                isValidVariant = validateSNP(referenceGenomeBuild, REF, ALT, radboudVariant);
            }
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
            position = moveNucleotidesMostLeftPosition(ALT, start, chromosome);
        } else if (ALT.chars().allMatch(c -> c == ALT.charAt(0))) {
            //Substring from first base of ALT, because all the nucleotides are the same in the ALT.
            position = moveNucleotidesMostLeftPosition(ALT.substring(0, 1), start, chromosome);
        } else {
            PositionAndNucleotides positionAndAlternative = moveDifferentNucleotidesMostLeftPosition(start, ALT, chromosome);
            position = positionAndAlternative.getPosition();
            ALT = positionAndAlternative.getNucleotides();
        }

        String referenceGenomeBuild = getBasesFromPosition(chromosome, position, position);
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
        //The deletions from cartagenia are sometimes differently formatted:103471457	103471462	CATCAT	CAT
        //the CAT from the REF is not part of the deletion here.
        if (radboudVariant instanceof CartageniaVariant) {
            if (ALT.matches("[ACGT]+")) {
                stop = stop - ALT.length();
                REF = REF.replaceFirst(ALT, "");
            }
        }
        //For the Alissa format, some of the alternatives from the deletions also have nucleotides instead of '0'
        //I've found this inconsistency after running the program and writing the report, but the function above could also be used for the
        //Alissa format.

        PositionAndNucleotides positionAndNucleotides = moveDeletionMostLeftPosition(chromosome, start, stop);
        int position = positionAndNucleotides.getPosition();

        String referenceGenomeBuild = getBasesFromPosition(chromosome, position, position);
        String newREF = referenceGenomeBuild + positionAndNucleotides.getNucleotides();
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, newREF, referenceGenomeBuild, classification, radboudVariant);
        vcfVariant.setValidVariant(validateREFDeletion());
        return vcfVariant;
    }

    private boolean validateREFDeletion() {
        boolean referenceValid = true;
        if (REF.length() > 0) {
            String GRChREF = getBasesFromPosition(chromosome, start, stop);
            if (!GRChREF.equals(REF)) {
                LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
                LOGGER.info("Reference genome: {} does not equal reference given for variant: {}. Flagging as invalid.\n", GRChREF, REF);
                VariantErrorCounter.referenceDeletionIsNotEqual();
                referenceValid = false;
            }
        }
        return referenceValid;
    }

    @Override
    public VCFVariant convertDuplication() {
        //If no ALT is given, the nucleotides between start-stop are used
        if (ALT.length() == 0) {
            ALT = getBasesFromPosition(chromosome, start, stop);
        }

        //The movement of the position is the same as for insertion variants
        return convertInsertion();
    }

    @Override
    public VCFVariant convertDeletionInsertion() {
        boolean validVariant = checkREFandALTDeletionInsertion();
        if (!validVariant) {
            VCFVariant vcfVariant = new VCFVariant();
            vcfVariant.setValidVariant(false);
            return vcfVariant;
        } else {
            VCFVariant vcfVariant = checkIfActualDelIns();
            if (vcfVariant == null) {
                vcfVariant = new VCFVariant(chromosome, start, REF, ALT, classification, radboudVariant);
                vcfVariant.setValidVariant(true);
            }
            return vcfVariant;
        }
    }

    private VCFVariant checkIfActualDelIns() {
        DeletionInsertionStripper deletionInsertionStripper = new DeletionInsertionStripper(REF, ALT, start, stop);
        deletionInsertionStripper.strip();
        ALT = deletionInsertionStripper.getALT();
        REF = deletionInsertionStripper.getREF();
        start = deletionInsertionStripper.getStart();
        stop = deletionInsertionStripper.getStop();

        if (ALT.length() == 0) {
            radboudVariant.setVariantType("del");
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Delins variant is actually a deletion.\n");
            VariantErrorCounter.deletionInsertionIsADeletion();
            return convertDeletion();
        } else if (REF.length() == 0) {
            radboudVariant.setVariantType("ins");
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Delins variant is actually an insertion.\n");
            VariantErrorCounter.deletionInsertionIsAnInsertion();
            start = start - 1;
            return convertInsertion();
        } else if (REF.length() == 1 && ALT.length() == 1) {
            radboudVariant.setVariantType("snp");
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Delins variant is actually a SNP.\n");
            VariantErrorCounter.deletionInsertionIsASNP();
            return convertSNP();
        } else {
            return null;
        }
    }

    private boolean checkREFandALTDeletionInsertion() {
        String GRChREF = getBasesFromPosition(chromosome, start, stop);
        boolean validVariant = true;
        if (REF.length() == 0) {
            REF = GRChREF;
        } else if (!REF.equals(GRChREF)) {
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Reference genome: {} does not equal reference given for variant: {}. Flagging as invalid.\n", GRChREF, REF);
            VariantErrorCounter.referenceDeletionInsertionIsNotEqual();
            validVariant = false;
        }
        if (ALT.length() == 0) {
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("No ALT available for delins. Flagging as invalid.\n");
            VariantErrorCounter.noAlternativeAvailableForDeletionInsertion();
            validVariant = false;
        }
        return validVariant;
    }

    private boolean checkStartAndStopPositions(int start, int stop) {
        boolean validPositions = true;
        if (stop < start) {
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Stop position is of smaller value than start position. Flagging as invalid.\n");
            VariantErrorCounter.stopPositionOfSmallerValueThanStartPosition();
            validPositions = false;
        }
        return validPositions;
    }

    private boolean checkStartAndStopPositionSNP(int start, int stop) {
        if (start != stop) {
            LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
            LOGGER.info("Start and stop position for SNP is not the same. Flagging as invalid.\n");
            VariantErrorCounter.startAndStopPositionNotTheSameForSNP();
        }
        return start == stop;
    }

    @Override
    public VCFVariant convertNotClassified() {
        LOGGER.info("{}: {}", radboudVariant.getLineNumber(), radboudVariant.getRawInformation());
        LOGGER.info("Variant could not be classified. Flagging as invalid.\n");
        VariantErrorCounter.variantCouldNotBeClassified();
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, classification, radboudVariant);
        vcfVariant.setValidVariant(false);
        return vcfVariant;
    }
}
