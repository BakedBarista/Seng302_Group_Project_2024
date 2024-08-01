package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent","Gardener's Grove/0.0; https://csse-seng302-team800.canterbury.ac.nz/prod/; team800.garden@gmail.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String response = responseEntity.getBody();
        logger.info("{}",response);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.get("search").isEmpty()) {
                return "[]";
            }

            StringBuilder result = new StringBuilder();
            for (JsonNode entityNode : jsonNode.get("search")) {
                String entityId = entityNode.get("id").asText();
                String imageUrl = getImageUrl(entityId);
                result.append("{")
                        .append("\"label\":\"").append(entityNode.get("label").asText()).append("\",")
                        .append("\"description\":\"").append(entityNode.get("description").asText()).append("\",")
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent","Gardener's Grove/0.0; https://csse-seng302-team800.canterbury.ac.nz/prod/; team800.garden@gmail.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody();
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
