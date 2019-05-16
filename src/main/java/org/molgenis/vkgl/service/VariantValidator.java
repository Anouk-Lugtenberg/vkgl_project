package org.molgenis.vkgl.service;

import org.molgenis.vkgl.model.VCFVariant;

import java.util.ArrayList;
import java.util.Map;

public class VariantValidator {
    Map<String, ArrayList<VCFVariant>> vcfVariantsPerUMC;

    public VariantValidator(Map<String, ArrayList<VCFVariant>> VCFVariantsPerUMC) {
        this.vcfVariantsPerUMC = VCFVariantsPerUMC;
    }

    public void startValidating() {

    }
}
