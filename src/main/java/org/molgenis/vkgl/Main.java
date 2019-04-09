package org.molgenis.vkgl;

import org.apache.commons.io.FileUtils;
import org.molgenis.vkgl.IO.FileProcessor;

import java.io.File;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        FileProcessor fileProcessor = new FileProcessor();
        String[] fileExtensions = new String[2];
        try {
            String filePath = args[0];
            fileExtensions[0] = "csv";
            fileExtensions[1] = "txt";
            Iterator iterator = FileUtils.iterateFiles(new File(filePath), fileExtensions, false);
            while (iterator.hasNext()) {
                fileProcessor.processFile((File) iterator.next());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: specify a path");
            e.printStackTrace();
        }
    }
}
