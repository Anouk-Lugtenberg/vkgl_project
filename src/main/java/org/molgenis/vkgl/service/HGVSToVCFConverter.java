package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HGVSToVCFConverter implements VCFConverter {
    private HGVSVariant HGVSVariant;
    private VCFVariant VCFVariant;
    private String chromosome;
    private String genomicDNA;

    HGVSToVCFConverter(HGVSVariant HGVSVariant) {
        this.HGVSVariant = HGVSVariant;
        this.chromosome = HGVSVariant.getChromosome();
        this.genomicDNA = HGVSVariant.getGenomicDNA();
    }

    @Override
    public VCFVariant convertToVCF() {
        switch (HGVSVariant.getVariantType()) {
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
        }
        return VCFVariant;
    }

    @Override
    public VCFVariant convertSNP() {
        StartAndStopPosition startAndStopPosition = getStartAndStopPosition();
        int start = startAndStopPosition.getStart();
        ReferenceAndAlternative referenceAndAlternative = getReferenceAndAlternativeSNP();
        String REF = referenceAndAlternative.getReference();
        String ALT = referenceAndAlternative.getAlternative();
        String GRCh37Reference = VCFConverter.getBasesFromPosition(chromosome, start, start);
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, HGVSVariant.getClassification(), HGVSVariant);
        if (VCFConverter.validateSNP(GRCh37Reference, REF, ALT, HGVSVariant)) {
            vcfVariant.setValidVariant(true);
        } else {
            vcfVariant.setValidVariant(false);
        }
        return vcfVariant;
    }

    private ReferenceAndAlternative getReferenceAndAlternativeSNP() {
        String REF;
        String ALT;
        Pattern pattern = Pattern.compile("([ACGT])>([ACGT])");
        Matcher matcher = pattern.matcher(genomicDNA);
        if (matcher.find()) {
            REF = matcher.group(1);
            ALT = matcher.group(2);
        } else {
            REF = "N/A";
            ALT = "N/A";
        }
        return new ReferenceAndAlternative(REF, ALT);
    }

    @Override
    public VCFVariant convertInsertion() {
        StartAndStopPosition startAndStop = getStartAndStopPosition();
        int start = startAndStop.getStart();
        String ALT = getAlternativeInsertion();

        PositionAndNucleotides positionAndNucleotides = moveNucleotidesInsertionAndDuplication(ALT, start);
        return createVCFVariantInsertionAndDuplication(positionAndNucleotides);
    }

    private boolean validateInsertion() {
        return true;
    }

    private String getAlternativeInsertion() {
        String alternative = "";
        Pattern pattern = Pattern.compile("g.\\d*_\\d*ins([ACGT]*)");
        Matcher matcher = pattern.matcher(genomicDNA);
        if (matcher.find()) {
            alternative = matcher.group(1);
        }
        return alternative;
    }

    @Override
    public VCFVariant convertDeletion() {
        StartAndStopPosition startAndStop = getStartAndStopPosition();
        int start = startAndStop.getStart();
        int stop = startAndStop.getStop();
        PositionAndNucleotides positionAndNucleotides = VCFConverter.moveDeletionMostLeftPosition(chromosome, start, stop);

        int position = positionAndNucleotides.getPosition();
        String referenceGenomeBuild = VCFConverter.getBasesFromPosition(chromosome, position, position);
        String newREF = referenceGenomeBuild + positionAndNucleotides.getNucleotides();
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, newREF, referenceGenomeBuild, HGVSVariant.getClassification(), HGVSVariant);
        //todo when is this valid?
        vcfVariant.setValidVariant(true);
        return vcfVariant;
    }

    @Override
    public VCFVariant convertDuplication() {
        StartAndStopPosition startAndStop = getStartAndStopPosition();
        String ALT = VCFConverter.getBasesFromPosition(chromosome, startAndStop.getStart(), startAndStop.getStop());

        //start -1 because the check should begin one base before the duplication
        int start = startAndStop.getStart() - 1;
        PositionAndNucleotides positionAndNucleotides = moveNucleotidesInsertionAndDuplication(ALT, start);

        return createVCFVariantInsertionAndDuplication(positionAndNucleotides);
    }

    @Override
    public VCFVariant convertDeletionInsertion() {
        return null;
    }

    @Override
    public org.molgenis.vkgl.model.VCFVariant convertNotClassified() {
        return null;
    }

    private StartAndStopPosition getStartAndStopPosition() {
        int start = 0;
        int stop = 0;
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
        return new StartAndStopPosition(start, stop);
    }

    private PositionAndNucleotides moveNucleotidesInsertionAndDuplication(String ALT, int start) {
        int position;
        String copyOfALT = ALT;
        if (ALT.length() == 1) {
            position = VCFConverter.moveNucleotidesMostLeftPosition(ALT, start, chromosome);
        } else if (ALT.chars().allMatch(c -> c == copyOfALT.charAt(0))) {
            //Substring from first base of ALT, because all the nucleotides are the same in the ALT.
            position = VCFConverter.moveNucleotidesMostLeftPosition(ALT.substring(0, 1), start, chromosome);
        } else {
            PositionAndNucleotides positionAndAlternative = VCFConverter.moveDifferentNucleotidesMostLeftPosition(start, ALT, chromosome);
            position = positionAndAlternative.getPosition();
            ALT = positionAndAlternative.getNucleotides();
        }
        return new PositionAndNucleotides(position, ALT);
    }

    private VCFVariant createVCFVariantInsertionAndDuplication(PositionAndNucleotides positionAndNucleotides) {
        int position = positionAndNucleotides.getPosition();
        String ALT = positionAndNucleotides.getNucleotides();

        String referenceGenomeBuild = VCFConverter.getBasesFromPosition(chromosome, position, position);
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, referenceGenomeBuild, newALT, HGVSVariant.getClassification(), HGVSVariant);

        //todo validate
        vcfVariant.setValidVariant(validateInsertion());
        return vcfVariant;
    }
}
