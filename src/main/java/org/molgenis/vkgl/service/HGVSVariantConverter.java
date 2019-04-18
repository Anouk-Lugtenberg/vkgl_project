package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.model.HGVSVariant;

import java.util.ArrayList;
import java.util.Map;

//public class HGVSVariantConverter {
//    private static Logger LOGGER = LogManager.getLogger(HGVSVariantConverter.class.getName());
//    Map<String, ArrayList<HGVSVariant>> HGVSVariants = rawData.getHGVSVariants();
//
//    private void convertVariant(HGVSVariants) {
//
//    }
//    for (Map.Entry<String, ArrayList<HGVSVariant>> entry : HGVSVariants.entrySet()) {
//        ArrayList<HGVSVariant> variants = entry.getValue();
//        for (HGVSVariant variant : variants) {
//            createUniqueIdentifier(variant);
//            LOGGER.info("Checking HGVS Syntax for: " + variant.getGenomicDNA());
//            checkHGVSSyntax(variant.getGenomicDNA());
////                System.out.println("variant.getVariantType() = " + variant.getVariantType());
//        }
//    }
//
//    private void createUniqueIdentifier(HGVSVariant) {
//
//    }
//}
