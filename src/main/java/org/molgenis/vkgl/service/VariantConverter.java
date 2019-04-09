package org.molgenis.vkgl.service;

import org.molgenis.vkgl.IO.RawDataReader;
import org.molgenis.vkgl.model.HGVSVariant;

import java.util.ArrayList;
import java.util.Map;

public class VariantConverter {
    public void convertVariants(RawDataReader rawData) {
        Map<String, ArrayList<HGVSVariant>> HGVSVariants = rawData.getHGVSVariants();
        for (Map.Entry<String, ArrayList<HGVSVariant>> entry : HGVSVariants.entrySet()) {
            ArrayList<HGVSVariant> stuff = entry.getValue();
            for (HGVSVariant variant : stuff) {
                System.out.println("HGVS variant.getClassification() = " + variant.getClassification());
            }
        }
    }
}
