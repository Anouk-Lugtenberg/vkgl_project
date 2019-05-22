package org.molgenis.vkgl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RadboudVariantTest {
    private RadboudVariant radboudVariant;

    @BeforeEach
    void init() { radboudVariant = new RadboudVariant(); }

    @Test
    void setClassificationBenign() {
        radboudVariant.setClassification("1");
        assertEquals(radboudVariant.getClassification(), ClassificationType.BENIGN);
    }

    @Test
    void setClassificationLikelyBenign() {
        radboudVariant.setClassification("2");
        assertEquals(radboudVariant.getClassification(), ClassificationType.LIKELY_BENIGN);
    }

    @Test
    void setClassificationVous() {
        radboudVariant.setClassification("3");
        assertEquals(radboudVariant.getClassification(), ClassificationType.VOUS);
    }

    @Test
    void setClassificationLikelyPathogenic() {
        radboudVariant.setClassification("4");
        assertEquals(radboudVariant.getClassification(), ClassificationType.LIKELY_PATHOGENIC);
    }

    @Test
    void setClassificationPathogenic() {
        radboudVariant.setClassification("5");
        assertEquals(radboudVariant.getClassification(), ClassificationType.PATHOGENIC);
    }

    @Test
    void setClassificationUnknownType() {
        radboudVariant.setClassification("unknownType");
        assertEquals(radboudVariant.getClassification(), ClassificationType.UNKNOWN_TYPE);
    }

    @Test
    void variantsEqual() {
        radboudVariant.setChromosome("1");
        radboudVariant.setStart(1);
        RadboudVariant radboudVariantTwo = new RadboudVariant();
        radboudVariantTwo.setChromosome("1");
        radboudVariantTwo.setStart(1);

        int result = RadboudVariant.Comparators.CHROMOSOME_AND_START.compare(radboudVariant, radboudVariantTwo);
        assertEquals(0, result, "Expected variants to be equal");
    }

    @Test
    void chromosome2GreaterThanChromosome1() {
        radboudVariant.setChromosome("1");
        RadboudVariant radboudVariantTwo = new RadboudVariant();
        radboudVariantTwo.setChromosome("2");

        int result = RadboudVariant.Comparators.CHROMOSOME_AND_START.compare(radboudVariant, radboudVariantTwo);
        assertTrue(result <= -1, "Expected chromosome 2 to be greater than chromosome 1");
    }

    @Test
    void chromosomeXGreaterThanChromosome1() {
        radboudVariant.setChromosome("1");
        RadboudVariant radboudVariantTwo = new RadboudVariant();
        radboudVariantTwo.setChromosome("X");

        int result = RadboudVariant.Comparators.CHROMOSOME_AND_START.compare(radboudVariant, radboudVariantTwo);
        assertTrue(result <= -1, "Expected chromosome X to be greater than chromosome 1");
    }

    @Test
    void chromosomeYGreaterThanChromosomeX() {
        radboudVariant.setChromosome("X");
        RadboudVariant radboudVariantTwo = new RadboudVariant();
        radboudVariantTwo.setChromosome("Y");

        int result = RadboudVariant.Comparators.CHROMOSOME_AND_START.compare(radboudVariant, radboudVariantTwo);
        assertTrue(result <= -1, "Expected chromosome Y to be greater than chromosome X");
    }

    @Test
    void startGreaterThan() {
        radboudVariant.setChromosome("1");
        radboudVariant.setStart(30);
        RadboudVariant radboudVariantTwo = new RadboudVariant();
        radboudVariantTwo.setChromosome("1");
        radboudVariant.setStart(20);

        int result = RadboudVariant.Comparators.CHROMOSOME_AND_START.compare(radboudVariant, radboudVariantTwo);
        assertTrue(result >= 1, "Expected position 30 to be greater than position 20");
    }

    @Test
    void startLessThan() {
        radboudVariant.setChromosome("1");
        radboudVariant.setStart(20);
        RadboudVariant radboudVariantTwo = new RadboudVariant();
        radboudVariantTwo.setChromosome("1");
        radboudVariantTwo.setStart(30);

        int result = RadboudVariant.Comparators.CHROMOSOME_AND_START.compare(radboudVariant, radboudVariantTwo);
        assertTrue(result <= -1, "Expected position 20 to be less than position 30");
    }
}
