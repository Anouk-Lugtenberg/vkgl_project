package org.molgenis.vkgl.model;

import java.util.Comparator;

public class VCFVariant {
    private String chromosome;
    private int position;
    private String ALT;
    private String REF;
    private boolean validVariant;
    private ClassificationType classification;
    private Variant rawVariant;
    private String dnaNotation;

    public VCFVariant(String chromosome, int position, String REF, String ALT, ClassificationType classification, Variant rawVariant) {
        this.chromosome = chromosome;
        this.position = position;
        this.REF = REF;
        this.ALT = ALT;
        this.classification = classification;
        this.rawVariant = rawVariant;
    }

    public String getChromosome() {
        return chromosome;
    }

    public int getPosition() {
        return position;
    }

    public String getALT() {
        return ALT;
    }

    public String getREF() {
        return REF;
    }

    public boolean isValidVariant() {
        return validVariant;
    }

    public void setValidVariant(boolean validVariant) {
        this.validVariant = validVariant;
    }

    public ClassificationType getClassification() {
        return classification;
    }

    public Variant getRawVariant() {
        return rawVariant;
    }

    public void setDnaNotation(String dnaNotation) {
        this.dnaNotation = dnaNotation;
    }

    public String getDnaNotation() {
        return dnaNotation;
    }

    public static class Comparators {
        public static final Comparator<VCFVariant> CHROMOSOME_AND_POSITION =
                Comparator.comparing(VCFVariant::getChromosome, Comparator.comparingInt(Comparators::extractInt))
                .thenComparing(VCFVariant::getPosition);

        static int extractInt(String chromosome) {
            String num = chromosome.replaceAll("\\D", "");
            return num.isEmpty() ? chromosome.charAt(0) : Integer.parseInt(num);
        }
    }
}
