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

    public HGVSToVCFConverter(HGVSVariant HGVSVariant) {
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
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, HGVSVariant);
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
        String ALT = getAlternativeInsertion();
        String referenceGenomeBuild = VCFConverter.getBasesFromPosition("chr1", startAndStop.getStart(), startAndStop.getStart());
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, startAndStop.getStart(), referenceGenomeBuild, newALT, HGVSVariant);
        vcfVariant.setValidVariant(validateInsertion());
        return vcfVariant;
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
        return null;
    }

    @Override
    public VCFVariant convertDuplication() {
        return null;
    }

    @Override
    public VCFVariant convertDeletionInsertion() {
        return null;
    }
}
