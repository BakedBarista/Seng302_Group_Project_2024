package nz.ac.canterbury.seng302.gardenersgrove.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


public class ModerationService {
    //API key
    Logger logger = LoggerFactory.getLogger(ModerationService.class);
    @Value("${MODERATION_KEY}")
    private String moderation_apiKey;

    private static final String MODERATION_API_URL = "https://api.openai.com/v1/moderations";

    private final RestTemplate restTemplate;

    public ModerationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> moderateDescription(String description) {
        String requestBody = "{\"input\": \"" + description + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + moderation_apiKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody,headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(MODERATION_API_URL, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        } else {
            throw new RuntimeException("Can not moderate description");
        }
    }
}
