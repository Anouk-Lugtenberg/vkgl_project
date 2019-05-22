package org.molgenis.vkgl.model.variants;

import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.VariantType;

import java.io.Serializable;

public class Variant implements Serializable {
    String identifier;
    private int lineNumber;
    ClassificationType classification;
    VariantType variantType;
    private String chromosome;
    private String geneName;
    private String cDNANotation;
    private String proteinNotation;
    private String rawInformation;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public ClassificationType getClassification() {
        return classification;
    }

    public void setClassification(String classification) {}

    public VariantType getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantNotation) {
        if (variantNotation.contains(">")) {
            this.variantType = VariantType.SNP;
        } else if (variantNotation.contains("delins")) {
            this.variantType = VariantType.DELETION_INSERTION;
        } else if (variantNotation.contains("del")) {
            this.variantType = VariantType.DELETION;
        } else if (variantNotation.contains("ins")) {
            this.variantType = VariantType.INSERTION;
        } else if (variantNotation.contains("dup")) {
            this.variantType = VariantType.DUPLICATION;
        } else {
            this.variantType = VariantType.NOT_CLASSIFIED;
        }
    }

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

    public String getRawInformation() {
        return rawInformation;
    }

    public void setRawInformation(String rawInformation) {
        this.rawInformation = rawInformation;
    }
}
