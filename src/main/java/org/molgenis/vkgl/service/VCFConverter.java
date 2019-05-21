package org.molgenis.vkgl.service;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.model.VCFVariant;
import org.molgenis.vkgl.model.Variant;

import java.io.File;
import java.io.FileNotFoundException;

public interface VCFConverter {
    Logger LOGGER = LogManager.getLogger(VCFConverter.class.getName());
    VCFVariant convertToVCF();
    VCFVariant convertSNP();
    VCFVariant convertInsertion();
    VCFVariant convertDeletion();
    VCFVariant convertDuplication();
    VCFVariant convertDeletionInsertion();
    VCFVariant convertNotClassified();

    /**
     *
     * @param chromosome
     * @param start
     * @param stop
     * @return
     */
    static String getBasesFromPosition(String chromosome, int start, int stop) {
        File indexedFasta = new File("/Users/anouk/Documents/afstudeerstage/chromosome/chromosome_grch_37/chr1.fasta");
        String subsequence = null;
        if (stop < start) {
            throw new IllegalArgumentException(stop + " is of lower value than " + start + ", cannot retrieve subsequence");
        }
        try {
            ReferenceSequenceFile faidx = new IndexedFastaSequenceFile(indexedFasta);
            subsequence = faidx.getSubsequenceAt(chromosome, start, stop).getBaseString().toUpperCase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return subsequence;
    }

    /**
     * Validates SNP. Checks if GRCh38 reference is same as reference given in file AND if ref doesn't equal ALT.
     * @param referenceGenomeBuild the base found in the reference genome.
     * @return boolean whether SNP is valid.
     */
    static boolean validateSNP(String referenceGenomeBuild, String REF, String ALT, Variant variant) {
        if (!referenceGenomeBuild.equals(REF)) {
            LOGGER.info(variant.getLineNumber() + ": " + variant.getRawInformation());
            LOGGER.info("Reference genome: " + referenceGenomeBuild + " does not equal reference given for variant: " + REF + "\n");
        } else if (REF.equals(ALT)) {
            LOGGER.info(variant.getLineNumber() + ": " + variant.getRawInformation());
            LOGGER.info("REF and ALT are the same, is not a valid variant\n");
        }
        return referenceGenomeBuild.equals(REF) && !REF.equals(ALT);
    }
}
