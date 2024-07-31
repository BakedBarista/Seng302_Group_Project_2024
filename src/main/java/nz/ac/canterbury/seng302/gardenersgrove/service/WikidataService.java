package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class WikidataService {

    Logger logger = LoggerFactory.getLogger(WikidataService.class);
    private static final String SEARCH_ENDPOINT = "https://www.wikidata.org/w/api.php?action=wbsearchentities&format=json&language=en&type=item&limit=10&search=";
    private static final String ENTITY_ENDPOINT = "https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&ids=";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WikidataService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     *
     * @param plantName to be looked up in the database
     * @return Parsed json string to be displayed
     */
    public String getPlantInfo(String plantName) {
        String url = SEARCH_ENDPOINT + plantName;
        logger.info("Sending search request...");
        String response = restTemplate.getForObject(url, String.class);
        logger.info("{}",response);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.get("search").isEmpty()) {
                return "[]";
            }

            StringBuilder result = new StringBuilder();
            for (JsonNode entity : jsonNode.get("search")) {
                String entityId = entity.get("id").asText();
                String imageUrl = getImageUrl(entityId);
                result.append("{")
                        .append("\"label\":\"").append(entity.get("label").asText()).append("\",")
                        .append("\"description\":\"").append(entity.get("description").asText()).append("\",")
                        .append("\"id\":\"").append(entityId).append("\",")
                        .append("\"image\":\"").append(imageUrl).append("\"")
                        .append("},");
            }
            if (!result.isEmpty()) {
                result.setLength(result.length() - 1);
            }
            return "[" + result + "]";
        } catch (IOException e) {
            logger.info("{}",e.toString());
            return "Error processing response";
        }
    }

    /**
     *
     * @param entityId used to retrieve image
     * @return image path for the given entity
     */
    private String getImageUrl(String entityId) {
        String url = ENTITY_ENDPOINT + entityId;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode claims = jsonNode.path("entities").path(entityId).path("claims").path("P18");
            if (claims.isArray() && !claims.isEmpty()) {
                String imageFilename = claims.get(0).path("mainsnak").path("datavalue").path("value").asText();
                return "https://commons.wikimedia.org/wiki/Special:FilePath/" + imageFilename;
            }
        } catch (IOException e) {
            logger.error("Error retrieving image URL", e);
        }
        return "";
    }
}
