package org.molgenis.vkgl.model;

import java.util.Comparator;

public class RadboudVariant extends Variant implements Comparable<RadboudVariant> {
    private int start;
    private int stop;
    private String REF;
    private String ALT;
    private String exon;
    private String transcript;

    public void setIdentifier() {
        this.identifier = this.start + "_" + this.stop;
    }

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
    public int compareTo(RadboudVariant radboudVariant) {
        return Comparators.CHROMOSOME_AND_START.compare(this, radboudVariant);
    }

    public static class Comparators {
        public static final Comparator<RadboudVariant> CHROMOSOME_AND_START =
                Comparator.comparing(RadboudVariant::getChromosome, Comparator.comparingInt(Comparators::extractInt))
                .thenComparing(RadboudVariant::getStart);

        static int extractInt(String chromosome) {
            String num = chromosome.replaceAll("\\D", "");
            return num.isEmpty() ? chromosome.charAt(0) : Integer.parseInt(num);
        }
    }
}
