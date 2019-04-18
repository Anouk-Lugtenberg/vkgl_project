package org.molgenis.vkgl.model;

public class RadboudVariant extends Variant {
    int start;
    int stop;
    String REF;
    String ALT;
    String exon;
    String transcript;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public String getREF() {
        return REF;
    }

    public void setREF(String REF) {
        this.REF = REF;
    }

    public String getALT() {
        return ALT;
    }

    public void setALT(String ALT) {
        this.ALT = ALT;
    }

    public String getExon() {
        return exon;
    }

    public void setExon(String exon) {
        this.exon = exon.startsWith("0") ? exon.substring(1) : exon.isEmpty() ? null : exon;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public void setClassification(String classification) {
        switch (classification.substring(classification.length() -1)) {
            case "1":
                this.classification = ClassificationType.BENIGN;
                break;
            case "2":
                this.classification = ClassificationType.LIKELY_BENIGN;
                break;
            case "3":
                this.classification = ClassificationType.VOUS;
                break;
            case "4":
                this.classification = ClassificationType.LIKELY_PATHOGENIC;
                break;
            case "5":
                this.classification = ClassificationType.PATHOGENIC;
                break;
        }
    }

    @Override
    public void setVariantType() {
        System.out.println("REF = " + REF);
        if (REF.length() == 1 && ALT.length() == 1) {
            this.variantType = VariantType.SNP;
        }
        this.variantType = VariantType.NOT_CLASSIFIED;
    }
}
