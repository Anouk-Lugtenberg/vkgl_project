package org.molgenis.vkgl;

import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.molgenis.vkgl.IO.RawFileProcessor;

import static org.apache.logging.log4j.core.lookup.MainMapLookup.setMainArguments;

public class Main {
    public static void main(String[] args) {
        try {
            setMainArguments(args);
            MainMapLookup mml = new MainMapLookup();
            //Log4j2 contains a bug for command line arguments prefixed with "-".
            //So argument starting with "-i" or "--inputDirectory" are looked up, and set as "inputDirectory"
            //https://issues.apache.org/jira/browse/LOG4J2-1013
            String inputDirectory = mml.lookup("-i");
            if (inputDirectory.length() == 0) {
                inputDirectory = mml.lookup("--inputDirectory");
            }
            String[] argsForLog = new String[2];
            argsForLog[0] = "inputDirectory";
            argsForLog[1] = inputDirectory;
            setMainArguments(argsForLog);
        } catch (NullPointerException e) {
            //null pointer exception will be handled in CLIParser.java
        }

        RawFileProcessor rawFileProcessor = new RawFileProcessor();
        rawFileProcessor.start(args);
    }
}
