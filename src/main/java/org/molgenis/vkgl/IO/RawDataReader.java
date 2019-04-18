package org.molgenis.vkgl.IO;

import org.apache.commons.io.FilenameUtils;
import org.molgenis.vkgl.model.CartageniaVariant;
import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.RadboudVariant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RawDataReader {
    private Map<String, ArrayList<RadboudVariant>> RadboudVariants = new HashMap<>();
    private Map<String, ArrayList<HGVSVariant>> HGVSVariants = new HashMap<>();
    private Map<String, ArrayList<CartageniaVariant>> CartageniaVariants = new HashMap<>();
    private static VariantParser variantParser = new VariantParser();

    void readFile(File file, FileType fileType) {
        //todo find solution if file has headers
        String nameUMC = FilenameUtils.removeExtension(file.getName());
        switch(fileType) {
            case RADBOUD:
                RadboudVariants.put(nameUMC, variantParser.parseRadboud(file));
                break;
            case HGVS:
                HGVSVariants.put(nameUMC, variantParser.parseHGVS(file));
                break;
            case CARTAGENIA:
                CartageniaVariants.put(nameUMC, variantParser.parseCartagenia(file));
                break;
        }
    }

    public Map<String, ArrayList<RadboudVariant>> getVCFVariants() { return RadboudVariants; }
    public Map<String, ArrayList<HGVSVariant>> getHGVSVariants() { return HGVSVariants; }
    public Map<String, ArrayList<CartageniaVariant>> getCartageniaVariants() { return CartageniaVariants; }
}

