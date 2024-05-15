package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.h2.util.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import nz.ac.canterbury.seng302.gardenersgrove.service.ProfranityService;

import java.util.Arrays;

@Service
public class ModerationService {
    Logger logger = LoggerFactory.getLogger(ModerationService.class);

    @Value("${moderation.api.key}")
    private String moderationApiKey;

    private static final String MODERATION_API_URL = "https://api.openai.com/v1/moderations";

    private static final String PROFANITY_URL = "https://www.purgomalum.com/service/";


    /**
     * Calls OPENAI Text Moderation API with given description
     * @param description garden description
     * @return moderation response in json format
     */
    public ResponseEntity<String> moderateDescription(String description) {
        logger.info("Moderating description");

        String requestBody = "{\"input\": \"" + description + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + moderationApiKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(MODERATION_API_URL, requestEntity, String.class);


        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            logger.info("Description moderated");
            return responseEntity;
        } else {
            throw new RuntimeException("Can not moderate description");
        }
    }

    /**
     * Check if description is flagged as inappropriate
     * @param description
     * @return
     */
    public boolean checkIfDescriptionIsFlagged(String description) {
        if (description.isEmpty()) {
            return false;
        }

        String responseBody = moderateDescription(description).getBody();

        // note - this may cause problems if OpenAI changes the structure of the content they
        // return as this doesn't properly parse the json string - could not find anything in java to do this
        if (responseBody != null) {
            String flaggedValue = responseBody.split("\"flagged\": ")[1].split(",")[0];
            return flaggedValue.equals("true");
        }

        // if no response, return false...
        return false;
    }
}
