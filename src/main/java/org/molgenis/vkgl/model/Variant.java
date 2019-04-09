package org.molgenis.vkgl.model;

public class Variant {
    String chromosome;
    Enum classification;
    String geneName;
    String cDNANotation;
    String proteinNotation;

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public Enum getClassification() {
        return classification;
    }

    public void setClassification(String classification) {}

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getcDNANotation() {
        return cDNANotation;
    }

    public void setcDNANotation(String cDNANotation) {
        this.cDNANotation = cDNANotation;
    }

    public String getProteinNotation() {
        return proteinNotation;
    }

    public void setProteinNotation(String proteinNotation) {
        this.proteinNotation = proteinNotation;
    }
}
