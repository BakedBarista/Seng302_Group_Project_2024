package nz.ac.canterbury.seng302.gardenersgrove.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/api")
public class LocationAPIController {
    final Logger logger = LoggerFactory.getLogger(LocationAPIController.class);

    /**
     * API key
     */
    @Value("${geoapify.api.key}")
    private String location_apiKey;

    @Value("${MODERATION_KEY}")
    private String moderation_key;


    /**
     * Return result from the location API request
     * @param currentValue
     * @return location information in json format
     */
    @GetMapping("/get_location")
    public ResponseEntity<String> getLocationData(@RequestParam String currentValue) {
        logger.info("GET /api/get_location {}", currentValue );
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String url = "https://api.geoapify.com/v1/geocode/autocomplete"
                + "?text=" + URLEncoder.encode(currentValue, StandardCharsets.UTF_8)
                + "&format=json"
                + "&limit=5"
                + "&apiKey=" + location_apiKey;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        logger.info("URL {}", url);
        logger.info("Response from API: {}", response);

        return response;

    }
}


