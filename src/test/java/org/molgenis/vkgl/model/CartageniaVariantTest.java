package org.molgenis.vkgl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartageniaVariantTest {
    private CartageniaVariant cartageniaVariant;

    @BeforeEach
    void init() { cartageniaVariant = new CartageniaVariant(); }

    @Test
    void setClassificationBenign() {
        cartageniaVariant.setClassification("BENIGN");
        assertEquals(cartageniaVariant.getClassification(), ClassificationType.BENIGN);
    }

    @Test
    void setClassificationLikelyBenign() {
        cartageniaVariant.setClassification("LIKELY_BENIGN");
        assertEquals(cartageniaVariant.getClassification(), ClassificationType.LIKELY_BENIGN);
    }

    @Test
    void setClassificationVous() {
        cartageniaVariant.setClassification("VOUS");
        assertEquals(cartageniaVariant.getClassification(), ClassificationType.VOUS);
    }

    @Test
    void setClassificationLikelyPathogenic() {
        cartageniaVariant.setClassification("LIKELY_PATHOGENIC");
        assertEquals(cartageniaVariant.getClassification(), ClassificationType.LIKELY_PATHOGENIC);
    }

    @Test
    void setClassificationPathogenic() {
        cartageniaVariant.setClassification("PATHOGENIC");
        assertEquals(cartageniaVariant.getClassification(), ClassificationType.PATHOGENIC);
    }

    @Test
    void setClassificationTypeUnknown() {
        cartageniaVariant.setClassification("unknownClassification");
        assertEquals(cartageniaVariant.getClassification(), ClassificationType.UNKNOWN_TYPE);
    }

    @Test
    void setVariantTypeSNP() {
        cartageniaVariant.setVariantType("snp");
        assertEquals(cartageniaVariant.getVariantType(), VariantType.SNP);
    }

    @Test
    void setVariantTypeInsertion() {
        cartageniaVariant.setVariantType("insertion");
        assertEquals(cartageniaVariant.getVariantType(), VariantType.INSERTION);
    }

    @Test
    void setVariantTypeDeletion() {
        cartageniaVariant.setVariantType("deletion");
        assertEquals(cartageniaVariant.getVariantType(), VariantType.DELETION);
    }

    @Test
    void setVariantTypeSubstitution() {
        cartageniaVariant.setVariantType("substitution");
        assertEquals(cartageniaVariant.getVariantType(), VariantType.DELETION_INSERTION);
    }

    @Test
    void setVariantTypeNotClassified() {
        cartageniaVariant.setVariantType("unknownVariantType");
        assertEquals(cartageniaVariant.getVariantType(), VariantType.NOT_CLASSIFIED);
    }

}
