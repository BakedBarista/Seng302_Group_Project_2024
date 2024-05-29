package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class LocationServiceTest {
    private LocationService locationService;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        locationService = new LocationService(restTemplate, objectMapper);
    }

    @Test
    void GetLatLng_ValidLocation_ReturnsLatLng() throws Exception {
        String location = "2 Janet Street Christchurch 8041 New Zealand";

        String jsonResponse = "{\"results\":[{\"lat\":-43.5320,\"lon\":172.6366}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));

        List<Double> latAndLng = locationService.getLatLng(location);

        assertEquals(-43.5320, latAndLng.get(0), 0);
        assertEquals(172.6366, latAndLng.get(1), 0);
    }

    @Test
    void GetLatLng_InvalidLocation_ReturnsEmptyList() throws Exception {
        String location = "askdfjhaksjdhf";

        String jsonResponse = "{\"results\":[]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));

        List<Double> latAndLng = locationService.getLatLng(location);

        assertTrue(latAndLng.isEmpty());
    }

    @Test
    void GetLatLng_401Response_CatchesError() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED, "Unauthorized", null, null, null);

        doThrow(exception).when(restTemplate).getForEntity(anyString(), Mockito.eq(String.class));
        List<Double> result = locationService.getLatLng("2 Janet Street Upper Riccarton 8041 New Zealand");
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void GetLatLng_OtherHTTPError_CatchesError() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", null, null, null);

        doThrow(exception).when(restTemplate).getForEntity(anyString(), Mockito.eq(String.class));
        List<Double> result = locationService.getLatLng("2 Janet Street Upper Riccarton 8041 New Zealand");
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void testGetLatLng_JsonProcessingException() throws Exception {
        String jsonResponse = "{\"results\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(anyString(), Mockito.eq(String.class)))
                .thenReturn(responseEntity);
        Mockito.when(objectMapper.readTree(jsonResponse)).thenThrow(new JsonProcessingException("Error") {});
        List<Double> result = locationService.getLatLng("2 Janet Street Upper Riccarton 8041 New Zealand");
        assertEquals(new ArrayList<>(), result);
    }

}
