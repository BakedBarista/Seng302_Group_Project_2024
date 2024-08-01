package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class WikidataServiceTest {

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
        ResponseEntity<String> searchEntity = ResponseEntity.ok(searchResponse);
        when(restTemplate.exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(searchEntity);

        String entityResponse = "{\"entities\":{\"Q235\":{\"claims\":{\"P18\":[{\"mainsnak\":{\"datavalue\":{\"value\":\"Tomato.jpg\"}}}]}}}}";
        ResponseEntity<String> entityEntity = ResponseEntity.ok(entityResponse);
        when(restTemplate.exchange(Mockito.contains("wbgetentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(entityEntity);

        JsonNode searchNode = new ObjectMapper().readTree(searchResponse);
        JsonNode entityNode = new ObjectMapper().readTree(entityResponse);
        when(objectMapper.readTree(searchResponse)).thenReturn(searchNode);
        when(objectMapper.readTree(entityResponse)).thenReturn(entityNode);

        String result = wikidataService.getPlantInfo("tomato");

        String expected = "[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]";

        assertEquals(expected, result);

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate).exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), entityCaptor.capture(), eq(String.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals("Gardener's Grove/0.0; https://csse-seng302-team800.canterbury.ac.nz/prod/; team800.garden@gmail.com", headers.getFirst("User-Agent"));
    }

    @Test
    void givenPlantNotExist_whenSearchReturnsNoResults_thenReturnEmptyList() throws IOException {
        String searchResponse = "{\"search\":[]}";
        ResponseEntity<String> searchEntity = ResponseEntity.ok(searchResponse);
        when(restTemplate.exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(searchEntity);

        JsonNode searchNode = new ObjectMapper().readTree(searchResponse);
        when(objectMapper.readTree(searchResponse)).thenReturn(searchNode);

        String result = wikidataService.getPlantInfo("nonexistentplant");

        String expected = "[]";

        assertEquals(expected, result);

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate).exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), entityCaptor.capture(), eq(String.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals("Gardener's Grove/0.0; https://csse-seng302-team800.canterbury.ac.nz/prod/; team800.garden@gmail.com", headers.getFirst("User-Agent"));
    }

}
