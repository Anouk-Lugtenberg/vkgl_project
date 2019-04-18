package org.molgenis.vkgl.model;

public class HGVSVariant extends Variant {
    private String referenceSequence;
    private String genomicDNA;
    private String genomicDNANormalized;

    public String getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(String referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public String getGenomicDNA() {
        return genomicDNA;
    }

    public void setGenomicDNA(String genomicDNA) {
        this.genomicDNA = genomicDNA;
    }

    public String getGenomicDNANormalized() {
        return genomicDNANormalized;
    }

    public void setGenomicDNANormalized(String genomicDNANormalized) {
        this.genomicDNANormalized = genomicDNANormalized;
    }

    @Override
    public void setClassification (String classification) {
        switch (classification) {
            case "-":
                this.classification = ClassificationType.BENIGN;
                break;
            case "-?":
                this.classification = ClassificationType.LIKELY_BENIGN;
                break;
            case "?":
                this.classification = ClassificationType.VOUS;
                break;
            case "+?":
                this.classification = ClassificationType.LIKELY_PATHOGENIC;
                break;
            case "+":
                this.classification = ClassificationType.PATHOGENIC;
        }
    }

    @Override
    public void setVariantType () {
        if (genomicDNA.contains(">")) {
            this.variantType = VariantType.SNP;
        } else if (genomicDNA.contains("ins")) {
            this.variantType = VariantType.INSERTION;
        } else if (genomicDNA.contains("del")) {
            this.variantType = VariantType.DELETION;
        } else {
            this.variantType = VariantType.NOT_CLASSIFIED;
        }
    }
}
