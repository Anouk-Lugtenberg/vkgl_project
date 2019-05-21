package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.model.VCFVariant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VariantValidator {
    private Map<String, ArrayList<VCFVariant>> vcfVariantsPerUMC;
    private Logger LOGGER = LogManager.getLogger(VariantValidator.class.getName());

    public VariantValidator(Map<String, ArrayList<VCFVariant>> VCFVariantsPerUMC) {
        this.vcfVariantsPerUMC = VCFVariantsPerUMC;
    }

    public void startValidating() {
        for (Map.Entry<String, ArrayList<VCFVariant>> entry : vcfVariantsPerUMC.entrySet()) {
            ArrayList<VCFVariant> vcfVariants = entry.getValue();
            checkDoubleVCFNotations(vcfVariants);
        }
    }

    private void checkDoubleVCFNotations(List<VCFVariant> vcfVariants) {
        int currentPosition = 0;
        List<Integer> itemsToBeRemoved = new ArrayList<>();
        for (VCFVariant variant : vcfVariants) {
            if (currentPosition > 0 && currentPosition < vcfVariants.size()) {
                if (variant.getPosition() == vcfVariants.get(currentPosition - 1).getPosition()) {
                    VCFVariant expectedDoubleVariant = vcfVariants.get(currentPosition - 1);
                    //If ALT, REF and classification are the same, variants are treated as the same entry to the VCF record
                    //and one of them is removed from the VCF variants list.
                    if (variant.getALT().equals(expectedDoubleVariant.getALT())
                            && variant.getREF().equals(expectedDoubleVariant.getREF())) {
                        LOGGER.info(variant.getRawVariant().getLineNumber() + ": " + variant.getRawVariant().getRawInformation());
                        LOGGER.info(expectedDoubleVariant.getRawVariant().getLineNumber() + ": " + expectedDoubleVariant.getRawVariant().getRawInformation());
                        if (variant.getClassification().equals(expectedDoubleVariant.getClassification())) {
                            LOGGER.info("Variants above are duplicates in VCF record (same position, REF, ALT and classification). Removing one of them.\n");
                            itemsToBeRemoved.add(currentPosition - itemsToBeRemoved.size());
                        } else {
                            LOGGER.info("Variants above are duplicates in VCF record (same position, REF, ALT), but do not have the same classification. Flagging as invalid.\n");
                            variant.setValidVariant(false);
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
