package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ModerationService {
    Logger logger = LoggerFactory.getLogger(ModerationService.class);

    private String moderationApiKey;
    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;

    private static final String MODERATION_API_URL = "https://api.openai.com/v1/moderations";

    public ModerationService(@Value("${moderation.api.key}") String moderationApiKey, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.moderationApiKey = moderationApiKey;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }


    /**
     * Calls OPENAI Text Moderation API with given description
     * @param description garden description
     * @return moderation response in json format
     */
    public ResponseEntity<String> moderateDescription(String description) {
        logger.info("Moderating description");

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("input", description);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            // This should never happen as it is always possible to encode a string using JSON
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + moderationApiKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

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
        if (description == null || description.isEmpty()) {
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
