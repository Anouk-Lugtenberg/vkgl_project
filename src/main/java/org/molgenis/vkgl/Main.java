package org.molgenis.vkgl;

import org.molgenis.vkgl.IO.RawFileProcessor;

public class Main {
    public static void main(String[] args) {
        RawFileProcessor rawFileProcessor = new RawFileProcessor();
        rawFileProcessor.start(args);
    }
}
