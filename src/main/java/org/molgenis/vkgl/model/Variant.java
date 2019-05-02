package org.molgenis.vkgl.model;

import java.io.Serializable;
import java.util.Comparator;

public class Variant implements Serializable {
    String identifier;
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

    public static Comparator<Variant> VariantComparator = new Comparator<Variant>() {
        public int compare(Variant v1, Variant v2) {
            return v1.getChromosome().compareTo(v2.getChromosome());
        }
    };
//    private static void order (ArrayList<Variant> variants) {
//        Collections.sort(variants, new Comparator<Variant>() {
//            @Override
//            public int compare(Variant o1, Variant o2) {
//                return o1.getChromosome().compareTo(o2.getChromosome());
//            }
//        });
//    }


}
