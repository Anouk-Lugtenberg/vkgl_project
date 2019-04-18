package org.molgenis.vkgl.IO;

import java.io.File;

class FileTypeDeterminer {
    FileType determineFileType(File PathName) {
        String name = PathName.getName().toUpperCase();
        if (name.contains("RADBOUD")) {
            return FileType.RADBOUD;
        } else if (name.contains("LUMC")) {
            return FileType.HGVS;
        } else {
            return FileType.CARTAGENIA;
        }
    }
}
