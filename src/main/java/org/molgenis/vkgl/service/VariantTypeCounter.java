package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.model.variants.Variant;

import java.util.ArrayList;
import java.util.Map;

public class VariantTypeCounter {
    private static Logger LOGGER = LogManager.getLogger(VariantToVCFConverter.class.getName());

    public VariantTypeCounter(Map<String, ArrayList<Variant>> variantsForUMCs) {
        for (Map.Entry<String, ArrayList<Variant>> entry : variantsForUMCs.entrySet()) {
            String nameUMC = entry.getKey();
            ArrayList<Variant> variants = entry.getValue();
            countVariantTypeOccurrences(nameUMC, variants);
        }
    }

    private void countVariantTypeOccurrences(String nameUMC, ArrayList<? extends Variant> variants) {
        int SNPCount = 0;
        int insertionCount = 0;
        int deletionCount = 0;
        int duplicationCount = 0;
        int deletionInsertionCount= 0;
        int notClassifiedCount = 0;

        for (Variant variant : variants) {
            switch (variant.getVariantType()) {
                case SNP:
                    SNPCount++;
                    break;
                case INSERTION:
                    insertionCount++;
                    break;
                case DELETION:
                    deletionCount++;
                    break;
                case DUPLICATION:
                    duplicationCount++;
                    break;
                case DELETION_INSERTION:
                    deletionInsertionCount++;
                    break;
                case NOT_CLASSIFIED:
                    notClassifiedCount++;
                    break;
            }
        }
        LOGGER.info("\nVariant type occurrences for: " + nameUMC + "\n" +
                "SNPs:\t\t\t" + SNPCount + "\n" +
                "Insertions:\t\t" + insertionCount + "\n" +
                "Deletions:\t\t" + deletionCount + "\n" +
                "Duplications:\t" + duplicationCount + "\n" +
                "Del_ins:\t\t" + deletionInsertionCount + "\n" +
                "Not classified:\t" + notClassifiedCount);
    }


}
