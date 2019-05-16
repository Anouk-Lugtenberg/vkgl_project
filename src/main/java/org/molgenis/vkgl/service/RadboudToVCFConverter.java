package org.molgenis.vkgl.service;

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

    public RadboudToVCFConverter(RadboudVariant radboudVariant) {
        this.radboudVariant = radboudVariant;
        this.chromosome = radboudVariant.getChromosome();
        this.start = radboudVariant.getStart();
        this.stop = radboudVariant.getStop();
        this.REF = radboudVariant.getREF();
        this.ALT = radboudVariant.getALT();
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
                break;
        }
        return VCFVariant;
    }

    @Override
    public VCFVariant convertSNP() {
        boolean isValidVariant;
        try {
            String referenceGenomeBuild = VCFConverter.getBasesFromPosition("chr1", start, stop);
            if (VCFConverter.validateSNP(referenceGenomeBuild, REF, ALT, radboudVariant)) {
                isValidVariant = true;
            } else {
                isValidVariant = false;
            }
        } catch (IllegalArgumentException e) {
            isValidVariant = false;
        }
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, radboudVariant);
        vcfVariant.setValidVariant(isValidVariant);
        return vcfVariant;
    }

    @Override
    public VCFVariant convertInsertion() {
        String referenceGenomeBuild = VCFConverter.getBasesFromPosition("chr1", start, stop);
        String newALT = referenceGenomeBuild + ALT;
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, referenceGenomeBuild, newALT, radboudVariant);
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
        VCFVariant vcfVariant = new VCFVariant(chromosome, startPosition, newREF, newALT, radboudVariant);
        vcfVariant.setValidVariant(validateDeletion());
        return vcfVariant;
    }

    private boolean validateDeletion() {
        boolean deletionValid = true;
        if (REF.length() > 0) {
            String GRChREF = VCFConverter.getBasesFromPosition("chr1", start, stop);
            if (!GRChREF.equals(REF)) {
                System.out.println("\nGRChREF: " + GRChREF + " does not equal: " + REF);
                System.out.println("radboudVariant = " + radboudVariant.getRawInformation());
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
            if (REF.length() > 0 && ALT.length() > 0) {
                validVariant = REF.equals(GRChREF);
            } else {
                REF = GRChREF;
                validVariant = ALT.length() > 0;
            }
        } catch (IllegalArgumentException e) {
            validVariant = false;
        }
        VCFVariant vcfVariant = new VCFVariant(chromosome, start, REF, ALT, radboudVariant);
        vcfVariant.setValidVariant(validVariant);
        return vcfVariant;
    }
}
