package org.molgenis.vkgl.service;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.model.PositionAndNucleotides;
import org.molgenis.vkgl.model.VCFVariant;
import org.molgenis.vkgl.model.Variant;

import java.io.*;

public abstract class VCFConverter {
    Logger LOGGER = LogManager.getLogger(VCFConverter.class.getName());
    abstract VCFVariant convertToVCF();
    abstract VCFVariant convertSNP();
    abstract VCFVariant convertInsertion();
    abstract VCFVariant convertDeletion();
    abstract VCFVariant convertDuplication();
    abstract VCFVariant convertDeletionInsertion();
    abstract VCFVariant convertNotClassified();

    /**
     * Retrieves the bases from a position given chromosome, start and stop with the use of samtools.
     * https://github.com/samtools/htsjdk
     *
     * Uses the fasta file given by the user via command line.
     * @param chromosome a String representation of the chromosome
     * @param start start position
     * @param stop stop position
     * @return String containing the found bases in the sequence
     */
    String getBasesFromPosition(String chromosome, int start, int stop) {
        String subsequence = null;
        try {
            ReferenceSequenceFile faidx = new IndexedFastaSequenceFile(CLIParser.fastaFileDirectory);
            subsequence = faidx.getSubsequenceAt(chromosome, start, stop).getBaseString().toUpperCase();
        } catch (FileNotFoundException e) {
            LOGGER.error("Please provide a fasta file");
        } catch (SAMException e) {
            LOGGER.error("SAM exception");
            e.printStackTrace();
        }
        return subsequence;
    }

    /**
     * Validates SNP. Checks if GRCh37 reference is same as reference given in file AND if ref doesn't equal ALT.
     * @param referenceGenomeBuild the base found in the reference genome.
     * @return boolean whether SNP is valid.
     */
    boolean validateSNP(String referenceGenomeBuild, String REF, String ALT, Variant variant) {
        if (!referenceGenomeBuild.equals(REF)) {
            LOGGER.info(variant.getLineNumber() + ": " + variant.getRawInformation());
            LOGGER.info("Reference genome: " + referenceGenomeBuild + " does not equal reference given for variant: " + REF + ". Flagging as invalid.\n");
            VariantErrorCounter.errorReferenceSNPDoesNotEqualReferencePlusOne();
        } else if (REF.equals(ALT)) {
            LOGGER.info(variant.getLineNumber() + ": " + variant.getRawInformation());
            LOGGER.info("REF and ALT are the same, is not a valid SNP.\n");
            VariantErrorCounter.errorReferenceAndAlternativeSameForSNP();
        }
        return referenceGenomeBuild.equals(REF) && !REF.equals(ALT);
    }

    /**
     * Moves the position of a deletion to the most left (5') position of the genome.
     * @param chromosome the chromosome the deletion lies on
     * @param start start position of the chromosome
     * @param stop stop position of the chromosome
     * @return PositionAndNucleotides: the position of the deletion and the nucleotides which are removed
     */
     PositionAndNucleotides moveDeletionMostLeftPosition(String chromosome, int start, int stop) {
        PositionAndNucleotides positionAndNucleotides;
        String removedNucleotides = getBasesFromPosition(chromosome, start, stop);
        //if start is same as stop, only one nucleotide is removed
        if (start == stop) {
            int position = moveNucleotidesMostLeftPosition(removedNucleotides, start, chromosome);
            positionAndNucleotides = new PositionAndNucleotides(position, removedNucleotides);
        } else {
            //New variable is made, so this variable can be used in the lambda.
            String nucleotidesRemoved = getBasesFromPosition(chromosome, start, stop);

            //if all nucleotides are the same, it's only needed to check if the nucleotide on the left is the same
            //as one of the nucleotides
            if (nucleotidesRemoved.chars().allMatch(c -> c == nucleotidesRemoved.charAt(0))) {
                int position = moveNucleotidesMostLeftPosition(nucleotidesRemoved.substring(0,1), start, chromosome);
                positionAndNucleotides = new PositionAndNucleotides(position, removedNucleotides);
            } else {
                //Start - 1, nucleotide from 3' side is one place left to the deleted sequence
                positionAndNucleotides = moveDifferentNucleotidesMostLeftPosition(start - 1, removedNucleotides, chromosome);
            }
        }
        return positionAndNucleotides;
    }

    /**
     * Checks if nucleotides more to the left position are the same as the given nucleotide. If they are
     * moves the position to the left.
     * @param nucleotide the nucleotide to be checked
     * @param position the original position of the nucleotide
     * @param chromosome the chromosome from the nucleotide
     * @return the new position
     */
     int moveNucleotidesMostLeftPosition(String nucleotide, int position, String chromosome) {
        //As long as the nucleotide in the position more to the left is the same, the position should be shuffled to the left
        //e.g. AATTCC, insertion of T at position 5 AATT-T-CC should actually be insertion of T at position 3
        //AA-T-TTCC.
        //Only need one base for the anchor, which is the same as the ALT. That's why position is used instead of start/stop.
        while (getBasesFromPosition(chromosome, position, position).equals(nucleotide)) {
            position = position - 1;
        }
        return position;
    }

    /**
     * Checks if different nucleotides can be moved more to the left. If that's the case, the position is moved
     * to the left.
     * @param position the original start position of the nucleotides
     * @param nucleotides the nucleotides to be checked
     * @param chromosome the chromosome from the nucleotides
     * @return PositionAndNucleotides an object containing both the new position and nucleotides
     */
     PositionAndNucleotides moveDifferentNucleotidesMostLeftPosition(int position, String nucleotides, String chromosome) {
        StringBuilder newNucleotides = new StringBuilder(nucleotides);

        //Get last nucleotide from ALT
        String lastNucleotide = nucleotides.substring(nucleotides.length() - 1);

        //Get nucleotide which is on the 3' side of the sequence.
        String nucleotideThreeSideALT = getBasesFromPosition(chromosome, position, position);

        while (nucleotideThreeSideALT.equals(lastNucleotide)) {
            //If nucleotides are equal: insert nucleotide to the front of the ALT and remove from the end of the ALT.
            newNucleotides.insert(0, lastNucleotide).setLength(newNucleotides.length() - 1);

            //Re-do while loop for position one step more to the 3' side of the sequence.
            //Last nucleotide is now the last character from the new ALT.
            lastNucleotide = newNucleotides.substring(newNucleotides.length() - 1);
            position = position - 1;
            nucleotideThreeSideALT = getBasesFromPosition(chromosome, position, position);
        }
        nucleotides = newNucleotides.toString();
        return new PositionAndNucleotides(position, nucleotides);
    }
}
