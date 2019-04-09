package org.molgenis.vkgl.IO;

import java.io.File;

public class FileTypeDeterminer {
    public FileType determineFileType(File PathName) {
        String name = PathName.getName();
        if (name.contains("RADBOUD")) {
            return FileType.VCF;
        } if (name.contains("LUMC")) {
            return FileType.HGVS;
        } else {
            return FileType.CARTAGENIA;
        }
    }
}
