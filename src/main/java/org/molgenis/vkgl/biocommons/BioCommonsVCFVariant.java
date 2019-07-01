package org.molgenis.vkgl.biocommons;

import org.molgenis.vkgl.model.VariantType;

public class BioCommonsVCFVariant {
    private String ref;
    private String alt;
    private String chrom;
    private int pos;
    private String type;
    private VariantType variantType;
    private String error;

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public String getChrom() {
        return chrom;
    }

    public int getPos() {
        return pos;
    }

    public String getType() { return type; }

    public String getError() { return error; }
}
