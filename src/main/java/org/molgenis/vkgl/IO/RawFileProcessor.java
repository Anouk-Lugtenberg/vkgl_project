package org.molgenis.vkgl.IO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.service.VariantConverter;

import java.io.File;
import java.util.Iterator;

public class RawFileProcessor {
    private static final Logger LOGGER = LogManager.getLogger(RawFileProcessor.class.getName());
    private static RawDataReader rawDataReader = new RawDataReader();
    private static FileTypeDeterminer fileTypeDeterminer = new FileTypeDeterminer();
    private static VariantConverter variantConverter = new VariantConverter();

    public void processRawFiles(File filePath) {
        String[] fileExtensions = new String[2];
        //File extensions from the raw files.
        fileExtensions[0] = "csv";
        fileExtensions[1] = "txt";
        Iterator iterator = FileUtils.iterateFiles(filePath, fileExtensions, false);
        while (iterator.hasNext()) {
            Object file = iterator.next();
            LOGGER.info("Processing file: " + file);
            FileType fileType = getFileType((File) file);
            rawDataReader.readFile((File) file, fileType);
        }
        variantConverter.convertVariants(rawDataReader);

    }

    private FileType getFileType(File file) {
        return fileTypeDeterminer.determineFileType(file);
    }
}
