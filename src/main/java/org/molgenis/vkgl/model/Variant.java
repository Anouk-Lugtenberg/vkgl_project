package org.molgenis.vkgl.model;

import java.io.Serializable;

public class Variant implements Serializable {
    ClassificationType classification;
    VariantType variantType;
    private String chromosome;
    private String geneName;
    private String cDNANotation;
    private String proteinNotation;

    public ClassificationType getClassification() {
        return classification;
    }

    public void setClassification(String classification) {}

    public VariantType getVariantType() {
        return variantType;
    }

    public void setVariantType() {}

    public void setVariantType(String variantType) {}

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome.startsWith("chr") ? chromosome.substring(3) : chromosome;
    }

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
