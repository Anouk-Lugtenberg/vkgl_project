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

class RadboudToVCFConverterTest {

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

    @Test
    void convertInsertionDifferentNucleotidesTwo() {
        RadboudVariant radboudVariantDifferentNucleotides = new RadboudVariant();
        radboudVariantDifferentNucleotides.setChromosome("chr3");
        radboudVariantDifferentNucleotides.setStart(7);
        radboudVariantDifferentNucleotides.setStop(7);
        radboudVariantDifferentNucleotides.setREF("");
        radboudVariantDifferentNucleotides.setALT("AAGAA");
        radboudVariantDifferentNucleotides.setClassification("class 1");
        radboudVariantDifferentNucleotides.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantDifferentNucleotides);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("3", 3, "T", "TAGAAA", ClassificationType.BENIGN, radboudVariantDifferentNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionWhichIsDuplication() {
        RadboudVariant radboudVariantDuplication = new RadboudVariant();
        radboudVariantDuplication.setChromosome("4");
        radboudVariantDuplication.setStart(8);
        radboudVariantDuplication.setStop(8);
        radboudVariantDuplication.setREF("");
        radboudVariantDuplication.setALT("AAGAA");
        radboudVariantDuplication.setClassification("class 1");
        radboudVariantDuplication.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantDuplication);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("4", 3,"T", "TAAGAA", ClassificationType.BENIGN, radboudVariantDuplication);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertLongInsertion() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("5");
        radboudVariant.setStart(13);
        radboudVariant.setStop(13);
        radboudVariant.setREF("");
        radboudVariant.setALT("ACAAGTG");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("ins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("5", 3, "T", "TGTGACAA", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertOneNucleotideDeletion() {
        RadboudVariant radboudVariantSimpleDeletion = new RadboudVariant();
        radboudVariantSimpleDeletion.setChromosome("6");
        radboudVariantSimpleDeletion.setStart(5);
        radboudVariantSimpleDeletion.setStop(5);
        radboudVariantSimpleDeletion.setREF("");
        radboudVariantSimpleDeletion.setALT("");
        radboudVariantSimpleDeletion.setClassification("class 1");
        radboudVariantSimpleDeletion.setVariantType("del");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantSimpleDeletion);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("6", 2, "GC", "G", ClassificationType.BENIGN, radboudVariantSimpleDeletion);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertTwoSameNucleotidesDeletion() {
        RadboudVariant radboudVariantTwoSameNucleotidesDeletion = new RadboudVariant();
        radboudVariantTwoSameNucleotidesDeletion.setChromosome("6");
        radboudVariantTwoSameNucleotidesDeletion.setStart(4);
        radboudVariantTwoSameNucleotidesDeletion.setStop(5);
        radboudVariantTwoSameNucleotidesDeletion.setREF("");
        radboudVariantTwoSameNucleotidesDeletion.setALT("");
        radboudVariantTwoSameNucleotidesDeletion.setClassification("class 1");
        radboudVariantTwoSameNucleotidesDeletion.setVariantType("del");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantTwoSameNucleotidesDeletion);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("6", 2, "GCC", "G", ClassificationType.BENIGN, radboudVariantTwoSameNucleotidesDeletion);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDifferentNucleotidesDeletion() {
        RadboudVariant radboudVariantDifferentNucleotidesDeletion = new RadboudVariant();
        radboudVariantDifferentNucleotidesDeletion.setChromosome("7");
        radboudVariantDifferentNucleotidesDeletion.setStart(6);
        radboudVariantDifferentNucleotidesDeletion.setStop(8);
        radboudVariantDifferentNucleotidesDeletion.setREF("");
        radboudVariantDifferentNucleotidesDeletion.setALT("");
        radboudVariantDifferentNucleotidesDeletion.setClassification("class 1");
        radboudVariantDifferentNucleotidesDeletion.setVariantType("del");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantDifferentNucleotidesDeletion);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("7", 3, "CAGA", "C", ClassificationType.BENIGN, radboudVariantDifferentNucleotidesDeletion);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertSingleDuplications() {
        RadboudVariant radboudVariantSingleDuplication = new RadboudVariant();
        radboudVariantSingleDuplication.setChromosome("1");
        radboudVariantSingleDuplication.setStart(4);
        radboudVariantSingleDuplication.setStop(4);
        radboudVariantSingleDuplication.setREF("");
        radboudVariantSingleDuplication.setALT("");
        radboudVariantSingleDuplication.setClassification("class 1");
        radboudVariantSingleDuplication.setVariantType("dup");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariantSingleDuplication);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 3, "G", "GT", ClassificationType.BENIGN, radboudVariantSingleDuplication);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDuplicationThatDoesNotMove() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("8");
        radboudVariant.setStart(6);
        radboudVariant.setStop(6);
        radboudVariant.setREF("");
        radboudVariant.setALT("TGGC");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("dup");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("8", 6, "G", "GTGGC", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionWithZeroAsRef() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("1");
        radboudVariant.setStart(4);
        radboudVariant.setStop(4);
        radboudVariant.setREF("0");
        radboudVariant.setALT("A");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("insertion");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 4, "T", "TA", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDeletionWithZeroAsAlt() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("1");
        radboudVariant.setStart(4);
        radboudVariant.setStop(5);
        radboudVariant.setREF("TC");
        radboudVariant.setALT("0");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("deletion");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 3, "GTC", "G", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDeletionWithNucleotidesInAlt() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("9");
        radboudVariant.setStart(4);
        radboudVariant.setStop(10);
        radboudVariant.setREF("AAAAAAA");
        radboudVariant.setALT("AAAA");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("deletion");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("9", 1, "GAAA", "G", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDeletionInsertionWhichIsDeletion() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("10");
        radboudVariant.setStart(2);
        radboudVariant.setStop(14);
        radboudVariant.setREF("CACACACACACAC");
        radboudVariant.setALT("ACACACACACAC");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("delins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("10", 1, "GC", "G", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDeletionInsertionWhichIsInsertion() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("10");
        radboudVariant.setStart(2);
        radboudVariant.setStop(4);
        radboudVariant.setREF("CAC");
        radboudVariant.setALT("CACC");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("delins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("10", 1, "G", "GC", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDeletionInsertionWhichIsInsertionTwo() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("11");
        radboudVariant.setStart(2);
        radboudVariant.setStop(6);
        radboudVariant.setREF("CTGTG");
        radboudVariant.setALT("CTGTGTG");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("delins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("11", 2, "C", "CTG", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDeletionInsertionWhichIsSNP() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("11");
        radboudVariant.setStart(4);
        radboudVariant.setStop(5);
        radboudVariant.setREF("GT");
        radboudVariant.setALT("TT");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("delins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("11", 4, "G", "T", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDeletionInsertion() {
        RadboudVariant radboudVariant = new RadboudVariant();
        radboudVariant.setChromosome("11");
        radboudVariant.setStart(3);
        radboudVariant.setStop(4);
        radboudVariant.setREF("TG");
        radboudVariant.setALT("AA");
        radboudVariant.setClassification("class 1");
        radboudVariant.setVariantType("delins");

        RadboudToVCFConverter radboudToVCFConverter = new RadboudToVCFConverter(radboudVariant);
        VCFVariant actualVCFVariant = radboudToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("11", 3, "TG", "AA", ClassificationType.BENIGN, radboudVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }
}

