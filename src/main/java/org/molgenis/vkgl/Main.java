package org.molgenis.vkgl;
import org.molgenis.vkgl.IO.RawFileProcessor;
import org.molgenis.vkgl.service.VariantErrorCounter;

import java.util.Arrays;

import static org.apache.logging.log4j.core.lookup.MainMapLookup.setMainArguments;

public class Main {
    public static void main(String[] args) {
        //Log4j2 contains a bug for command line arguments prefixed with "-".
        //So argument starting with "-i" or "--inputDirectory" are looked up, and set as "inputDirectory"
        //https://issues.apache.org/jira/browse/LOG4J2-1013

        String inputDirectory;

        //if -i command not given, longer command --inputDirectory is checked for availability
        if (Arrays.asList(args).contains("-i")) {
            int index = Arrays.asList(args).indexOf("-i");
            inputDirectory = Arrays.asList(args).get(index + 1);
        } else if (Arrays.asList(args).contains("--inputDirectory")) {
            int index = Arrays.asList(args).indexOf("--inputDirectory");
            inputDirectory = Arrays.asList(args).get(index + 1);
        } else {
            inputDirectory = "logs";
        }

        String[] argsForLog = new String[2];
        argsForLog[0] = "inputDirectory";
        argsForLog[1] = inputDirectory;

        setMainArguments(argsForLog);

        RawFileProcessor rawFileProcessor = new RawFileProcessor();
        rawFileProcessor.start(args);
        VariantErrorCounter.writeErrors();
    }
}
