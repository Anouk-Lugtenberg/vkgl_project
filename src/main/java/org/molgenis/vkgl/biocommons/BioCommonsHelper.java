package org.molgenis.vkgl.biocommons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.molgenis.vkgl.model.VCFVariant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BioCommonsHelper {
    private HttpClient httpClient;
    private HttpPost httpPostLeftAnchorTrue;
    private HttpPost httpPostLeftAnchorFalse;

    public BioCommonsHelper() {
        this.httpClient = HttpClients.createDefault();
        this.httpPostLeftAnchorTrue = new HttpPost("http://localhost:1234/h2v?keep_left_anchor=True");
        this.httpPostLeftAnchorFalse = new HttpPost("http://localhost:1234/h2v?keep_left_anchor=False");
    }

    /**
     * Retrieves a list of bio commons variants based on a list of VCF variants.
     * @param vcfVariants the list with VCF variants
     * @param isSNP true if it's a list of snps, false if contains other variants
     * @return a list containing BioCommonsVCFVariants
     */
    public List<BioCommonsVCFVariant> getBioCommonsVariants(ArrayList<VCFVariant> vcfVariants, boolean isSNP) {
        ArrayList<String> dnaNotations = new ArrayList<>();
        List<BioCommonsVCFVariant> bioCommonsVCFVariants;

        //Create a list with only the dna notations
        for (VCFVariant vcfVariant : vcfVariants) {
            dnaNotations.add(vcfVariant.getDnaNotation());
        }

        //Create a JSON string
        String json = createJSONArray(dnaNotations);
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);

        //If variants are SNPS, no anchor should be retrieved
        if (isSNP) {
            httpPostLeftAnchorFalse.setEntity(requestEntity);
        } else {
            httpPostLeftAnchorTrue.setEntity(requestEntity);
        }

        try {
            HttpResponse rawResponse;
            if (isSNP) {
                rawResponse = httpClient.execute(httpPostLeftAnchorFalse);
            } else {
                rawResponse = httpClient.execute(httpPostLeftAnchorTrue);
            }
            String response = convertInputStreamToString(rawResponse.getEntity().getContent());
            bioCommonsVCFVariants = convertToBioCommonsVariants(response);
        } catch (IOException e) {
            e.printStackTrace();
            bioCommonsVCFVariants = null;
        }
        return bioCommonsVCFVariants;
    }

    /**
     * Creates a JSON array based on a list of dna notation
     * @param dnaNotations the list containg the dna notations
     * @return json string
     */
    private String createJSONArray(ArrayList<String> dnaNotations) {
        String json = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(dnaNotations);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Converts an input stream to a string
     * @param inputStream the input stream
     * @return a string containing the information from the input string
     */
    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * Converts a json array to a list containing BioCommonsVCFVariants
     * @param jsonArray the json array
     * @return a List containing BioCommonsVCFVariants
     * @throws IOException if object mapper can't read the array
     */
    private List<BioCommonsVCFVariant> convertToBioCommonsVariants(String jsonArray) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(jsonArray, BioCommonsVCFVariant[].class));
    }
}
