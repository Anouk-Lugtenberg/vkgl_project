package org.molgenis.vkgl.model;

public class VCFVariant {
    private String chromosome;
    private int position;
    private String ALT;
    private String REF;

    VCFVariant(String chromosome, int position, String ALT, String REF) {
        this.chromosome = chromosome;
        this.position = position;
        this.ALT = ALT;
        this.REF = REF;
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
}
