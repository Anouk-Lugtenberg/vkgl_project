package org.molgenis.vkgl.model;

public class PositionAndNucleotides {
    private final int position;
    private final String nucleotides;

    public PositionAndNucleotides(int position, String nucleotides) {
        this.position = position;
        this.nucleotides = nucleotides;
    }

    public int getPosition() {
        return position;
    }

    public String getNucleotides() {
        return nucleotides;
    }
}
