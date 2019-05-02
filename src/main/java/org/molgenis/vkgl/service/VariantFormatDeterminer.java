package org.molgenis.vkgl.service;

public class VariantFormatDeterminer {

    /**
     * Determines the format in which the variant file is written, based on the name of the file.
     * @param file String - file name to determine format of
     * @return VariantFormat
     */
    public VariantFormat getVariantFormat(String file) {
        String name = file.toUpperCase();
        if (name.contains("RADBOUD")) {
            return VariantFormat.RADBOUD;
        } else if (name.contains("LUMC")) {
            return VariantFormat.HGVS;
        } else {
            return VariantFormat.CARTAGENIA;
        }
    }
}
