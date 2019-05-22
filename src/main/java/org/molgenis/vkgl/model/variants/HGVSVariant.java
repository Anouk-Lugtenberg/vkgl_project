package org.molgenis.vkgl.model.variants;

import org.molgenis.vkgl.model.ClassificationType;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HGVSVariant extends Variant implements Comparable<HGVSVariant> {
    private String referenceSequence;
    private String genomicDNA;
    private String genomicDNANormalized;

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

    public String getGenomicDNANormalized() {
        return genomicDNANormalized;
    }

    public void setGenomicDNANormalized(String genomicDNANormalized) {
        this.genomicDNANormalized = genomicDNANormalized;
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

        static int extractChromosome(String chromosome) {
            String num = chromosome.replaceAll("\\D", "");
            return num.isEmpty() ? chromosome.charAt(0) : Integer.parseInt(num);
        }

        static int extractPosition(String genomicDNA) {
            Pattern p = Pattern.compile("(\\d+)");
            String[] stuff = genomicDNA.split(":");
            Matcher m = p.matcher(stuff[1]);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            } else {
                return 0;
            }
        }
    }
}
