package org.molgenis.vkgl.IO;

import java.io.File;

class FileTypeDeterminer {
    FileType determineFileType(File PathName) {
        String name = PathName.getName();
        if (name.contains("RADBOUD")) {
            return FileType.VCF;
        } else if (name.contains("LUMC")) {
            return FileType.HGVS;
        } else {
            return FileType.CARTAGENIA;
        }
    }
}
