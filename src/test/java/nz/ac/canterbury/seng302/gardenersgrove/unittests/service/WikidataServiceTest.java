package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class WikidataServiceTest {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private WikidataService wikidataService;

    @BeforeEach
    void setup() {
        restTemplate = Mockito.mock(RestTemplate.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        wikidataService = new WikidataService(restTemplate,objectMapper);
    }

    @Test
    void givenPlantExists_whenSearchTomato_thenReturnInformationAndImage() throws IOException {
        String searchResponse = "{\"search\":[{\"id\":\"Q235\",\"label\":\"Tomato\",\"description\":\"A red fruit\"}]}";
        when(restTemplate.getForObject(Mockito.contains("wbsearchentities"), eq(String.class)))
                .thenReturn(searchResponse);

        String entityResponse = "{\"entities\":{\"Q235\":{\"claims\":{\"P18\":[{\"mainsnak\":{\"datavalue\":{\"value\":\"Tomato.jpg\"}}}]}}}}";
        when(restTemplate.getForObject(Mockito.contains("wbgetentities"), eq(String.class)))
                .thenReturn(entityResponse);

        JsonNode searchNode = new ObjectMapper().readTree(searchResponse);
        JsonNode entityNode = new ObjectMapper().readTree(entityResponse);
        when(objectMapper.readTree(searchResponse)).thenReturn(searchNode);
        when(objectMapper.readTree(entityResponse)).thenReturn(entityNode);

        String result = wikidataService.getPlantInfo("tomato");

        String expected = "[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]";

        assertEquals(expected, result);
    }

    @Test
    void givenPlantNotExist_whenSearchReturnsNoResults_thenReturnEmptyList() throws IOException {
        String searchResponse = "{\"search\":[]}";
        when(restTemplate.getForObject(Mockito.contains("wbsearchentities"), eq(String.class)))
                .thenReturn(searchResponse);

        JsonNode searchNode = new ObjectMapper().readTree(searchResponse);
        when(objectMapper.readTree(searchResponse)).thenReturn(searchNode);

        String result = wikidataService.getPlantInfo("nonexistentplant");

        String expected = "[]";

        assertEquals(expected, result);
    }

}
