package org.molgenis.vkgl.model;

import java.util.Comparator;

public class VCFVariant {
    private String chromosome;
    private int position;
    private String ALT;
    private String REF;
    private boolean validVariant;
    private Variant rawVariant;

    public VCFVariant(String chromosome, int position, String REF, String ALT, Variant rawVariant) {
        this.chromosome = chromosome;
        this.position = position;
        this.REF = REF;
        this.ALT = ALT;
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

    public Variant getRawVariant() {
        return rawVariant;
    }

    public void setRawVariant(Variant rawVariant) {
        this.rawVariant = rawVariant;
    }

    public int compareTo(VCFVariant vcfVariant) {
        return Comparators.CHROMOSOME_AND_POSITION.compare(this, vcfVariant);
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
