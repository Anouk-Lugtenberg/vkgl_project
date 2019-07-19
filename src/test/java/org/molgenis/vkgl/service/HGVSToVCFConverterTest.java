package org.molgenis.vkgl.service;

import com.shazam.shazamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.model.ClassificationType;
import org.molgenis.vkgl.model.HGVSVariant;
import org.molgenis.vkgl.model.VCFVariant;

import java.io.File;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

class HGVSToVCFConverterTest {

    @BeforeEach
    void setUpFasta() {
        CLIParser.fastaFileDirectory = new File(getClass().getResource("/fasta/fake_fasta.fasta").getFile());
    }

    @Test
    void convertSimpleSNP() {
        HGVSVariant hgvsVariantSimpleSNP = new HGVSVariant();
        hgvsVariantSimpleSNP.setChromosome("1");
        hgvsVariantSimpleSNP.setGenomicDNA("NC_000001.10:g.1A>G");
        hgvsVariantSimpleSNP.setClassification("?");
        hgvsVariantSimpleSNP.setVariantType(hgvsVariantSimpleSNP.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantSimpleSNP);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 1, "A", "G", ClassificationType.VOUS, hgvsVariantSimpleSNP);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));

    }

    @Test
    void convertSimpleInsertion() {
        HGVSVariant hgvsVariantSimpleInsertion = new HGVSVariant();
        hgvsVariantSimpleInsertion.setChromosome("1");
        hgvsVariantSimpleInsertion.setGenomicDNA("NC_000001.10:g.4_5insGCA");
        hgvsVariantSimpleInsertion.setClassification("?");
        hgvsVariantSimpleInsertion.setVariantType(hgvsVariantSimpleInsertion.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantSimpleInsertion);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 4, "T", "TGCA", ClassificationType.VOUS, hgvsVariantSimpleInsertion);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionOneNucleotide() {
        HGVSVariant hgvsVariantInsertionOneNucleotide = new HGVSVariant();
        hgvsVariantInsertionOneNucleotide.setChromosome("1");
        hgvsVariantInsertionOneNucleotide.setGenomicDNA("NC_000001.10:g.4_5insC");
        hgvsVariantInsertionOneNucleotide.setClassification("?");
        hgvsVariantInsertionOneNucleotide.setVariantType(hgvsVariantInsertionOneNucleotide.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantInsertionOneNucleotide);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 4,"T", "TC", ClassificationType.VOUS, hgvsVariantInsertionOneNucleotide);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionPositionShuffledToTheLeft() {
        HGVSVariant hgvsVariantInsertionShuffledToTheLeft = new HGVSVariant();
        hgvsVariantInsertionShuffledToTheLeft.setChromosome("1");
        hgvsVariantInsertionShuffledToTheLeft.setGenomicDNA("NC_000001.10:g.10_11insG");
        hgvsVariantInsertionShuffledToTheLeft.setClassification("?");
        hgvsVariantInsertionShuffledToTheLeft.setVariantType(hgvsVariantInsertionShuffledToTheLeft.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantInsertionShuffledToTheLeft);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 7, "C", "CG", ClassificationType.VOUS, hgvsVariantInsertionShuffledToTheLeft);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionTwoSameNucleotides() {
        HGVSVariant hgvsVariantTwoSameNucleotides = new HGVSVariant();
        hgvsVariantTwoSameNucleotides.setChromosome("1");
        hgvsVariantTwoSameNucleotides.setGenomicDNA("NC_00001.10:g.10_11insGG");
        hgvsVariantTwoSameNucleotides.setClassification("?");
        hgvsVariantTwoSameNucleotides.setVariantType(hgvsVariantTwoSameNucleotides.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantTwoSameNucleotides);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 7, "C", "CGG", ClassificationType.VOUS, hgvsVariantTwoSameNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionThreeSameNucleotides() {
        HGVSVariant hgvsVariantThreeSameNucleotides = new HGVSVariant();
        hgvsVariantThreeSameNucleotides.setChromosome("1");
        hgvsVariantThreeSameNucleotides.setGenomicDNA("NC_00001.10:g.10_11insGGG");
        hgvsVariantThreeSameNucleotides.setClassification("?");
        hgvsVariantThreeSameNucleotides.setVariantType(hgvsVariantThreeSameNucleotides.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantThreeSameNucleotides);
        VCFVariant actualVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 7, "C", "CGGG", ClassificationType.VOUS, hgvsVariantThreeSameNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionALotOfSameNucleotides() {
        HGVSVariant hgvsVariantLotOfSameNucleotides = new HGVSVariant();
        hgvsVariantLotOfSameNucleotides.setChromosome("1");
        hgvsVariantLotOfSameNucleotides.setGenomicDNA("NC_00001.10:g.10_11insGGGGGGGGGGG");
        hgvsVariantLotOfSameNucleotides.setClassification("?");
        hgvsVariantLotOfSameNucleotides.setVariantType(hgvsVariantLotOfSameNucleotides.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantLotOfSameNucleotides);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 7, "C", "CGGGGGGGGGGG", ClassificationType.VOUS, hgvsVariantLotOfSameNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionTwoDifferentNucleotides() {
        HGVSVariant hgvsVariantTwoDifferentNucleotides = new HGVSVariant();
        hgvsVariantTwoDifferentNucleotides.setChromosome("1");
        hgvsVariantTwoDifferentNucleotides.setGenomicDNA("NC_000001.10:g.3_4insTACGTG");
        hgvsVariantTwoDifferentNucleotides.setClassification("?");
        hgvsVariantTwoDifferentNucleotides.setVariantType(hgvsVariantTwoDifferentNucleotides.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantTwoDifferentNucleotides);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 2, "G", "GGTACGT", ClassificationType.VOUS, hgvsVariantTwoDifferentNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionDifferentNucleotides() {
        HGVSVariant hgvsVariantDifferentNucleotides = new HGVSVariant();
        hgvsVariantDifferentNucleotides.setChromosome("2");
        hgvsVariantDifferentNucleotides.setGenomicDNA("NC_000001.10:g.8_9insCAAA");
        hgvsVariantDifferentNucleotides.setClassification("?");
        hgvsVariantDifferentNucleotides.setVariantType(hgvsVariantDifferentNucleotides.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantDifferentNucleotides);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("2", 5, "A", "AAAAC", ClassificationType.VOUS, hgvsVariantDifferentNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertInsertionDifferentNucleotidesTwo() {
        HGVSVariant hgvsVariantDifferentNucleotides = new HGVSVariant();
        hgvsVariantDifferentNucleotides.setChromosome("3");
        hgvsVariantDifferentNucleotides.setGenomicDNA("NC_00001.10:g.7_8insAAGAA");
        hgvsVariantDifferentNucleotides.setClassification("?");
        hgvsVariantDifferentNucleotides.setVariantType(hgvsVariantDifferentNucleotides.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantDifferentNucleotides);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("3", 3, "T", "TAGAAA", ClassificationType.VOUS, hgvsVariantDifferentNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertOneNucleotideDeletion() {
        HGVSVariant hgvsVariantOneNucleotideDeletion = new HGVSVariant();
        hgvsVariantOneNucleotideDeletion.setChromosome("6");
        hgvsVariantOneNucleotideDeletion.setGenomicDNA("NC_000001.10:g.5del");
        hgvsVariantOneNucleotideDeletion.setClassification("?");
        hgvsVariantOneNucleotideDeletion.setVariantType(hgvsVariantOneNucleotideDeletion.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantOneNucleotideDeletion);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("6", 2, "GC", "G", ClassificationType.VOUS, hgvsVariantOneNucleotideDeletion);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertTwoSameNucleotidesDeletion() {
        HGVSVariant hgvsVariantTwoSameNucleotidesDeletion = new HGVSVariant();
        hgvsVariantTwoSameNucleotidesDeletion.setChromosome("6");
        hgvsVariantTwoSameNucleotidesDeletion.setGenomicDNA("NC_000001.10:g.4_5del");
        hgvsVariantTwoSameNucleotidesDeletion.setClassification("?");
        hgvsVariantTwoSameNucleotidesDeletion.setVariantType(hgvsVariantTwoSameNucleotidesDeletion.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantTwoSameNucleotidesDeletion);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("6", 2, "GCC", "G", ClassificationType.VOUS, hgvsVariantTwoSameNucleotidesDeletion);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDifferentNucleotidesDeletion() {
        HGVSVariant hgvsVariantDifferentNucleotidesDeletion = new HGVSVariant();
        hgvsVariantDifferentNucleotidesDeletion.setChromosome("7");
        hgvsVariantDifferentNucleotidesDeletion.setGenomicDNA("NC_000001.10:g.6_8del");
        hgvsVariantDifferentNucleotidesDeletion.setClassification("?");
        hgvsVariantDifferentNucleotidesDeletion.setVariantType(hgvsVariantDifferentNucleotidesDeletion.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantDifferentNucleotidesDeletion);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("7", 3, "CAGA", "C", ClassificationType.VOUS, hgvsVariantDifferentNucleotidesDeletion);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertSingleDuplication() {
        HGVSVariant hgvsVariantSingleDuplication = new HGVSVariant();
        hgvsVariantSingleDuplication.setChromosome("1");
        hgvsVariantSingleDuplication.setGenomicDNA("NC_000001.10:g.4dup");
        hgvsVariantSingleDuplication.setClassification("?");
        hgvsVariantSingleDuplication.setVariantType(hgvsVariantSingleDuplication.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantSingleDuplication);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("1", 3, "G", "GT", ClassificationType.VOUS, hgvsVariantSingleDuplication);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDuplicationMultipleNucleotides() {
        HGVSVariant hgvsVariantMultipleNucleotides = new HGVSVariant();
        hgvsVariantMultipleNucleotides.setChromosome("6");
        hgvsVariantMultipleNucleotides.setGenomicDNA("NC_000001.10:g.14_17dup");
        hgvsVariantMultipleNucleotides.setClassification("?");
        hgvsVariantMultipleNucleotides.setVariantType(hgvsVariantMultipleNucleotides.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariantMultipleNucleotides);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("6", 10, "T", "TGCTA", ClassificationType.VOUS, hgvsVariantMultipleNucleotides);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }

    @Test
    void convertDeletionInsertion() {
        HGVSVariant hgvsVariant = new HGVSVariant();
        hgvsVariant.setChromosome("11");
        hgvsVariant.setGenomicDNA("NC_000001.11:g.3_4delinsAA");
        hgvsVariant.setClassification("?");
        hgvsVariant.setVariantType(hgvsVariant.getGenomicDNA());

        HGVSToVCFConverter hgvsToVCFConverter = new HGVSToVCFConverter(hgvsVariant);
        VCFVariant actualVCFVariant = hgvsToVCFConverter.convertToVCF();

        VCFVariant expectedVCFVariant = new VCFVariant("11", 3, "TG", "AA", ClassificationType.VOUS, hgvsVariant);
        expectedVCFVariant.setValidVariant(true);

        MatcherAssert.assertThat(actualVCFVariant, sameBeanAs(expectedVCFVariant));
    }
}
