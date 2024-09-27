package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocalPlantDataService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * Handles fetching plant data from WikiData API
 */
@RestController
@RequestMapping("/api")
public class WikiDataAPIController {
    Logger logger = LoggerFactory.getLogger(WikiDataAPIController.class);
    private final WikidataService wikidataService;
    private final LocalPlantDataService localPlantDataService;
    private final ObjectMapper objectMapper;

    public WikiDataAPIController(WikidataService wikidataService, LocalPlantDataService localPlantDataService, ObjectMapper objectMapper) {
        this.wikidataService = wikidataService;
        this.localPlantDataService = localPlantDataService;
        this.objectMapper = objectMapper;
    }

    /**
     * Autocomplete search for plants with the given name
     * @param currentValue plant name to be searched
     * @return parsed json data from response
     */
    @GetMapping("/search-plant-autocomplete")
    public CompletableFuture<ResponseEntity<ObjectNode>> searchPlantAutocomplete(@RequestParam String currentValue) {
        return wikidataService.getPlantInfoAsync(currentValue)
            .thenApply(plantInfo -> {
                if (plantInfo.isEmpty()) {
                    plantInfo = localPlantDataService.getSimilarPlantInfo(currentValue);
                }
                ObjectNode results = JsonNodeFactory.instance.objectNode();
                results.set("results", objectMapper.valueToTree(plantInfo));
                return ResponseEntity.ok(results);

            }).exceptionally(ex -> {
                ObjectNode errorMessage = objectMapper.createObjectNode().put("error", "Plant information service is unavailable at the moment, please try again later");
                return ResponseEntity.status(503).body(errorMessage);
            });
    }
}

