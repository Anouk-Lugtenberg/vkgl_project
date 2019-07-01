package org.molgenis.vkgl.biocommons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.molgenis.vkgl.service.VariantToVCFConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BioCommonsHelper {
    private HttpClient httpClient;
    private HttpPost httpPostLeftAnchorTrue;
    private HttpPost httpPostLeftAnchorFalse;
    private static Logger LOGGER = LogManager.getLogger(BioCommonsHelper.class.getName());

    public BioCommonsHelper() {
        this.httpClient = HttpClients.createDefault();
        this.httpPostLeftAnchorTrue = new HttpPost("http://localhost:1234/h2v?keep_left_anchor=True");
        this.httpPostLeftAnchorFalse = new HttpPost("http://localhost:1234/h2v?keep_left_anchor=False");
    }

    public BioCommonsVCFVariant postDNANotation(String DNANotation, boolean isSNP) {
        String jsonString = createJSON(DNANotation);
        StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        if (isSNP) {
            httpPostLeftAnchorFalse.setEntity(requestEntity);
        } else {
            httpPostLeftAnchorTrue.setEntity(requestEntity);
        }

        BioCommonsVCFVariant bioCommonsVCFVariant = new BioCommonsVCFVariant();
        try {
            HttpResponse rawResponse;
            if (isSNP) {
                rawResponse = httpClient.execute(httpPostLeftAnchorFalse);
            } else {
                rawResponse = httpClient.execute(httpPostLeftAnchorTrue);
            }
            ObjectMapper mapper = new ObjectMapper();
            BufferedReader rd = new BufferedReader(new InputStreamReader(rawResponse.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                try {
                    BioCommonsVCFVariant[] bioCommonsVCFVariantArray = mapper.readValue(line, BioCommonsVCFVariant[].class);
                    bioCommonsVCFVariant = bioCommonsVCFVariantArray[0];
                } catch (UnrecognizedPropertyException e) {
                    LOGGER.info("Somethings went wrong while retrieving the information with DNA notation: {} from BioCommons", DNANotation);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bioCommonsVCFVariant;
    }

    private String createJSON(String genomicDNANotation) {
        String json = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(genomicDNANotation);
            json = "[" + json + "]";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}
