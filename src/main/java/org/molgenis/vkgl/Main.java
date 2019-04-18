package org.molgenis.vkgl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.IO.RawFileProcessor;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

    /**
     * @param args arguments from the command line
     * @throws ArrayIndexOutOfBoundsException if user has not specified a path in the arguments
     */
    public static void main(String[] args) {
        CLIParser CLIParser = new CLIParser(args);
        CLIParser.parseCLI();
//        RawFileProcessor rawFileProcessor = new RawFileProcessor();
//        try {
//            rawFileProcessor.processRawFiles(args[0]);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            LOGGER.error("Please specify a path for the raw files. ", e);
//        }
    }
}
