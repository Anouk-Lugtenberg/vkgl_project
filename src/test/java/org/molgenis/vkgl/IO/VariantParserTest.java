package org.molgenis.vkgl.IO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.RadboudVariant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariantParserTest {

    @TempDir
    static Path directoryWithVariants;

//    @Test
//    void parseRadboudSunny() throws IOException {
//        String stringSunnyRadboudFormat = "chr1\t" +
//                "1\t" +
//                "2\t" +
//                "G\t" +
//                "C\t" +
//                "geneName\t" +
//                "cDNANotation\t" +
//                "transcript\t" +
//                "proteinNotation\t\t\t" +
//                "exon\t\t" +
//                "1";
//
//        Path radboudVariantsPath = Files.createFile(directoryWithVariants.resolve("radboud.txt"));
//        File radboudVariantsFile = radboudVariantsPath.toFile();
//
//        BufferedWriter writer = new BufferedWriter(new FileWriter(radboudVariantsFile));
//        writer.write(stringSunnyRadboudFormat);
//        writer.close();
//
//        String expectedChromosome = "1";
//        int expectedStart = 1;
//        int expectedStop = 2;
//        String expectedREF = "G";
//        String expectedALT = "C";
//        String expectedGeneName = "geneName";
//        String expectedCDNANotation = "cDNANotation";
//        String expectedTranscript = "transcript";
//        String expectedproteinNotation = "proteinNotation";
//        String expectedExon = "exon";
//        ClassificationType expectedClassification = ClassificationType.BENIGN;
//
//        VariantParser variantParser = new VariantParser();
//        ArrayList<RadboudVariant> actualRadboudVariants = variantParser.parseRadboud(radboudVariantsFile);
//        RadboudVariant actualRadboudVariant = actualRadboudVariants.get(0);
//
//        assertEquals(actualRadboudVariant.getChromosome(), expectedChromosome);
//        assertEquals(actualRadboudVariant.getStart(), expectedStart);
//        assertEquals(actualRadboudVariant.getStop(), expectedStop);
//        assertEquals(actualRadboudVariant.getREF(), expectedREF);
//        assertEquals(actualRadboudVariant.getALT(), expectedALT);
//        assertEquals(actualRadboudVariant.getGeneName(), expectedGeneName);
//        assertEquals(actualRadboudVariant.getcDNANotation(), expectedCDNANotation);
//        assertEquals(actualRadboudVariant.getTranscript(), expectedTranscript);
//        assertEquals(actualRadboudVariant.getProteinNotation(), expectedproteinNotation);
//        assertEquals(actualRadboudVariant.getExon(), expectedExon);
//        assertEquals(actualRadboudVariant.getClassification(), expectedClassification);
//    }
//
//    @Test
//    void parseRadboudMissingColumn() throws IOException {
//        String missingColumn = "chr1\t" +
//                "1\t" +
//                "2\t" +
//                "G\t" +
//                "C\t" +
//                "geneName\t" +
//                "cDNANotation\t" +
//                "transcript\t" +
//                "proteinNotation\t\t\t" +
//                "exon\t\t";
//
//        Path radboudVariantsPath = Files.createFile(directoryWithVariants.resolve("radboud.txt"));
//        File radboudVariantsFile = radboudVariantsPath.toFile();
//
//        BufferedWriter writer = new BufferedWriter(new FileWriter(radboudVariantsFile));
//        writer.write(missingColumn);
//        writer.close();
//
//        VariantParser variantParser = new VariantParser();
//        ArrayList<RadboudVariant> actualRadboudVariants = variantParser.parseRadboud(radboudVariantsFile);
//        ArrayList<RadboudVariant> expectedRadboudVariants = new ArrayList<>();
//        assertEquals(actualRadboudVariants, expectedRadboudVariants);
//    }
}
