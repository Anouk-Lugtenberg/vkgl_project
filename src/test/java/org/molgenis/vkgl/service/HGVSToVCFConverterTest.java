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
}
