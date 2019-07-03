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

    public List<BioCommonsVCFVariant> getBioCommonsVariants(ArrayList<VCFVariant> vcfVariants, boolean isSNP) {
        ArrayList<String> dnaNotations = new ArrayList<>();
        List<BioCommonsVCFVariant> bioCommonsVCFVariants;
        for (VCFVariant vcfVariant : vcfVariants) {
            dnaNotations.add(vcfVariant.getDnaNotation());
        }
        String json = createJSONArray(dnaNotations);
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
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

    private List<BioCommonsVCFVariant> convertToBioCommonsVariants(String jsonArray) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(jsonArray, BioCommonsVCFVariant[].class));
    }
}
