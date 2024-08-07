package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocalPlantDataService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;


class WikidataAPIControllerTest {
    private WikidataService wikidataService;
    private WikiDataAPIController wikiDataAPIController;
    private LocalPlantDataService localPlantDataService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        wikidataService = mock(WikidataService.class);
        localPlantDataService = mock(LocalPlantDataService.class);
        objectMapper = new ObjectMapper();
        wikiDataAPIController = new WikiDataAPIController(wikidataService, localPlantDataService, objectMapper);

    }

    @Test
    void givenPlantExists_whenAutocompleteTomato_thenReturnInformationAndImage() throws Exception {
        String plantInfo = "[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]";
        List<PlantInfoDTO> plantInfoList = objectMapper.readValue(plantInfo, new TypeReference<List<PlantInfoDTO>>() {
        });
        when(wikidataService.getPlantInfo("tomato")).thenReturn(plantInfoList);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlantAutocomplete("tomato");

        String expected = "{\"results\":[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\",\"formatted\":\"<strong>Tomato</strong> &ndash; <em>A red fruit</em>\"}]}";
        assertEquals(expected, response.getBody().toString());
    }

    @Test
    void givenPlantNotExist_whenAutocompleteReturnsNoResults_thenReturnEmptyList() throws Exception {
        String plantInfo = "[]";
        List<PlantInfoDTO> plantInfoList = objectMapper.readValue(plantInfo, new TypeReference<List<PlantInfoDTO>>() {
        });

        when(localPlantDataService.getSimilarPlantInfo(anyString())).thenReturn(plantInfoList);
        when(wikidataService.getPlantInfo("tomato")).thenReturn(plantInfoList);

        ResponseEntity<JsonNode> response = wikiDataAPIController.searchPlantAutocomplete("nonexistentplant");

        String expected = "{\"results\":[]}";
        assertEquals(expected, response.getBody().toString());
    }
}
