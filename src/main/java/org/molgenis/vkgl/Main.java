package org.molgenis.vkgl;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.molgenis.vkgl.IO.RawFileProcessor;

import static org.apache.logging.log4j.core.lookup.MainMapLookup.setMainArguments;

public class Main {
    public static void main(String[] args) {
        try {
            MainMapLookup mml = new MainMapLookup();
            //Log4j2 contains a bug for command line arguments prefixed with "-".
            //So argument starting with "-i" or "--inputDirectory" are looked up, and set as "inputDirectory"
            //https://issues.apache.org/jira/browse/LOG4J2-1013

            //-i command is checked first
            String inputDirectory = mml.lookup("-i");
            //if -i command not given, longer command --inputDirectory is checked for availability
            if (inputDirectory.length() == 0) {
                inputDirectory = mml.lookup("--inputDirectory");
            }

            //if input directory is available, it is set to be used by the logger and the program continues
            if (inputDirectory.length() != 0) {
                System.out.println("inputDirectory = " + inputDirectory);
                String[] argsForLog = new String[2];
                argsForLog[0] = "inputDirectory";
                argsForLog[1] = inputDirectory;
                setMainArguments(argsForLog);
            }
        } catch (NullPointerException e) {
            //The errors which are caused when the user did not specify an input file are handled
            //in CLI parser. Try block is only for setting the place for the log-file not for handling errors.
            RawFileProcessor rawFileProcessor = new RawFileProcessor();
            rawFileProcessor.start(args);
        }
    }
}
