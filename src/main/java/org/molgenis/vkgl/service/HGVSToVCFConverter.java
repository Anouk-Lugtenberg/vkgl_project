package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HGVSToVCFConverter extends VCFConverter {
    private HGVSVariant HGVSVariant;
    private VCFVariant VCFVariant;
    private String chromosome;
    private String genomicDNA;
    private int start;
    private int stop;
    private String ALT;
    private String REF;

    HGVSToVCFConverter(HGVSVariant HGVSVariant) {
        this.HGVSVariant = HGVSVariant;
        this.chromosome = HGVSVariant.getChromosome();
        this.genomicDNA = HGVSVariant.getGenomicDNA();
    }

    @Override
    public VCFVariant convertToVCF() {
        try {
            getStartAndStopPosition();
        } catch (NumberFormatException e) {
            //The HGVS variant only contains Strings in the raw file, so even the header can be processed into a HGVSVariant.
            //This throws an exception when creating the start and stop position, so the error is caught here.
            LOGGER.info("Number format exception for line: {}", HGVSVariant.getRawInformation());
            LOGGER.info("Probably header, skipping line");
            VCFVariant vcfVariant = new VCFVariant();
            vcfVariant.setValidVariant(false);
            return vcfVariant;
        }
        switch (HGVSVariant.getVariantType()) {
            case SNP:
                getReferenceAndAlternativeSNP();
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
        }
        return VCFVariant;
    }

    @Override
    public VCFVariant convertSNP() {
        String GRCh37Reference = getBasesFromPosition(chromosome, start, start);
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, HGVSVariant.getClassification(), HGVSVariant);
        if (validateSNP(GRCh37Reference, REF, ALT, HGVSVariant)) {
            vcfVariant.setValidVariant(true);
        } else {
            vcfVariant.setValidVariant(false);
        }
        return vcfVariant;
    }

    private void getReferenceAndAlternativeSNP() {
        Pattern pattern = Pattern.compile("([ACGT])>([ACGT])");
        Matcher matcher = pattern.matcher(genomicDNA);
        if (matcher.find()) {
            REF = matcher.group(1);
            ALT = matcher.group(2);
        } else {
            REF = "N/A";
            ALT = "N/A";
        }
    }

    @Override
    public VCFVariant convertInsertion() {
        getAlternativeInsertion();
        PositionAndNucleotides positionAndNucleotides = moveNucleotidesInsertionAndDuplication();
        return createVCFVariantInsertionAndDuplication(positionAndNucleotides);
    }

    private void getAlternativeInsertion() {
        Pattern pattern = Pattern.compile("g.\\d*_\\d*ins([ACGT]*)");
        Matcher matcher = pattern.matcher(genomicDNA);
        if (matcher.find()) {
            ALT = matcher.group(1);
        }
    }

    @Override
    public VCFVariant convertDeletion() {
        PositionAndNucleotides positionAndNucleotides = moveDeletionMostLeftPosition(chromosome, start, stop);

        int position = positionAndNucleotides.getPosition();
        String referenceGenomeBuild = getBasesFromPosition(chromosome, position, position);
        String newREF = referenceGenomeBuild + positionAndNucleotides.getNucleotides();
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, newREF, referenceGenomeBuild, HGVSVariant.getClassification(), HGVSVariant);

        //is valid, since there is no REF available to check against reference genome.
        vcfVariant.setValidVariant(true);
        return vcfVariant;
    }

    @Override
    public VCFVariant convertDuplication() {
        ALT = getBasesFromPosition(chromosome, start, stop);

        //start -1 because the check should begin one base before the duplication
        start = start - 1;
        PositionAndNucleotides positionAndNucleotides = moveNucleotidesInsertionAndDuplication();

        return createVCFVariantInsertionAndDuplication(positionAndNucleotides);
    }

    @Override
    public VCFVariant convertDeletionInsertion() {
        getReferenceAndAlternativeDeletionInsertion();
        if (REF.equals("")) {
            VCFVariant vcfVariant = new VCFVariant();
            vcfVariant.setValidVariant(false);
            return vcfVariant;
        } else {
            DeletionInsertionStripper deletionInsertionStripper = new DeletionInsertionStripper(REF, ALT, start, stop);
            deletionInsertionStripper.strip();
            ALT = deletionInsertionStripper.getALT();
            REF = deletionInsertionStripper.getREF();
            start = deletionInsertionStripper.getStart();
            stop = deletionInsertionStripper.getStop();

            if (ALT.length() == 0) {
                HGVSVariant.setVariantType("del");
                LOGGER.info("{}: {}", HGVSVariant.getLineNumber(), HGVSVariant.getRawInformation());
                LOGGER.info("Delins variant is actually a deletion.\n");
                return convertDeletion();
            } else if (REF.length() == 0) {
                HGVSVariant.setVariantType("ins");
                LOGGER.info("{}: {}", HGVSVariant.getLineNumber(), HGVSVariant.getRawInformation());
                LOGGER.info("Delins variant is actually an insertion.\n");
                start = start - 1;
                return convertInsertion();
            } else if (REF.length() == 1 && ALT.length() == 1) {
                HGVSVariant.setVariantType("snp");
                LOGGER.info("{}: {}", HGVSVariant.getLineNumber(), HGVSVariant.getRawInformation());
                LOGGER.info("Delins variant is actually a SNP.\n");
                return convertSNP();
            } else {
                VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, HGVSVariant.getClassification(), HGVSVariant);
                vcfVariant.setValidVariant(true);
                return vcfVariant;
            }
        }
    }

    @Override
    public org.molgenis.vkgl.model.VCFVariant convertNotClassified() {
        return null;
    }

    private void getReferenceAndAlternativeDeletionInsertion() {
        Pattern patternDelIns = Pattern.compile("delins([ACGT]*)");
        Matcher matcherDelIns = patternDelIns.matcher(genomicDNA);
        Pattern patternDelNucsIns = Pattern.compile("del([ACGT]*)ins([ACGT]*)");
        Matcher matcherDelNucsIns = patternDelNucsIns.matcher(genomicDNA);
        if (matcherDelIns.find()) {
            REF = getBasesFromPosition(chromosome, start, stop);
            ALT = matcherDelIns.group(1);
        } else if (matcherDelNucsIns.find()) {
            if (!getBasesFromPosition(chromosome, start, stop).equals(matcherDelNucsIns.group((1)))) {
                LOGGER.info("{}: {}", HGVSVariant.getLineNumber(), HGVSVariant.getRawInformation());
                LOGGER.info("Reference genome: {} does not equal reference from given variant: {}. Flagging as invalid.\n",
                        REF, matcherDelNucsIns.group(1));
                VariantErrorCounter.referenceDeletionInsertionIsNotEqual();
                REF = "";
            } else {
                REF = matcherDelNucsIns.group(1);
                ALT = matcherDelNucsIns.group(2);
            }
        }
    }

    private void getStartAndStopPosition() {
        Pattern patternTwoDigits = Pattern.compile("g.(\\d*)_(\\d*)");
        Matcher matcherTwoDigits = patternTwoDigits.matcher(genomicDNA);
        Pattern patternOneDigit = Pattern.compile("g.(\\d*)");
        Matcher matcherOneDigit = patternOneDigit.matcher(genomicDNA);
        if (matcherTwoDigits.find()) {
            start = Integer.parseInt(matcherTwoDigits.group(1));
            stop = Integer.parseInt(matcherTwoDigits.group(2));
        } else if (matcherOneDigit.find()) {
            start = Integer.parseInt(matcherOneDigit.group(1));
            stop = start;
        }
    }

    private PositionAndNucleotides moveNucleotidesInsertionAndDuplication() {
        int position;
        String copyOfALT = ALT;
        if (ALT.length() == 0) {
            LOGGER.info("{}: {}", HGVSVariant.getLineNumber(), HGVSVariant.getRawInformation());
            LOGGER.info("ALT could not be determined for HGVS notation. Flagging as invalid.\n");
            VariantErrorCounter.hgvsNotationSyntaxError();
            position = 0;
            ALT = null;
        } else if (ALT.length() == 1) {
            position = moveNucleotidesMostLeftPosition(ALT, start, chromosome);
        } else if (ALT.chars().allMatch(c -> c == copyOfALT.charAt(0))) {
            //Substring from first base of ALT, because all the nucleotides are the same in the ALT.
            position = moveNucleotidesMostLeftPosition(ALT.substring(0, 1), start, chromosome);
        } else {
            PositionAndNucleotides positionAndAlternative = moveDifferentNucleotidesMostLeftPosition(start, ALT, chromosome);
            position = positionAndAlternative.getPosition();
            ALT = positionAndAlternative.getNucleotides();
        }
        return new PositionAndNucleotides(position, ALT);
    }

    private VCFVariant createVCFVariantInsertionAndDuplication(PositionAndNucleotides positionAndNucleotides) {
        int position = positionAndNucleotides.getPosition();
        String ALT = positionAndNucleotides.getNucleotides();

        String referenceGenomeBuild = getBasesFromPosition(chromosome, position, position);
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, referenceGenomeBuild, newALT, HGVSVariant.getClassification(), HGVSVariant);

        if (ALT == null) {
            vcfVariant.setValidVariant(false);
        } else {
            vcfVariant.setValidVariant(true);
        }
        return vcfVariant;
    }
}
