package org.molgenis.vkgl.service;

import java.io.File;

public class VariantFormatDeterminer {
    private String file;

    public VariantFormatDeterminer(String file) {
        this.file = file;
    }
    /**
     * Determines the format in which the variant file is written, based on the name of the file.
     * @return VariantFormat
     */
    public VariantFormat getVariantFormat() {
        String[] fileName = file.split(File.separator);
        String name = fileName[fileName.length-1].toUpperCase();
        if (name.contains("RADBOUD")) {
            return VariantFormat.RADBOUD;
        } else if (name.contains("LUMC")||name.contains("HGVS")) {
            return VariantFormat.HGVS;
        } else {
            return VariantFormat.CARTAGENIA;
        }
    }
}
