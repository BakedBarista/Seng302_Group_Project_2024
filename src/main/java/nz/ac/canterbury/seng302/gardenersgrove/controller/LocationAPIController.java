package nz.ac.canterbury.seng302.gardenersgrove.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
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


    /**
     * Return result from the location API request
     *
     * @param currentValue The value where the location data is requested
     * @return location information in json format
     */
    @GetMapping("/location-autocomplete")
    public ResponseEntity<String> getLocationData(@RequestParam String currentValue) {
        logger.info("Requesting API autocomplete");
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
        return restTemplate.getForEntity(url, String.class);
    }


    /**
     * Requests the API for the geocoding information for a manually inputted address
     * @param location the manual address as a string
     * @return location information in JSON
     */
    @GetMapping("/location-search")
    public ResponseEntity<String> getManualLocationData(@RequestParam String location) {
        logger.info("Requesting API for location match");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        ResponseEntity<String> request = requestAPI(location, 1);
        logger.info("API: {}", request.getBody());
        return request;
    }

    /**
     * Requests the location API.
     * @param location the location to request
     * @param limit the number of results to return
     * @return the API response
     */
    private ResponseEntity<String> requestAPI(String location, int limit) {
        try {
            String url = "https://api.geoapify.com/v1/geocode/autocomplete"
                    + "?text=" + URLEncoder.encode(location, StandardCharsets.UTF_8)
                    + "&format=json"
                    + "&limit=" + limit
                    + "&apiKey=" + location_apiKey;

            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException e) {
             if (e.getStatusCode().value() == 401) {
                logger.error("Authentication issue with location API, check API key.");
            } else {
                logger.error("An unknown error occurred with the location API.", e);
            }
        }
        return null;
    }
}


