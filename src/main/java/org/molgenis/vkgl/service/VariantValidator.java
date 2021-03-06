package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.model.VCFVariant;

import java.util.*;

public class VariantValidator {
    private Map<String, ArrayList<VCFVariant>> vcfVariantsPerUMC;
    private Logger LOGGER = LogManager.getLogger(VariantValidator.class.getName());

    public VariantValidator(Map<String, ArrayList<VCFVariant>> VCFVariantsPerUMC) {
        this.vcfVariantsPerUMC = VCFVariantsPerUMC;
    }

    /**
     * Checks for double VCF entries (same position, REF and ALT) in a List of VCFVariants. If classification
     * is the same, one of the entries is removed from the list. If classification is not the same, the variants are both
     * flagged as invalid.
     */
    public void checkDoubleVCFNotations() {
        for (Map.Entry<String, ArrayList<VCFVariant>> entry : vcfVariantsPerUMC.entrySet()) {
            List<VCFVariant> vcfVariants = entry.getValue();
            int currentPosition = 0;
            List<Integer> itemsToBeRemoved = new ArrayList<>();
            for (VCFVariant variant : vcfVariants) {
                if (currentPosition > 0 && currentPosition < vcfVariants.size()) {
                    //Check if position is the same as previous variant.
                    if (variant.getPosition() == vcfVariants.get(currentPosition - 1).getPosition()) {
                        VCFVariant expectedDuplicateVariant = vcfVariants.get(currentPosition - 1);
                        //If ALT, REF and classification are the same, variants are treated as the same entry to the VCF record
                        //and one of them is removed from the VCF variants list.
                        if (variant.getALT().equals(expectedDuplicateVariant.getALT())
                                && variant.getREF().equals(expectedDuplicateVariant.getREF())) {
                            LOGGER.info(expectedDuplicateVariant.getRawVariant().getLineNumber() + ": " + expectedDuplicateVariant.getRawVariant().getRawInformation());
                            LOGGER.info(variant.getRawVariant().getLineNumber() + ": " + variant.getRawVariant().getRawInformation());
                            //Variants are only a real duplication of eachother when their classification is the same as well.
                            if (variant.getClassification().equals(expectedDuplicateVariant.getClassification())) {
                                LOGGER.info("Variants above are duplicates in VCF record (same position, REF, ALT and classification). Removing one of them.\n");
                                //When the items are removed from the list one by one, their position changes as well.
                                //This is corrected by extracting the number of items which are removed beforehand from
                                //the current position.
                                itemsToBeRemoved.add(currentPosition - itemsToBeRemoved.size());
                                VariantErrorCounter.removedDuplicatesFromVCFVariantList();
                            } else {
                                LOGGER.info("Variants above are duplicates in VCF record (same position, REF, ALT), but do not have the same classification. Flagging as invalid.\n");
                                variant.setValidVariant(false);
                                expectedDuplicateVariant.setValidVariant(false);
                                VariantErrorCounter.duplicatesWhichDoNotHaveTheSameClassification();
                            }
                        }
                    }
                }
                currentPosition++;
            }
            for (int itemToRemove : itemsToBeRemoved) {
                vcfVariants.remove(itemToRemove);
            }
        }
    }
}
