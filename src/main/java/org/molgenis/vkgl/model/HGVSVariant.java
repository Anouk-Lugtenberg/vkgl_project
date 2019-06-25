package org.molgenis.vkgl.model;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HGVSVariant extends Variant implements Comparable<HGVSVariant> {
    private String referenceSequence;
    private String genomicDNA;

    public String getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(String referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public String getGenomicDNA() {
        return genomicDNA;
    }

    public void setGenomicDNA(String genomicDNA) {
        this.genomicDNA = genomicDNA;
    }

    @Override
    public void setClassification (String classification) {
        switch (classification) {
            case "-":
                this.classification = ClassificationType.BENIGN;
                break;
            case "-?":
                this.classification = ClassificationType.LIKELY_BENIGN;
                break;
            case "?":
                this.classification = ClassificationType.VOUS;
                break;
            case "+?":
                this.classification = ClassificationType.LIKELY_PATHOGENIC;
                break;
            case "+":
                this.classification = ClassificationType.PATHOGENIC;
                break;
            default:
                this.classification = ClassificationType.UNKNOWN_TYPE;
        }
    }

    @Override
    public int compareTo(HGVSVariant hgvsVariant) {
        return HGVSVariant.Comparators.CHROMOSOME_AND_POSITION.compare(this, hgvsVariant);
    }

    public static class Comparators {
        public static final Comparator<HGVSVariant> CHROMOSOME_AND_POSITION =
                Comparator.comparing(HGVSVariant::getChromosome, Comparator.comparingInt(Comparators::extractChromosome))
                .thenComparing(HGVSVariant::getGenomicDNA, Comparator.comparingInt(Comparators::extractPosition));

        //Chromosome can be integer or String X/Y/M
        static int extractChromosome(String chromosome) {
            String num = chromosome.replaceAll("\\D", "");
            //If no number found, character (X/Y/M) of chromosome is used
            return num.isEmpty() ? chromosome.charAt(0) : Integer.parseInt(num);
        }

        //Position is part of the genomic DNA in HGVS Variants
        static int extractPosition(String genomicDNA) {
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(genomicDNA.split(":")[1]);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            } else {
                return 0;
            }
        }
    }
}
