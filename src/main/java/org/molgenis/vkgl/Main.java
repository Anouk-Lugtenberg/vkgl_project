package org.molgenis.vkgl;

import org.molgenis.vkgl.CLI.CLIParser;

public class Main {
    public static void main(String[] args) {
        CLIParser CLIParser = new CLIParser(args);
        CLIParser.parseCLI();
    }
}
