package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


public class WikidataControllerTest {
    private WikidataService wikidataService;
    private WikiDataAPIController wikiDataAPIController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        wikidataService = mock(WikidataService.class);
        wikiDataAPIController = new WikiDataAPIController(wikidataService);
        objectMapper = new ObjectMapper();

    }

    @Test
    void givenPlantExists_whenSearchTomato_thenReturnInformationAndImage() throws Exception {
        String plantInfo = "[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]";
        JsonNode plantInfoJson = objectMapper.readTree(plantInfo);
        Mockito.when(wikidataService.getPlantInfo("tomato")).thenReturn(plantInfoJson);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlant("tomato");
        assertEquals(response.getBody(), plantInfoJson);
    }

    @Test
    void givenPlantNotExist_whenSearchReturnsNoResults_thenReturnEmptyList() throws Exception {
        String plantInfo = "[]";
        JsonNode plantInfoJson = objectMapper.readTree(plantInfo);
        Mockito.when(wikidataService.getPlantInfo("nonexistentplant")).thenReturn(plantInfoJson);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlant("nonexistentplant");
        assertEquals(response.getBody(), plantInfoJson);
    }
}
