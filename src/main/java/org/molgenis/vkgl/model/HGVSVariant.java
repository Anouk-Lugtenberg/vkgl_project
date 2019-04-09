package org.molgenis.vkgl.model;

public class HGVSVariant extends Variant {
    String referenceSequence;
    String genomicDNA;
    String genomicDNANormalized;

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

    public void setClassification (String classification) {
        switch (classification) {
            case "-":
                this.classification = ClassificationTypes.BENIGN;
                break;
            case "-?":
                this.classification = ClassificationTypes.LIKELY_BENIGN;
                break;
            case "?":
                this.classification = ClassificationTypes.VOUS;
                break;
            case "+?":
                this.classification = ClassificationTypes.LIKELY_PATHOGENIC;
                break;
            case "+":
                this.classification = ClassificationTypes.PATHOGENIC;
        }
    }
}
