package org.molgenis.vkgl.IO;

import org.apache.commons.io.FilenameUtils;
import org.molgenis.vkgl.model.CartageniaVariant;
import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.VCFVariant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RawDataReader {
    public Map<String, ArrayList<VCFVariant>> VCFVariants = new HashMap<>();
    public Map<String, ArrayList<HGVSVariant>> HGVSVariants = new HashMap<>();
    public Map<String, ArrayList<CartageniaVariant>> CartageniaModels = new HashMap<>();

    public void readFile(File file, FileType fileType) {
        //todo find solution if file has headers
        String nameUMC = FilenameUtils.removeExtension(file.getName());
        switch(fileType) {
            case VCF:
                VCFFileProcessor vcfFileProcessor = new VCFFileProcessor();
                VCFVariants.put(nameUMC, vcfFileProcessor.processVCFFile(file));
                break;
            case HGVS:
                HGVSFileProcessor hgvsFileProcessor = new HGVSFileProcessor();
                HGVSVariants.put(nameUMC, hgvsFileProcessor.processHGVSFile(file));
                break;
            case CARTAGENIA:
                CartageniaFileProcessor cartageniaFileProcessor = new CartageniaFileProcessor();
                CartageniaModels.put(nameUMC, cartageniaFileProcessor.processFile(file));
                break;
        }
    }

    public Map<String, ArrayList<VCFVariant>> getVCFVariants() { return VCFVariants; }
    public Map<String, ArrayList<HGVSVariant>> getHGVSVariants() { return HGVSVariants; }
    public Map<String, ArrayList<CartageniaVariant>> getCartageniaVariants() { return CartageniaModels; }
}

