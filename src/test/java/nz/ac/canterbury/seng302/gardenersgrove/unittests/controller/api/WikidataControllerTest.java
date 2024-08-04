package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.service.ExternalServiceException;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WikidataControllerTest {
    private WikidataService wikidataService;
    private WikiDataAPIController wikiDataAPIController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        wikidataService = mock(WikidataService.class);
        objectMapper = new ObjectMapper();
        wikiDataAPIController = new WikiDataAPIController(wikidataService, objectMapper);
    }

    @Test
    void givenPlantExists_whenSearchTomato_thenReturnInformationAndImage() throws Exception {
        JsonNode plantInfoJson = objectMapper.readTree("{\"plants\":[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]}");
        when(wikidataService.getPlantInfo("tomato")).thenReturn(plantInfoJson);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlant("tomato");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(plantInfoJson, response.getBody());
    }

    @Test
    void givenPlantNotExist_whenSearchReturnsNoResults_thenReturnEmptyList() throws Exception {
        JsonNode plantInfoJson = objectMapper.readTree("{\"plants\":[]}");
        when(wikidataService.getPlantInfo("nonexistentplant")).thenReturn(plantInfoJson);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlant("nonexistentplant");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(plantInfoJson, response.getBody());
    }

    @Test
    void givenExternalServiceException_whenSearchPlant_thenReturnServiceUnavailableMessage() throws ExternalServiceException {
        when(wikidataService.getPlantInfo("tomato")).thenThrow(new ExternalServiceException("Service unavailable"));

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlant("tomato");

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("{\"error\":\"Plant information service is unavailable at the moment, please try again later\"}", response.getBody().toString());
    }
}
