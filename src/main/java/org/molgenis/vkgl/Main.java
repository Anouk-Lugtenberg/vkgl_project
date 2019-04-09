package org.molgenis.vkgl;

import org.molgenis.vkgl.IO.RawFileProcessor;

public class Main {
    public static void main(String[] args) {
        RawFileProcessor rawFileProcessor = new RawFileProcessor();
        try {
            rawFileProcessor.processRawFiles(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: specify a path");
        }
    }
}
