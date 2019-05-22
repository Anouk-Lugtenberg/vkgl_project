package org.molgenis.vkgl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HGVSVariantTest {
    private HGVSVariant hgvsVariant;

    @BeforeEach
    void init () {
        hgvsVariant = new HGVSVariant();
    }

    @Test
    void setClassificationBenign() {
        hgvsVariant.setClassification("-");
        assertEquals(hgvsVariant.getClassification(), ClassificationType.BENIGN);
    }

    @Test
    void setClassificationLikelyBenign() {
        hgvsVariant.setClassification("-?");
        assertEquals(hgvsVariant.getClassification(), ClassificationType.LIKELY_BENIGN);
    }

    @Test
    void setClassificationVous() {
        hgvsVariant.setClassification("?");
        assertEquals(hgvsVariant.getClassification(), ClassificationType.VOUS);
    }

    @Test
    void setClassificationLikelyPathogenic() {
        hgvsVariant.setClassification("+?");
        assertEquals(hgvsVariant.getClassification(), ClassificationType.LIKELY_PATHOGENIC);
    }

    @Test
    void setClassificationPathogenic() {
        hgvsVariant.setClassification("+");
        assertEquals(hgvsVariant.getClassification(), ClassificationType.PATHOGENIC);
    }

    @Test
    void setClassificationTypeUnknown() {
        hgvsVariant.setClassification("unknown");
        assertEquals(hgvsVariant.getClassification(), ClassificationType.UNKNOWN_TYPE);
    }

    @Test
    void variantsEqual() {
        hgvsVariant.setChromosome("1");
        hgvsVariant.setGenomicDNA("transcript:g.1C>T");
        HGVSVariant hgvsVariantTwo = new HGVSVariant();
        hgvsVariantTwo.setChromosome("1");
        hgvsVariantTwo.setGenomicDNA("transcript:g.1C>T");

        int result = HGVSVariant.Comparators.CHROMOSOME_AND_POSITION.compare(hgvsVariant, hgvsVariantTwo);
        assertEquals(0, result, "Expected variants to be equal");
    }

    @Test
    void chromosome2GreaterThanChromosome1() {
        hgvsVariant.setChromosome("1");
        HGVSVariant hgvsVariantTwo = new HGVSVariant();
        hgvsVariantTwo.setChromosome("2");

        int result = HGVSVariant.Comparators.CHROMOSOME_AND_POSITION.compare(hgvsVariant, hgvsVariantTwo);
        assertTrue(result <= -1, "Expected variant with higher chromosome number to be greater.");
    }

    @Test
    void chromosomeXGreaterThanChromosome1() {
        hgvsVariant.setChromosome("1");
        HGVSVariant hgvsVariantTwo = new HGVSVariant();
        hgvsVariantTwo.setChromosome("X");

        int result = HGVSVariant.Comparators.CHROMOSOME_AND_POSITION.compare(hgvsVariant, hgvsVariantTwo);
        assertTrue(result <= -1, "Expected variant with chromosome 1 to be greater than chromosomeX");
    }

    @Test
    void chromosomeYGreaterThanChromosomeX() {
        hgvsVariant.setChromosome("X");
        HGVSVariant hgvsVariantTwo = new HGVSVariant();
        hgvsVariantTwo.setChromosome("Y");

        int result = HGVSVariant.Comparators.CHROMOSOME_AND_POSITION.compare(hgvsVariant, hgvsVariantTwo);
        assertTrue(result <= -1, "Expected variant with chromosome X to be greater than chromosome Y");
    }

    @Test
    void positionGreaterThan() { ;
        hgvsVariant.setChromosome("1");
        hgvsVariant.setGenomicDNA("transcript:g.30C>T");
        HGVSVariant hgvsVariantTwo = new HGVSVariant();
        hgvsVariantTwo.setChromosome("1");
        hgvsVariantTwo.setGenomicDNA("transcript:g.20C>T");
        int result = HGVSVariant.Comparators.CHROMOSOME_AND_POSITION.compare(hgvsVariant, hgvsVariantTwo);
        assertTrue(result >= 1, "Expected variant with genomic DNA of 30 to be greater than 20.");
    }

    @Test
    void positionLessThan() {
        hgvsVariant.setChromosome("1");
        hgvsVariant.setGenomicDNA("transcript:g.20C>T");
        HGVSVariant hgvsVariantTwo = new HGVSVariant();
        hgvsVariantTwo.setChromosome("1");
        hgvsVariantTwo.setGenomicDNA("transcript:g.30C>T");
        int result = HGVSVariant.Comparators.CHROMOSOME_AND_POSITION.compare(hgvsVariant, hgvsVariantTwo);
        assertTrue(result <= -1, "Expected variant with genomic DNA of 20 to be less than 30.");
    }
}
