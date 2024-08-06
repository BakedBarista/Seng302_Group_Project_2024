package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.service.ExternalServiceException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        String searchResponse = "{\"search\":[{\"id\":\"Q235\",\"label\":\"Tomato\",\"description\":\"A red fruit\"}]}";
        ResponseEntity<String> searchEntity = ResponseEntity.ok(searchResponse);
        when(restTemplate.exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(searchEntity);

        String entityResponse = "{\"entities\":{\"Q235\":{\"claims\":{\"P18\":[{\"mainsnak\":{\"datavalue\":{\"value\":\"Tomato.jpg\"}}}],\"P279\":[{\"mainsnak\":{\"datavalue\":{\"value\":{\"entity-type\":\"item\",\"numeric-id\":11004,\"id\":\"Q11004\"}}}}]}}}}";
        ResponseEntity<String> entityEntity = ResponseEntity.ok(entityResponse);
        when(restTemplate.exchange(Mockito.contains("wbgetentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(entityEntity);

        JsonNode result = wikidataService.getPlantInfo("tomato");

        JsonNode expected = objectMapper.readTree("{\"plants\":[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\",\"formatted\":\"<strong>Tomato</strong> &ndash; <em>A red fruit</em>\"}]}");

        assertEquals(expected, result);

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

        JsonNode result = wikidataService.getPlantInfo("nonexistentplant");

        JsonNode expected = objectMapper.readTree("{\"plants\":[]}");

        assertEquals(expected, result);

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
        assertThrows(ExternalServiceException.class, () -> wikidataService.getPlantInfo("tomato"));
    }

    @Test
    void givenExternalServiceException_whenGetPlantInfo_thenReturnErrorMessage() throws ExternalServiceException, JsonProcessingException {
        String plantName = "tomato";
        when(restTemplate.exchange(Mockito.contains("wbsearchentities"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            wikidataService.getPlantInfo(plantName);
        });

        assertEquals("Unable to fetch plant info from Wikidata", exception.getMessage());
    }


}
