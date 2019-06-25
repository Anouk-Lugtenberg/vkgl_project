package org.molgenis.vkgl.service;

import com.shazam.shazamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.RadboudVariant;
import org.molgenis.vkgl.model.VCFVariant;

import java.io.File;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

public class RadboudToVCFConverterTest {

    @BeforeEach
    void setUpFasta() {
        CLIParser.fastaFileDirectory = new File(getClass().getResource("/fasta/fake_fasta.fasta").getFile());
    }

    @Test
    void convertSimpleSNP() {
        RadboudVariant radboudVariantSimpleSNP = new RadboudVariant();
        radboudVariantSimpleSNP.setChromosome("chr1");
        radboudVariantSimpleSNP.setStart(4);
        radboudVariantSimpleSNP.setStop(4);
        radboudVariantSimpleSNP.setREF("T");
        radboudVariantSimpleSNP.setALT("G");
        radboudVariantSimpleSNP.setClassification("class 1");
        radboudVariantSimpleSNP.setVariantType(">");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantSimpleSNP);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 4, "T", "G", ClassificationType.BENIGN, radboudVariantSimpleSNP);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertSimpleInsertion() {
        RadboudVariant radboudVariantSimpleInsertion = new RadboudVariant();
        radboudVariantSimpleInsertion.setChromosome("chr1");
        radboudVariantSimpleInsertion.setStart(4);
        radboudVariantSimpleInsertion.setStop(4);
        radboudVariantSimpleInsertion.setREF("");
        radboudVariantSimpleInsertion.setALT("GCA");
        radboudVariantSimpleInsertion.setClassification("class 1");
        radboudVariantSimpleInsertion.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantSimpleInsertion);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 4, "T", "TGCA", ClassificationType.BENIGN, radboudVariantSimpleInsertion);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionOneNucleotide() {
        RadboudVariant radboudVariantInsertionOneNucleotide = new RadboudVariant();
        radboudVariantInsertionOneNucleotide.setChromosome("chr1");
        radboudVariantInsertionOneNucleotide.setStart(4);
        radboudVariantInsertionOneNucleotide.setStop(4);
        radboudVariantInsertionOneNucleotide.setREF("");
        radboudVariantInsertionOneNucleotide.setALT("C");
        radboudVariantInsertionOneNucleotide.setClassification("class 1");
        radboudVariantInsertionOneNucleotide.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantInsertionOneNucleotide);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 4, "T", "TC", ClassificationType.BENIGN, radboudVariantInsertionOneNucleotide);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionPositionShuffledToTheLeft() {
        RadboudVariant radboudVariantShuffleToLeft = new RadboudVariant();
        radboudVariantShuffleToLeft.setChromosome("chr1");
        radboudVariantShuffleToLeft.setStart(10);
        radboudVariantShuffleToLeft.setStop(10);
        radboudVariantShuffleToLeft.setREF("");
        radboudVariantShuffleToLeft.setALT("G");
        radboudVariantShuffleToLeft.setClassification("class 1");
        radboudVariantShuffleToLeft.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantShuffleToLeft);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 7, "C", "CG", ClassificationType.BENIGN, radboudVariantShuffleToLeft);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionTwoSameNucleotides() {
        RadboudVariant radboudVariantTwoSameNucleotides = new RadboudVariant();
        radboudVariantTwoSameNucleotides.setChromosome("chr1");
        radboudVariantTwoSameNucleotides.setStart(10);
        radboudVariantTwoSameNucleotides.setStop(10);
        radboudVariantTwoSameNucleotides.setREF("");
        radboudVariantTwoSameNucleotides.setALT("GG");
        radboudVariantTwoSameNucleotides.setClassification("class 1");
        radboudVariantTwoSameNucleotides.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantTwoSameNucleotides);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 7, "C", "CGG", ClassificationType.BENIGN, radboudVariantTwoSameNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionThreeSameNucleotides() {
        RadboudVariant radboudVariantThreeSameNucleotides = new RadboudVariant();
        radboudVariantThreeSameNucleotides.setChromosome("chr1");
        radboudVariantThreeSameNucleotides.setStart(10);
        radboudVariantThreeSameNucleotides.setStop(10);
        radboudVariantThreeSameNucleotides.setREF("");
        radboudVariantThreeSameNucleotides.setALT("GGG");
        radboudVariantThreeSameNucleotides.setClassification("class 1");
        radboudVariantThreeSameNucleotides.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantThreeSameNucleotides);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 7, "C", "CGGG", ClassificationType.BENIGN, radboudVariantThreeSameNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionALotOfSameNucleotides() {
        RadboudVariant radboudVariantLotOfSameNucleotides = new RadboudVariant();
        radboudVariantLotOfSameNucleotides.setChromosome("chr1");
        radboudVariantLotOfSameNucleotides.setStart(10);
        radboudVariantLotOfSameNucleotides.setStop(10);
        radboudVariantLotOfSameNucleotides.setREF("");
        radboudVariantLotOfSameNucleotides.setALT("GGGGGGGGGGG");
        radboudVariantLotOfSameNucleotides.setClassification("class 1");
        radboudVariantLotOfSameNucleotides.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantLotOfSameNucleotides);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 7, "C", "CGGGGGGGGGGG", ClassificationType.BENIGN, radboudVariantLotOfSameNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionTwoDifferentNucleotides() {
        RadboudVariant radboudVariantTwoDifferentNucleotides = new RadboudVariant();
        radboudVariantTwoDifferentNucleotides.setChromosome("chr1");
        radboudVariantTwoDifferentNucleotides.setStart(3);
        radboudVariantTwoDifferentNucleotides.setStop(3);
        radboudVariantTwoDifferentNucleotides.setREF("");
        radboudVariantTwoDifferentNucleotides.setALT("TACGTG");
        radboudVariantTwoDifferentNucleotides.setClassification("class 1");
        radboudVariantTwoDifferentNucleotides.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantTwoDifferentNucleotides);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 2, "G", "GGTACGT", ClassificationType.BENIGN, radboudVariantTwoDifferentNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionDifferentNucleotides() {
        RadboudVariant radboudVariantDifferentNucleotides = new RadboudVariant();
        radboudVariantDifferentNucleotides.setChromosome("chr2");
        radboudVariantDifferentNucleotides.setStart(8);
        radboudVariantDifferentNucleotides.setStop(8);
        radboudVariantDifferentNucleotides.setREF("");
        radboudVariantDifferentNucleotides.setALT("CAAA");
        radboudVariantDifferentNucleotides.setClassification("class 1");
        radboudVariantDifferentNucleotides.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantDifferentNucleotides);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("2", 5, "A", "AAAAC", ClassificationType.BENIGN, radboudVariantDifferentNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }
}
