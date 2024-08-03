package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WikidataService {

    Logger logger = LoggerFactory.getLogger(WikidataService.class);
    private static final String SEARCH_ENDPOINT = "https://www.wikidata.org/w/api.php?action=wbsearchentities&format=json&language=en&type=item&limit=10&search=";
    private static final String ENTITY_ENDPOINT = "https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&ids=";
    //IDs refer to category: Fruit, vegetable, shrub, herb, tree, fruit vegetable, table apple, perennial plant(e.g. catnip), tracheophyta(succulent)
    //This list is not exhaustive as wikidata keeps thousands of different categories of plants
    private static final List<String> CATEGORY_IDS = List.of("Q3314483","Q11004","Q42295","Q207123","Q10884","Q1470762","Q3395974","Q157957","Q27133");

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WikidataService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Send search request to API and return parsed responses
     * @param plantName to be searched
     * @return JsonNode with a list of PlantInfoDTOs
     */
    public JsonNode getPlantInfo(String plantName) {
        String url = SEARCH_ENDPOINT + plantName;
        logger.info("Sending search request...");
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, constructEntity(), String.class);
        String response = responseEntity.getBody();
        logger.info("{}", response);

        JsonNode jsonNode = readJson(response);

        List<PlantInfoDTO> plantInfoList = new ArrayList<>();
        if (jsonNode.has("search") && !jsonNode.get("search").isEmpty()) {
            for (JsonNode entityNode : jsonNode.get("search")) {
                String entityId = entityNode.get("id").asText();
                if (isSubclassOfGardenPlants(entityId)) {
                    String imageUrl = getImageUrl(entityId);
                    PlantInfoDTO plantInfo = new PlantInfoDTO(
                            entityNode.get("label").asText(),
                            entityNode.get("description").asText(),
                            entityId,
                            imageUrl
                    );
                    plantInfoList.add(plantInfo);
                }
            }
        }
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode resultNode = factory.objectNode();
        resultNode.set("plants", objectMapper.valueToTree(plantInfoList));
        return resultNode;
    }

    private JsonNode readJson(String response) {
        try {
            return objectMapper.readTree(response);
        } catch (IOException e) {
            throw new RuntimeException("Error processing response", e);
        }
    }

    private HttpEntity<String> constructEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Gardener's Grove/0.0; https://csse-seng302-team800.canterbury.ac.nz/prod/; team800.garden@gmail.com");
        return new HttpEntity<>(headers);
    }
    private boolean isSubclassOfGardenPlants(String entityId) {
        String url = ENTITY_ENDPOINT + entityId;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, constructEntity(), String.class);
        String response = responseEntity.getBody();
        JsonNode jsonNode = readJson(response);
        JsonNode claims = jsonNode.path("entities").path(entityId).path("claims").path("P279"); // P279 is the property for subclass of

        if (claims.isArray()) {
            for (JsonNode claim : claims) {
                JsonNode dataValue = claim.path("mainsnak").path("datavalue").path("value");
                String valueId = dataValue.path("id").asText();
                if (CATEGORY_IDS.contains(valueId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getImageUrl(String entityId) {
        String url = ENTITY_ENDPOINT + entityId;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, constructEntity(), String.class);
        String response = responseEntity.getBody();
        JsonNode jsonNode = readJson(response);
        JsonNode claims = jsonNode.path("entities").path(entityId).path("claims").path("P18");
        if (claims.isArray() && !claims.isEmpty()) {
            String imageFilename = claims.get(0).path("mainsnak").path("datavalue").path("value").asText();
            return "https://commons.wikimedia.org/wiki/Special:FilePath/" + imageFilename;
        }
        return "";
    }
}
