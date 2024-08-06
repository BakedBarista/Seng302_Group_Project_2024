package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

class WikidataServiceTest {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private WikidataService wikidataService;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        wikidataService = new WikidataService(restTemplate, objectMapper);
    }

    @Test
    void givenPlantExists_whenSearchTomato_thenReturnInformationAndImage() throws Exception {
        String searchResponse = "{\"search\":[{\"id\":\"Q235\",\"label\":\"tomato\",\"description\":\"a red fruit\"}]}";
        ResponseEntity<String> searchEntity = ResponseEntity.ok(searchResponse);
        when(restTemplate.exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(searchEntity);

        String entityResponse = "{\"entities\":{\"Q235\":{\"claims\":{\"P18\":[{\"mainsnak\":{\"datavalue\":{\"value\":\"Tomato.jpg\"}}}],\"P279\":[{\"mainsnak\":{\"datavalue\":{\"value\":{\"entity-type\":\"item\",\"numeric-id\":11004,\"id\":\"Q11004\"}}}}]}}}}";
        ResponseEntity<String> entityEntity = ResponseEntity.ok(entityResponse);
        when(restTemplate.exchange(Mockito.contains("wbgetentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(entityEntity);

        List<PlantInfoDTO> result = wikidataService.getPlantInfo("tomato");

        assertEquals(1, result.size());
        assertEquals("Tomato", result.get(0).getLabel());
        assertEquals("A red fruit", result.get(0).getDescription());
        assertEquals("Q235", result.get(0).getId());
        assertEquals("https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg", result.get(0).getImage());
        assertEquals("<strong>Tomato</strong> &ndash; <em>A red fruit</em>", result.get(0).getFormatted());

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate).exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), entityCaptor.capture(), eq(String.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals("Gardener's Grove/0.0; https://csse-seng302-team800.canterbury.ac.nz/prod/; team800.garden@gmail.com", headers.getFirst("User-Agent"));
    }

    @Test
    void givenPlantNotExist_whenSearchReturnsNoResults_thenReturnEmptyList() throws Exception {
        String searchResponse = "{\"search\":[]}";
        ResponseEntity<String> searchEntity = ResponseEntity.ok(searchResponse);
        when(restTemplate.exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(searchEntity);

        List<PlantInfoDTO> result = wikidataService.getPlantInfo("nonexistentplant");
        assertTrue(result.isEmpty());

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate).exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), entityCaptor.capture(), eq(String.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals("Gardener's Grove/0.0; https://csse-seng302-team800.canterbury.ac.nz/prod/; team800.garden@gmail.com", headers.getFirst("User-Agent"));
    }

    @Test
    void givenRuntimeException_whenSearchTomato_thenThrowRuntimeException() {
        // Set up the mock to throw RuntimeException
        when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class), Mockito.eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        // Verify that the exception is thrown
        assertThrows(RuntimeException.class, () -> wikidataService.getPlantInfo("tomato"));
    }

    @Test
    void givenEmptyFields_whenSearchTomato_thenReturnEmptyFields() {
        String searchResponse = "{\"search\":[{\"id\":\"Q235\",\"label\":\"\",\"description\":\"\"}]}";
        ResponseEntity<String> searchEntity = ResponseEntity.ok(searchResponse);
        when(restTemplate.exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(searchEntity);

        String entityResponse = "{\"entities\":{\"Q235\":{\"claims\":{\"P18\":[{\"mainsnak\":{\"datavalue\":{\"value\":\"Tomato.jpg\"}}}],\"P279\":[{\"mainsnak\":{\"datavalue\":{\"value\":{\"entity-type\":\"item\",\"numeric-id\":11004,\"id\":\"Q11004\"}}}}]}}}}";
        ResponseEntity<String> entityEntity = ResponseEntity.ok(entityResponse);
        when(restTemplate.exchange(Mockito.contains("wbgetentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(entityEntity);

        List<PlantInfoDTO> result = wikidataService.getPlantInfo("tomato");

        assertEquals(1, result.size());
        assertEquals("", result.get(0).getLabel());
        assertEquals("", result.get(0).getDescription());
        assertEquals("Q235", result.get(0).getId());
        assertEquals("https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg", result.get(0).getImage());
    }

    @Test
    void givenNotAPlant_whenSearchCar_thenReturnNoPlants() {
        String searchResponse = "{\"search\":[{\"id\":\"Q1420\",\"label\":\"motor car\",\"description\":\"motorized road vehicle designed to carry one to eight people rather than primarily goods\"}]}";
        ResponseEntity<String> searchEntity = ResponseEntity.ok(searchResponse);
        when(restTemplate.exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(searchEntity);

        String entityResponse = "{\"entities\":{\"Q1420\":{\"claims\":{\"P18\":[{\"mainsnak\":{\"datavalue\":{\"value\":\"Car.jpg\"}}}],\"P279\":[{\"mainsnak\":{\"datavalue\":{\"value\":{\"entity-type\":\"item\",\"numeric-id\":752870,\"id\":\"Q752870\"}}}}]}}}}";
        ResponseEntity<String> entityEntity = ResponseEntity.ok(entityResponse);
        when(restTemplate.exchange(Mockito.contains("wbgetentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(entityEntity);

        List<PlantInfoDTO> result = wikidataService.getPlantInfo("tomato");

        assertEquals(0, result.size());
    }
}
