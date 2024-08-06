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

class WikidataAPIControllerTest {
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
        String plantInfo = "{\"plants\":[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]}";
        JsonNode plantInfoJson = objectMapper.readTree(plantInfo);
        Mockito.when(wikidataService.getPlantInfo("tomato")).thenReturn(plantInfoJson);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlant("tomato");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(plantInfoJson, response.getBody());
    }

    @Test
    void givenPlantNotExist_whenSearchReturnsNoResults_thenReturnEmptyList() throws Exception {
        JsonNode plantInfoJson = objectMapper.readTree("{\"plants\":[]}");
        when(wikidataService.getPlantInfo("nonexistentplant")).thenReturn(plantInfoJson);
        String plantInfo = "{\"plants\":[]}";
        JsonNode plantInfoJson = objectMapper.readTree(plantInfo);
        Mockito.when(wikidataService.getPlantInfo("nonexistentplant")).thenReturn(plantInfoJson);

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

    @Test
    void givenPlantExists_whenAutocompleteTomato_thenReturnInformationAndImage() throws Exception {
        String plantInfo = "{\"plants\":[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]}";
        JsonNode plantInfoJson = objectMapper.readTree(plantInfo);
        Mockito.when(wikidataService.getPlantInfo("tomato")).thenReturn(plantInfoJson);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlantAutocomplete("tomato");

        String expected = "{\"results\":[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]}";
        JsonNode expectedJson = objectMapper.readTree(expected);
        assertEquals(expectedJson, response.getBody());
    }

    @Test
    void givenPlantNotExist_whenAutocompleteReturnsNoResults_thenReturnEmptyList() throws Exception {
        String plantInfo = "{\"plants\":[]}";
        JsonNode plantInfoJson = objectMapper.readTree(plantInfo);
        Mockito.when(wikidataService.getPlantInfo("nonexistentplant")).thenReturn(plantInfoJson);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlantAutocomplete("nonexistentplant");

        String expected = "{\"results\":[]}";
        JsonNode expectedJson = objectMapper.readTree(expected);
        assertEquals(expectedJson, response.getBody());
    }
}
