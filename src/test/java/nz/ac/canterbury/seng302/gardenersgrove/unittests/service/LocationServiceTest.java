package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

public class LocationService {
    @Autowired
    private GeoapifyService geoapifyService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ObjectMapper objectMapper;

    @Value("${geoapify.api.key}")
    private String locationApiKey;

    @Test
    public void testGetLatLng() throws Exception {
        String location = "Some location";
        String url = "https://api.geoapify.com/v1/geocode/autocomplete"
                + "?text=" + URLEncoder.encode(location, StandardCharsets.UTF_8)
                + "&format=json"
                + "&limit=1"
                + "&apiKey=" + locationApiKey;

        String jsonResponse = "{\"results\":[{\"lat\":-43.5320,\"lon\":172.6366}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);
        when(objectMapper.readTree(jsonResponse)).thenReturn(new ObjectMapper().readTree(jsonResponse));

        ArrayList<Double> latAndLng = geoapifyService.getLatLng(location);

        assertEquals(-43.5320, latAndLng.get(0), 0);
        assertEquals(172.6366, latAndLng.get(1), 0);
    }
}
