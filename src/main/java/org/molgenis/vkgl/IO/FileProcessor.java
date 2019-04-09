package org.molgenis.vkgl.IO;

import org.molgenis.vkgl.service.VariantConverter;

import java.io.File;

public class FileProcessor {
    private static RawDataReader data = new RawDataReader();
    public void processFile(File pathName) {
        FileTypeDeterminer type = new FileTypeDeterminer();
        FileType fileType = type.determineFileType(pathName);
        System.out.println("Processing: " + pathName);
        data.ReadFile(pathName, fileType);
        VariantConverter variantConverter = new VariantConverter();
        variantConverter.convertVariants(data);
    }
}
