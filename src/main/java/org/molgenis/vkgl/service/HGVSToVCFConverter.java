package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.ReferenceAndAlternative;
import org.molgenis.vkgl.model.StartAndStopPosition;
import org.molgenis.vkgl.model.VCFVariant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HGVSToVCFConverter implements VCFConverter {
    private HGVSVariant HGVSVariant;
    private VCFVariant VCFVariant;
    private String chromosome;
    private String genomicDNA;
    private String genomicDNANormalized;

    public HGVSToVCFConverter(HGVSVariant HGVSVariant) {
        this.HGVSVariant = HGVSVariant;
        this.chromosome = HGVSVariant.getChromosome();
        this.genomicDNA = HGVSVariant.getGenomicDNA();
        this.genomicDNANormalized = HGVSVariant.getGenomicDNANormalized();
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
        int start = getPositionSNP();
        ReferenceAndAlternative referenceAndAlternative = getReferenceAndAlternativeSNP();
        String REF = referenceAndAlternative.getReference();
        String ALT = referenceAndAlternative.getAlternative();
        String GRCh37Reference = VCFConverter.getBasesFromPosition("chr1", start, start);
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, HGVSVariant.getClassification(), HGVSVariant);
        if (VCFConverter.validateSNP(GRCh37Reference, REF, ALT, HGVSVariant)) {
            vcfVariant.setValidVariant(true);
        } else {
            vcfVariant.setValidVariant(false);
        }
        return vcfVariant;
    }

    private int getPositionSNP() {
        int position;
        Pattern pattern = Pattern.compile("\\.(\\d*)[ACGT]");
        Matcher matcher = pattern.matcher(genomicDNA);
        if (matcher.find()) {
            position = Integer.parseInt(matcher.group(1));
        } else {
            position = 0;
        }
        return position;
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
        StartAndStopPosition startAndStop = getStartAndStopInsertion();
        int start = startAndStop.getStart();
        int position;
        String ALT = getAlternativeInsertion();
        if (ALT.length() == 1) {
            position = moveNucleotidesMostLeftPosition(ALT, start);
        } else if (ALT.chars().allMatch(c -> c == ALT.charAt(0))) {
            //Substring from first base of ALT, because all the nucleotides are the same in the ALT.
            position = moveNucleotidesMostLeftPosition(ALT.substring(0, 1), start);
        } else {
            //TODO: ALT which aren't of length 1 or are the same nucleotides aren't processed yet.
           position = start;
        }
        if (position != startAndStop.getStart()) {
            LOGGER.info("{}: {}", HGVSVariant.getLineNumber(), HGVSVariant.getRawInformation());
            LOGGER.info("Variant could be placed more to the left, position changed from {} to {}", start, position);
        }

        String referenceGenomeBuild = VCFConverter.getBasesFromPosition("chr1", position, position);
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, referenceGenomeBuild, newALT, HGVSVariant.getClassification(), HGVSVariant);
        vcfVariant.setValidVariant(validateInsertion());
        return vcfVariant;
    }

    private int moveNucleotidesMostLeftPosition(String ALT, int position) {
        //As long as the nucleotide in the position more to the left is the same, the position should be shuffled to the left
        //e.g. AATTCC, insertion of T at position 5 AATT-T-CC should actually be insertion of T at position 3
        //AA-T-TTCC.
        while (VCFConverter.getBasesFromPosition("chr1", position, position).equals(ALT)) {
            position = position - 1;
        }
        return position;
    }

    private boolean validateInsertion() {
        return true;
    }

    private StartAndStopPosition getStartAndStopInsertion() {
        int start = 0;
        int stop = 0;
        Pattern pattern = Pattern.compile("g.(\\d*)_(\\d*)ins");
        Matcher matcher = pattern.matcher(genomicDNA);
        if (matcher.find()) {
            start = Integer.parseInt(matcher.group(1));
            stop = Integer.parseInt(matcher.group(2));
        }
        return new StartAndStopPosition(start, stop);
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
        StartAndStopPosition startAndStop = getStartAndStopDeletion();
        int position = startAndStop.getStart() - 1;
        String REF = VCFConverter.getBasesFromPosition("chr1", position, startAndStop.getStop());
//        String REF = VCFConverter.getBasesFromPosition("chr1", 874816, 874864);
        String ALT = REF.substring(0, 1);
        VCFVariant vcfVariant = new VCFVariant(chromosome, position, REF, ALT, HGVSVariant.getClassification(), HGVSVariant);
        vcfVariant.setValidVariant(true);
        return vcfVariant;
    }

    private StartAndStopPosition getStartAndStopDeletion() {
        int start = 0;
        int stop = 0;
        Pattern patternStartAndStop = Pattern.compile("g.(\\d*)_(\\d*)del");
        Matcher matcherStartAndStop = patternStartAndStop.matcher(genomicDNA);
        Pattern patternStart = Pattern.compile("g.(\\d*)del");
        Matcher matcherStart = patternStart.matcher(genomicDNA);
        if (matcherStartAndStop.find()) {
            start = Integer.parseInt(matcherStartAndStop.group(1));
            stop = Integer.parseInt(matcherStartAndStop.group(2));
        } else if (matcherStart.find()) {
            int position = Integer.parseInt(matcherStart.group(1));
            start = position;
            stop = position;
        }
        return new StartAndStopPosition(start, stop);
    }

    @Override
    public VCFVariant convertDuplication() {
        return null;
    }

    @Override
    public VCFVariant convertDeletionInsertion() {
        return null;
    }

    @Override
    public org.molgenis.vkgl.model.VCFVariant convertNotClassified() {
        return null;
    }
}
