package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.api;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


public class WikidataControllerTest {
    private WikidataService wikidataService;
    private WikiDataAPIController wikiDataAPIController;

    @BeforeEach
    void setUp() {
        wikidataService = mock(WikidataService.class);
        wikiDataAPIController = new WikiDataAPIController(wikidataService);
    }

    @Test
    void givenPlantExists_whenSearchTomato_thenReturnInformationAndImage() throws Exception {
        String plantInfo = "[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]";
        Mockito.when(wikidataService.getPlantInfo("tomato")).thenReturn(plantInfo);

        String response = wikiDataAPIController.searchPlant("tomato");
        assertEquals(response,plantInfo);
    }

    @Test
    void givenPlantNotExist_whenSearchReturnsNoResults_thenReturnEmptyList() throws Exception {
        String plantInfo = "[]";
        Mockito.when(wikidataService.getPlantInfo("nonexistentplant")).thenReturn(plantInfo);

        String response = wikiDataAPIController.searchPlant("nonexistentplant");
        assertEquals(response,plantInfo);

    }
}
