package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {
    @Value("${geoapify.api.key}")
    private String LOCATION_API_KEY;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(LocationService.class);

    public LocationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Geocodes the lat and lng of a location based on a search string
     * @param location the location string to search for
     * @return the Lat and Lng in a list [Lat, Lng]
     */
    public List<Double> getLatLng(String location) {
        try {
            ArrayList<Double> latAndLng = new ArrayList<>();
            logger.info("Requesting location from API");
            String url = "https://api.geoapify.com/v1/geocode/autocomplete"
                    + "?text=" + URLEncoder.encode(location, StandardCharsets.UTF_8)
                    + "&format=json"
                    + "&limit=" + 1
                    + "&apiKey=" + LOCATION_API_KEY;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());
            JsonNode results = json.path("results");

            if (!results.isEmpty()){
                JsonNode result  = results.get(0);
                latAndLng.add(result.path("lat").asDouble());
                latAndLng.add(result.path("lon").asDouble());
                logger.info("Location returned Lat: {}, Lon: {}", latAndLng.get(0), latAndLng.get(1));
            }

            return latAndLng;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                logger.error("Authentication issue with location API, check API key.");
            } else {
                logger.error("An unknown error occurred with the location API.", e);
            }
        } catch (JsonProcessingException e) {
            logger.error("Issue processing location API response JSON.");
        }
        return new ArrayList<>();
    }
}
