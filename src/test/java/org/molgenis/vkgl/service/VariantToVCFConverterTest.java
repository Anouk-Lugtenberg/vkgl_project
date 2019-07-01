package org.molgenis.vkgl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vkgl.biocommons.BioCommonsVCFVariant;
import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.RadboudVariant;
import org.molgenis.vkgl.model.VCFVariant;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VariantToVCFConverterTest {
    private BioCommonsVCFVariant bioCommonsVCFVariant;
    private VariantToVCFConverter variantToVCFConverter;

    @BeforeEach
    void setBioCommonsVariant() {
        String jsonResponse = "[{\"ref\": \"C\", \"alt\": \"CCCGGG\", \"chrom\": \"1\", \"pos\": 8384561, \"type\": \"ins\"}]";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            BioCommonsVCFVariant[] bioCommonsVCFVariantArray = objectMapper.readValue(jsonResponse, BioCommonsVCFVariant[].class);
            bioCommonsVCFVariant = bioCommonsVCFVariantArray[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        variantToVCFConverter = new VariantToVCFConverter();
    }

    @Test
    void sameVariants() {
        VCFVariant vcfVariant = new VCFVariant("1",8384561, "C", "CCCGGG", ClassificationType.BENIGN, new RadboudVariant());
        boolean sameVariant = variantToVCFConverter.sameVariant(bioCommonsVCFVariant, vcfVariant);
        assertTrue(sameVariant, "Variants should be the same");
    }

    @Test
    void sameVariantNotSameChromosome() {
        VCFVariant vcfVariant = new VCFVariant("2", 8384561, "C", "CCCGGG", ClassificationType.BENIGN, new RadboudVariant());
        boolean sameVariant = variantToVCFConverter.sameVariant(bioCommonsVCFVariant, vcfVariant);
        assertFalse(sameVariant, "Chromosome of variants is different");
    }

    @Test
    void sameVariantsNotSamePosition() {
        VCFVariant vcfVariant = new VCFVariant("1", 8, "C", "CCCGGG", ClassificationType.BENIGN, new RadboudVariant());
        boolean sameVariant = variantToVCFConverter.sameVariant(bioCommonsVCFVariant, vcfVariant);
        assertFalse(sameVariant, "Position of variants is different");
    }

    @Test
    void sameVariantsDifferentRef() {
        VCFVariant vcfVariant = new VCFVariant("1", 8384561, "A", "CCCGGG", ClassificationType.BENIGN, new RadboudVariant());
        boolean sameVariant = variantToVCFConverter.sameVariant(bioCommonsVCFVariant, vcfVariant);
        assertFalse(sameVariant, "Reference of variants is different");
    }

    @Test
    void sameVariantsDifferentAlt() {
        VCFVariant vcfVariant = new VCFVariant("1", 8384561, "C", "CG", ClassificationType.BENIGN, new RadboudVariant());
        boolean sameVariant = variantToVCFConverter.sameVariant(bioCommonsVCFVariant, vcfVariant);
        assertFalse(sameVariant, "Alternative of variants is different");
    }

    @Test
    void sameVariantsTwoDifferences() {
        VCFVariant vcfVariant = new VCFVariant("2", 8, "C", "CCCGGG", ClassificationType.BENIGN, new RadboudVariant());
        boolean sameVariant = variantToVCFConverter.sameVariant(bioCommonsVCFVariant, vcfVariant);
        assertFalse(sameVariant, "Two differences in the variants");
    }

    @Test
    void sameVariantsThreeDifferences() {
        VCFVariant vcfVariant = new VCFVariant("3", 9, "A", "CCCGGG", ClassificationType.BENIGN, new RadboudVariant());
        boolean sameVariant = variantToVCFConverter.sameVariant(bioCommonsVCFVariant, vcfVariant);
        assertFalse(sameVariant, "Three differences in the variants");
    }

    @Test
    void sameVariantsFourDifferences() {
        VCFVariant vcfVariant = new VCFVariant("3", 9, "A", "G", ClassificationType.BENIGN, new RadboudVariant());
        boolean sameVariant = variantToVCFConverter.sameVariant(bioCommonsVCFVariant, vcfVariant);
        assertFalse(sameVariant, "Four differences in the variants");
    }
}
