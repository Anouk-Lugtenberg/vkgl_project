package org.molgenis.vkgl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.molgenis.vkgl.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class VariantToVCFConverter {
    private static Logger LOGGER = LogManager.getLogger(VariantToVCFConverter.class.getName());

    public void convertVariants(Map<String, ArrayList<Variant>> variants, Path outputDirectory) {
        for (Map.Entry<String, ArrayList<Variant>> entry : variants.entrySet()) {
            String nameUMC = entry.getKey();
            ArrayList<Variant> variantsPerUMC = entry.getValue();
            VariantFormatDeterminer variantFormatDeterminer = new VariantFormatDeterminer();
            VariantFormat variantFormat = variantFormatDeterminer.getVariantFormat(nameUMC);
            switch (variantFormat) {
                case RADBOUD:
                    convertRadboudVariantToVCF(nameUMC, variantsPerUMC);
                    break;
            }
        }
    }

    private void convertRadboudVariantToVCF(String nameUMC, ArrayList<Variant> variants) {
        for (Variant variant : variants) {
            RadboudVariant radboudVariant = (RadboudVariant)variant;
            switch (radboudVariant.getVariantType()) {
                case SNP:
                    int start = radboudVariant.getStart();
                    int stop = radboudVariant.getStop();
                    String ALT = radboudVariant.getALT();
                    String REF = radboudVariant.getREF();
                    if (start == stop && !ALT.equals(REF)) {
                        checkReferenceInGenomeSNP(radboudVariant.getREF(), radboudVariant.getStart());
                    } else {
                        LOGGER.error("It appears that radboud variant with start: " + start + " and stop: " + stop + "" +
                                "is not a valid SingleNucleotideVariant");
                    }
            }
        }
    }

    private void checkReferenceInGenomeSNP(String REF, int position) {

    }

    private boolean checkHGVSSyntax(String cDNANotation) {
        String URLString = "https://mutalyzer.nl/json/checkSyntax?variant=" + cDNANotation;
        try {
            URL urlObject = new URL(URLString);
            URLConnection urlConnection = urlObject.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject json = new JSONObject(line);
                if (!(boolean) json.get("valid")) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
