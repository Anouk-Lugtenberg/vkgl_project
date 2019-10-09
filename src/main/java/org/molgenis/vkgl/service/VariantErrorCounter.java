package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariantErrorCounter {
    private static Logger LOGGER = LogManager.getLogger(VariantErrorCounter.class.getName());
    private static int fileParsingErrors = 0;
    private static int referenceSNPDoesNotEqualReference = 0;
    private static int referenceAndAlternativeSameForSNP = 0;
    private static int startAndStopPositionNotTheSameForSNP = 0;
    private static int stopPositionOfSmallerValueThanStartPosition = 0;
    private static int noAlternativeAvailableForDeletionInsertion = 0;
    private static int referenceDeletionInsertionIsNotEqual = 0;
    private static int deletionInsertionIsASNP = 0;
    private static int deletionInsertionIsAnInsertion = 0;
    private static int deletionInsertionIsADeletion = 0;
    private static int referenceDeletionIsNotEqual = 0;
    private static int variantCouldNotBeClassified = 0;
    private static int bioCommonsVariantNotTheSameAsVCFVariant = 0;
    private static int bioCommonsUnknownErrors = 0;
    private static int bioCommonsErrorLengthCoordinatesDoesNotEqualSequenceLength = 0;
    private static int bioCommonsErrorReferenceDoesNotAgreeWithReferenceSequence = 0;
    private static int bioCommonsSplignError = 0;
    private static int bioCommonsUnexpectedError = 0;
    private static int bioCommonsStartPositionGreaterThanEndPosition = 0;
    private static int bioCommonsCoordinatesOutsideBoundsOfReferenceSequence = 0;
    private static int bioCommonsSyntaxErrors = 0;
    private static int duplicatesWhichDoNotHaveTheSameClassification = 0;
    private static int removedDuplicatesFromVCFVariantList = 0;
    private static int hgvsNotationSyntaxError = 0;

    public static void fileParsingErrorsPlusOne() {
        fileParsingErrors++;
    }

    public static void errorReferenceSNPDoesNotEqualReferencePlusOne() {
        referenceSNPDoesNotEqualReference++;
    }

    public static void errorReferenceAndAlternativeSameForSNP() {
        referenceAndAlternativeSameForSNP++;
    }

    public static void startAndStopPositionNotTheSameForSNP() {
        startAndStopPositionNotTheSameForSNP++;
    }

    public static void stopPositionOfSmallerValueThanStartPosition() {
        stopPositionOfSmallerValueThanStartPosition++;
    }

    public static void noAlternativeAvailableForDeletionInsertion() {
        noAlternativeAvailableForDeletionInsertion++;
    }

    public static void referenceDeletionInsertionIsNotEqual() {
        referenceDeletionInsertionIsNotEqual++;
    }

    public static void deletionInsertionIsASNP() {
        deletionInsertionIsASNP++;
    }

    public static void deletionInsertionIsADeletion() {
        deletionInsertionIsADeletion++;
    }

    public static void deletionInsertionIsAnInsertion() {
        deletionInsertionIsAnInsertion++;
    }

    public static void referenceDeletionIsNotEqual() {
        referenceDeletionIsNotEqual++;
    }

    public static void variantCouldNotBeClassified() {
        variantCouldNotBeClassified++;
    }

    public static void bioCommonsVariantNotTheSameAsVCFVariant() {
        bioCommonsVariantNotTheSameAsVCFVariant++;
    }

    public static void hgvsNotationSyntaxError() { hgvsNotationSyntaxError++; }

    public static void addBioCommonsError(String bioCommonsError) {
        Pattern patternErrorSplign = Pattern.compile("splign");
        Matcher matcherErrorSplign = patternErrorSplign.matcher(bioCommonsError);
        if (matcherErrorSplign.find()) {
            bioCommonsSplignError++;
            return;
        }

        Pattern patternErrorLengthCoordinatesDoesNotEqualSequenceLength = Pattern.compile("Length implied by coordinates must equal sequence deletion length");
        Matcher matcherErrorLengthCoordinatesDoesNotEqualSequenceLength = patternErrorLengthCoordinatesDoesNotEqualSequenceLength.matcher(bioCommonsError);
        if (matcherErrorLengthCoordinatesDoesNotEqualSequenceLength.find()) {
            bioCommonsErrorLengthCoordinatesDoesNotEqualSequenceLength++;
            return;
        }

        Pattern patternErrorReferenceDoesNotAgreeWithReferenceSequence = Pattern.compile("Variant reference \\([ACGT]+\\) does not agree with reference sequence \\([ACGT]+\\)");
        Matcher matcherErrorReferenceDoesNotAgreeWithReferenceSequence = patternErrorReferenceDoesNotAgreeWithReferenceSequence.matcher(bioCommonsError);
        if (matcherErrorReferenceDoesNotAgreeWithReferenceSequence.find()) {
            bioCommonsErrorReferenceDoesNotAgreeWithReferenceSequence++;
            return;
        }

        Pattern patternBaseStartGreaterThanEndPosition = Pattern.compile("base start position must be <= end position");
        Matcher matcherBaseStartGreaterThanEndPosition = patternBaseStartGreaterThanEndPosition.matcher(bioCommonsError);
        if (matcherBaseStartGreaterThanEndPosition.find()) {
            bioCommonsStartPositionGreaterThanEndPosition++;
            return;
        }

        Pattern patternCoordinateOutsideBoundsReferenceSequence = Pattern.compile("The given coordinate is outside the bounds of the reference sequence");
        Matcher matcherCoordinateOutsideBoundsReferenceSequence = patternCoordinateOutsideBoundsReferenceSequence.matcher(bioCommonsError);
        if (matcherCoordinateOutsideBoundsReferenceSequence.find()) {
            bioCommonsCoordinatesOutsideBoundsOfReferenceSequence++;
            return;
        }

        Pattern patternSyntaxErrors = Pattern.compile("expected EOF|expected a letter or digit|expected one of|expected a letter");
        Matcher matcherSyntaxErrors = patternSyntaxErrors.matcher(bioCommonsError);
        if (matcherSyntaxErrors.find()) {
            bioCommonsSyntaxErrors++;
            return;
        }

        Pattern patternUnexpectedError = Pattern.compile("Unexpected error");
        Matcher matcherUnexpectedError = patternUnexpectedError.matcher(bioCommonsError);
        if (matcherUnexpectedError.find()) {
            bioCommonsUnexpectedError++;
            return;
        }
        System.out.println("\nBIOCOMMONS UNKNOWN ERROR: " + bioCommonsError);
        bioCommonsUnknownErrors++;
    }

    public static void duplicatesWhichDoNotHaveTheSameClassification() {
        duplicatesWhichDoNotHaveTheSameClassification += 2;
    }

    public static void removedDuplicatesFromVCFVariantList() {
        removedDuplicatesFromVCFVariantList++;
    }

    public static void writeErrors() {
        HashMap<String, Integer> criticalErrors = new HashMap<>();
        criticalErrors.put("File parsing errors", fileParsingErrors);
        criticalErrors.put("REF of SNP does not equal genome reference", referenceSNPDoesNotEqualReference);
        criticalErrors.put("REF and ALT are same for SNP", referenceAndAlternativeSameForSNP);
        criticalErrors.put("Start and stop position not the same for SNP", startAndStopPositionNotTheSameForSNP);
        criticalErrors.put("Stop position < start position", stopPositionOfSmallerValueThanStartPosition);
        criticalErrors.put("No ALT available for delins", noAlternativeAvailableForDeletionInsertion);
        criticalErrors.put("REF delins does not equal genome reference", referenceDeletionInsertionIsNotEqual);
        criticalErrors.put("REF deletion does not equal genome reference", referenceDeletionIsNotEqual);
        criticalErrors.put("Variant could not be classified", variantCouldNotBeClassified);
        criticalErrors.put("BioCommons: variant is not the same as created VCF variant", bioCommonsVariantNotTheSameAsVCFVariant);
        criticalErrors.put("BioCommons: unknown errors", bioCommonsUnknownErrors);
        criticalErrors.put("BioCommons: length of coordinates does not equal the sequence length", bioCommonsErrorLengthCoordinatesDoesNotEqualSequenceLength);
        criticalErrors.put("BioCommons: REF does not equal genome reference", bioCommonsErrorReferenceDoesNotAgreeWithReferenceSequence);
        criticalErrors.put("BioCommons: splign error", bioCommonsSplignError);
        criticalErrors.put("BioCommons: unexpected error", bioCommonsUnexpectedError);
        criticalErrors.put("BioCommons: start > end position", bioCommonsStartPositionGreaterThanEndPosition);
        criticalErrors.put("BioCommons: coordinates outside bounds of reference sequence", bioCommonsCoordinatesOutsideBoundsOfReferenceSequence);
        criticalErrors.put("BioCommons: syntax errors", bioCommonsSyntaxErrors);
        criticalErrors.put("HGVS syntax error", hgvsNotationSyntaxError);
        criticalErrors.put("Duplicates do not have the same classificiation", duplicatesWhichDoNotHaveTheSameClassification);
        criticalErrors.put("Removed duplicates from VCF Variant last", removedDuplicatesFromVCFVariantList);

        HashMap<String, Integer> delInsErrors = new HashMap<>();
        delInsErrors.put("Delins is a SNP", deletionInsertionIsASNP);
        delInsErrors.put("Delins is an insertion", deletionInsertionIsAnInsertion);
        delInsErrors.put("Delins is a deletion", deletionInsertionIsADeletion);

        LOGGER.info("############################# delins Errors #############################");
        writeErrorsFromMap(delInsErrors);
        LOGGER.info("############################# Critical Errors #############################");
        writeErrorsFromMap(criticalErrors);
    }

    public static void writeErrorsFromMap(HashMap<String, Integer> mapWithErrors) {
        int totalErrors = 0;
        for (Map.Entry<String, Integer> errors : mapWithErrors.entrySet()) {
            String error = errors.getKey();
            int countOfErrors = errors.getValue();
            if (countOfErrors > 0) {
                totalErrors += countOfErrors;
                LOGGER.info("{}: {}", error, countOfErrors);
            }
        }
        LOGGER.info("Total errors: " + totalErrors);
    }
}
