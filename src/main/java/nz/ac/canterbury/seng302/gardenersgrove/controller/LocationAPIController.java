package nz.ac.canterbury.seng302.gardenersgrove.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class LocationAPIController {
    final Logger logger = LoggerFactory.getLogger(LocationAPIController.class);

    /**
     * API key
     */
    @Value("${geoapify.api.key}")
    private String apiKey;

    /**
     * Gets the API key for location API
     *
     * @return API key
     */
    @GetMapping("/get_api_key")
    public String getApiKey() {
        logger.info("GET /api/get_api_key");
        return apiKey;
    }
}


