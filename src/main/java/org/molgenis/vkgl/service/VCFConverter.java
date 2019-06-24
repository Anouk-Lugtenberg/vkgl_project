package org.molgenis.vkgl.service;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.CLI.CLIParser;
import org.molgenis.vkgl.model.VCFVariant;
import org.molgenis.vkgl.model.Variant;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public interface VCFConverter {
    Logger LOGGER = LogManager.getLogger(VCFConverter.class.getName());
    VCFVariant convertToVCF();
    VCFVariant convertSNP();
    VCFVariant convertInsertion();
    VCFVariant convertDeletion();
    VCFVariant convertDuplication();
    VCFVariant convertDeletionInsertion();
    VCFVariant convertNotClassified();
    File fastaFile = CLIParser.fastaFileDirectory;

    /**
     *
     * @param chromosome
     * @param start
     * @param stop
     * @return
     */
    static String getBasesFromPosition(String chromosome, int start, int stop) {
        String subsequence = null;
        if (stop < start) {
            throw new IllegalArgumentException(stop + " is of lower value than " + start + ", cannot retrieve subsequence");
        }
        try {
            ReferenceSequenceFile faidx = new IndexedFastaSequenceFile(fastaFile);
            subsequence = faidx.getSubsequenceAt(chromosome, start, stop).getBaseString().toUpperCase();
        } catch (FileNotFoundException e) {
            LOGGER.error("Please provide a fasta file");
        }
        return subsequence;
    }

    /**
     * Validates SNP. Checks if GRCh37 reference is same as reference given in file AND if ref doesn't equal ALT.
     * @param referenceGenomeBuild the base found in the reference genome.
     * @return boolean whether SNP is valid.
     */
    static boolean validateSNP(String referenceGenomeBuild, String REF, String ALT, Variant variant) {
        if (!referenceGenomeBuild.equals(REF)) {
            LOGGER.info(variant.getLineNumber() + ": " + variant.getRawInformation());
            LOGGER.info("Reference genome: " + referenceGenomeBuild + " does not equal reference given for variant: " + REF + ". Flagging as invalid.\n");
        } else if (REF.equals(ALT)) {
            LOGGER.info(variant.getLineNumber() + ": " + variant.getRawInformation());
            LOGGER.info("REF and ALT are the same, is not a valid SNP.\n");
        }
        return referenceGenomeBuild.equals(REF) && !REF.equals(ALT);
    }

//    static void getMinorAlleleFrequency(Variant variant) {
//        String server = "https://rest.ensemble.org";
//        String ext = "/vep/human/hgvs/" + variant.getGeneName() + variant.getcDNANotation() + "?";
//        try {
//            URL url = new URL(server + ext);
//            URLConnection connection = url.openConnection();
//            HttpURLConnection httpConnection = (HttpURLConnection)connection;
//            httpConnection.setRequestProperty("Content-Type", "application/json");
//
//            InputStream response = connection.getInputStream();
//            int responseCode = httpConnection.getResponseCode();
//
//            if (responseCode != 200) {
//                throw new RuntimeException("Response code was not 200. Detected response was " + responseCode);
//            }
//
//            String output;
//            Reader reader = null;
//            try {
//                reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
//                StringBuilder builder = new StringBuilder();
//                char[] buffer = new char[8192];
//                int read;
//                while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
//                    builder.append(buffer, 0, read);
//                }
//                output = builder.toString();
//            } finally {
//                if (reader != null) try {
//                    reader.close();
//                } catch (IOException logOrIgnore) {
//                    logOrIgnore.printStackTrace();
//                }
//            }
//
//            System.out.println(output);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
}
