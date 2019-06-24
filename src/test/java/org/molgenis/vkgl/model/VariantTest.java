package org.molgenis.vkgl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariantTest {
    private Variant variant;

    @BeforeEach
    void init() { variant = new Variant(); }

    @Test
    void setVariantTypeSNP() {
        variant.setVariantType(">");
        assertEquals(variant.getVariantType(), VariantType.SNP);
    }

    @Test
    void setVariantTypeDeletionInsertion() {
        variant.setVariantType("delins");
        assertEquals(variant.getVariantType(), VariantType.DELETION_INSERTION);
    }

    @Test
    void setVariantTypeDeletion() {
        variant.setVariantType("del");
        assertEquals(variant.getVariantType(), VariantType.DELETION);
    }

    @Test
    void setVariantTypeInsertion() {
        variant.setVariantType("ins");
        assertEquals(variant.getVariantType(), VariantType.INSERTION);
    }

    @Test
    void setVariantTypeDuplication() {
        variant.setVariantType("dup");
        assertEquals(variant.getVariantType(), VariantType.DUPLICATION);
    }

    @Test
    void setVariantTypeNotClassified() {
        variant.setVariantType("notClassified");
        assertEquals(variant.getVariantType(), VariantType.NOT_CLASSIFIED);
    }

    @Test
    void setChromosomeWithChrRemoved() {
        variant.setChromosome("chr1");
        assertEquals(variant.getChromosome(), "1");
    }
}
