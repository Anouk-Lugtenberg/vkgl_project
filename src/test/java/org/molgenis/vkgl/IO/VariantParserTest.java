package org.molgenis.vkgl.IO;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.molgenis.vkgl.LogAppenderResource;
import org.molgenis.vkgl.model.CartageniaVariant;
import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.RadboudVariant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.containsString;

class VariantParserTest {

    private LogAppenderResource appender = new LogAppenderResource(LogManager.getLogger(VariantParser.class.getName()));
    private VariantParser variantParser = new VariantParser();

    @BeforeEach
    void init() { appender.before(); }

    @AfterEach
    void close() { appender.after(); }

    @Test
    void parseRadboudVariantSunny() {
        File radboudFile = new File(getClass().getResource("/variants/singleRadboudVariantSunny.txt").getFile());
        variantParser.parseFile(radboudFile);

        RadboudVariant expectedRadboudVariant = new RadboudVariant();
        expectedRadboudVariant.setChromosome("chr1");
        expectedRadboudVariant.setStart(1);
        expectedRadboudVariant.setStop(2);
        expectedRadboudVariant.setREF("REF");
        expectedRadboudVariant.setALT("ALT");
        expectedRadboudVariant.setGeneName("geneName");
        expectedRadboudVariant.setcDNANotation("cDNANotation");
        expectedRadboudVariant.setTranscript("transcript");
        expectedRadboudVariant.setProteinNotation("proteinNotation");
        expectedRadboudVariant.setExon("exon");
        expectedRadboudVariant.setClassification("1");
        expectedRadboudVariant.setVariantType(expectedRadboudVariant.getcDNANotation());
        expectedRadboudVariant.setIdentifier();
        expectedRadboudVariant.setLineNumber(1);

        ArrayList<RadboudVariant> expectedListRadboudVariants = new ArrayList<>();
        expectedListRadboudVariants.add(expectedRadboudVariant);
        Map<String, ArrayList<RadboudVariant>> expectedMapRadboudVariants = new HashMap<>();
        expectedMapRadboudVariants.put("singleRadboudVariantSunny", expectedListRadboudVariants);

        assertThat(variantParser.getRadboudVariants(), sameBeanAs(expectedMapRadboudVariants).ignoring("singleRadboudVariantSunny.rawInformation"));
    }

    @Test
    void parseHGVSVariantSunny() {
        File HGVSFile = new File(getClass().getResource("/variants/HGVSVariantSunny.txt").getFile());
        variantParser.parseFile(HGVSFile);

        HGVSVariant expectedHGVSVariant = new HGVSVariant();
        expectedHGVSVariant.setReferenceSequence("hg19");
        expectedHGVSVariant.setChromosome("1");
        expectedHGVSVariant.setGenomicDNA("NC_000001.10:g.874714C>T");
        expectedHGVSVariant.setClassification("-?");
        expectedHGVSVariant.setGeneName("NOC2L");
        expectedHGVSVariant.setcDNANotation("NM_015658.3:c.*5360G>A");
        expectedHGVSVariant.setProteinNotation("NM_015658.3:p.(=)");
        expectedHGVSVariant.setVariantType(expectedHGVSVariant.getGenomicDNA());
        expectedHGVSVariant.setLineNumber(1);

        ArrayList<HGVSVariant> expectedListHGVSVariants = new ArrayList<>();
        expectedListHGVSVariants.add(expectedHGVSVariant);
        Map<String, ArrayList<HGVSVariant>> expectedMapHGVSVariants = new HashMap<>();
        expectedMapHGVSVariants.put("HGVSVariantSunny", expectedListHGVSVariants);

        assertThat(variantParser.getHGVSVariants(), sameBeanAs(expectedMapHGVSVariants).ignoring("HGVSVariantSunny.rawInformation"));
    }

    @Test
    void parseCartageniaVariantSunny() {
        File CartageniaFile = new File(getClass().getResource("/variants/CartageniaVariantSunny.txt").getFile());
        variantParser.parseFile(CartageniaFile);

        CartageniaVariant expectedCartageniaVariant = new CartageniaVariant();
        expectedCartageniaVariant.setTimestamp("2018-11-29T09:06:15Z");
        expectedCartageniaVariant.setId("aaaacz26w76qj6qwh3vxmyaaa4");
        expectedCartageniaVariant.setChromosome("1");
        expectedCartageniaVariant.setStart(237754110);
        expectedCartageniaVariant.setStop(237754110);
        expectedCartageniaVariant.setREF("C");
        expectedCartageniaVariant.setALT("G");
        expectedCartageniaVariant.setGeneName("RYR2");
        expectedCartageniaVariant.setTranscript("NM_001035.2");
        expectedCartageniaVariant.setcDNANotation("c.3978C>G");
        expectedCartageniaVariant.setProteinNotation("p.=");
        expectedCartageniaVariant.setExon("31");
        expectedCartageniaVariant.setVariantType("snp");
        expectedCartageniaVariant.setLocation("exonic");
        expectedCartageniaVariant.setEffect("synonymous");
        expectedCartageniaVariant.setClassification("LIKELY_BENIGN");
        expectedCartageniaVariant.setLastUpdatedOn("2016-06-26 09:02:41");
        expectedCartageniaVariant.setLineNumber(1);

        ArrayList<CartageniaVariant> expectedListCartageniaVariants = new ArrayList<>();
        expectedListCartageniaVariants.add(expectedCartageniaVariant);
        Map<String, ArrayList<CartageniaVariant>> expectedMapCartageniaVariants = new HashMap<>();
        expectedMapCartageniaVariants.put("CartageniaVariantSunny", expectedListCartageniaVariants);

        assertThat(variantParser.getCartageniaVariants(), sameBeanAs(expectedMapCartageniaVariants).ignoring("CartageniaVariantSunny.rawInformation"));
    }

    @Test
    void parseVariantMissingColumn() {
        File radboudFile = new File(getClass().getResource("/variants/singleRadboudVariantMissingColumn.txt").getFile());
        variantParser.parseFile(radboudFile);
        assertThat(appender.getOutput(), containsString("Line 2 of " + radboudFile.toString() + " could not be processed. Please check the syntax."));
    }

    @Test
    void parseVariantWithHeader() {
        File radboudFile = new File(getClass().getResource("/variants/radboudVariantWithHeader.txt").getFile());
        variantParser.parseFile(radboudFile);
        assertThat(appender.getOutput(), containsString("Line 1 of " + radboudFile.toString() + " could not be processed. Probably header, skipping line."));
    }

    @Test
    void parseFileFileDoesNotExist() {
        File fileDoesNotExist = new File("/does/not/exist");
        variantParser.parseFile(fileDoesNotExist);
        assertThat(appender.getOutput(), containsString(fileDoesNotExist.toString() + " (No such file or directory)"));
    }



}
