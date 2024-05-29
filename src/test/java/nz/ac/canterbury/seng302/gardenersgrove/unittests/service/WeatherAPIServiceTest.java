package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.WeatherData;
import nz.ac.canterbury.seng302.gardenersgrove.model.weather.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.GardenWeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherAPIServiceTest {
    private WeatherAPIService weatherAPIService;
    private GardenService gardenService;
    private GardenWeatherService gardenWeatherService;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        gardenService = Mockito.mock(GardenService.class);
        gardenWeatherService = Mockito.mock(GardenWeatherService.class);
        weatherAPIService = new WeatherAPIService(restTemplate, gardenService, gardenWeatherService);
        objectMapper = Mockito.mock(ObjectMapper.class);
    }

    @ParameterizedTest
    @CsvSource({
            // All of the rainy conditions for WeatherAPI.com
            "Rain",
            "Drizzle",
            "Heavy rain",
            "Light rain",
            "Showers",
            "Thunderstorms",
            "Sleet",
            "Snow",
            "Light snow",
            "Heavy snow",
            "Snow showers"
    })
    void GetWateringRecommendation_CurrentlyRainy_FalseReturned(String conditions) {
        GardenWeather gardenWeather = new GardenWeather();
        WeatherAPICurrentResponse currentResponse = new WeatherAPICurrentResponse();
        Current current = new Current();
        Condition condition = new Condition();
        condition.setConditions(conditions);
        current.setCondition(condition);
        currentResponse.setCurrent(current);

        assertFalse(weatherAPIService.getWateringRecommendation(gardenWeather, currentResponse));
    }

    @ParameterizedTest
    @CsvSource({
            // Current condition is raining, should return false
            "Rain, Clear, Clear, false",
            "Drizzle, Clear, Clear, false",

            // Previous conditions are raining, current is clear, should return false
            "Clear, Rain, Rain, false",
            "Clear, Drizzle, Drizzle, false",

            // No rain at all, should return true
            "Clear, Clear, Clear, true",
            "Partly cloudy, Clear, Clear, true",
    })
    void GetWateringRecommendation_VariousCombinations_CorrectValueReturned(String previousDay1, String previousDay2, String currentCondition, boolean expectedResponse) {
        GardenWeather gardenWeather = new GardenWeather();
        WeatherData day1 = new WeatherData();
        WeatherData day2 = new WeatherData();
        day1.setConditions(previousDay1);
        day2.setConditions(previousDay2);
        gardenWeather.setPreviousWeather(List.of(day1, day2));

        WeatherAPICurrentResponse currentResponse = new WeatherAPICurrentResponse();
        Current current = new Current();
        Condition condition = new Condition();
        condition.setConditions(currentCondition);
        current.setCondition(condition);
        currentResponse.setCurrent(current);

        assertEquals(weatherAPIService.getWateringRecommendation(gardenWeather, currentResponse), expectedResponse);
    }

    @Test
    void GetWeatherData_GardenExistsWeatherValid_WeatherReturnedFromDB() {
        long gardenId = 1L;
        double lat = -43.54;
        double lng = 172.63;

        Garden garden = new Garden();
        GardenWeather gardenWeather = new GardenWeather();
        gardenWeather.setLastUpdated(LocalDate.now().toString());
        garden.setGardenWeather(gardenWeather);

        when(gardenService.getGardenById(gardenId)).thenReturn(Optional.of(garden));

        GardenWeather result = weatherAPIService.getWeatherData(gardenId, lat, lng);

        assertNotNull(result, "GardenWeather should be returned as not null.");
        assertEquals(LocalDate.now().toString(), result.getLastUpdated());
        verify(restTemplate, never()).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void GetWeatherData_GardenDoesNotExist_NullReturned() {
        long gardenId = 1L;
        double lat = -43.54;
        double lng = 172.63;

        when(gardenService.getGardenById(gardenId)).thenReturn(Optional.empty());
        GardenWeather result = weatherAPIService.getWeatherData(gardenId, lat, lng);

        assertNull(result, "GardenWeather should be null when no garden exists.");
        // Check the external API is never called.
        verify(restTemplate, never()).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void GetWeatherData_GardenWeatherNull_WeatherFetchedFromAPI() throws JsonProcessingException {
        weatherAPIService = Mockito.spy(new WeatherAPIService(restTemplate, gardenService, gardenWeatherService));

        long gardenId = 1L;
        double lat = -43.54;
        double lng = 172.63;

        Garden garden = new Garden();
        garden.setGardenWeather(null);

        when(gardenService.getGardenById(gardenId)).thenReturn(Optional.of(garden));

        String jsonResponse = "{\"location\": {\"name\": \"Christchurch\"}, \"forecast\": {\"forecastday\": [{\"date\": \"2024-05-23\", \"day\": {\"maxtemp_c\": 10, \"mintemp_c\": 5, \"condition\": {\"text\": \"Sunny\"}}}]}}";
        WeatherAPIResponse forecastResponse = new WeatherAPIResponse();
        WeatherAPIResponse historyResponse = new WeatherAPIResponse();
        List<WeatherAPIResponse> historyResponses = new ArrayList<>();
        historyResponses.add(historyResponse);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok(jsonResponse));
        when(objectMapper.readValue(anyString(), eq(WeatherAPIResponse.class))).thenReturn(forecastResponse);
        when(objectMapper.readValue(anyString(), eq(WeatherAPIResponse.class))).thenReturn(historyResponse);
        doReturn(new GardenWeather()).when(weatherAPIService).saveWeather(anyDouble(), anyDouble(), any(Garden.class), any(WeatherAPIResponse.class), anyList());

        GardenWeather result = weatherAPIService.getWeatherData(gardenId, lat, lng);

        assertNotNull(result, "GardenWeather should be returned as not null.");
        // Check that the API is called 3 times, 1 for forecast, 2 for previous
        verify(restTemplate, times(3)).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void GetWeatherData_GardenWeatherExpired_WeatherFetchedFromAPI() throws JsonProcessingException {
        // Specific setup for the test requiring a spy
        weatherAPIService = Mockito.spy(new WeatherAPIService(restTemplate, gardenService, gardenWeatherService));

        long gardenId = 1L;
        double lat = -43.54;
        double lng = 172.63;

        Garden garden = new Garden();
        GardenWeather gardenWeather = new GardenWeather();
        gardenWeather.setLastUpdated(LocalDate.now().minusDays(1).toString());
        garden.setGardenWeather(gardenWeather);

        when(gardenService.getGardenById(gardenId)).thenReturn(Optional.of(garden));

        String jsonResponse = "{\"location\": {\"name\": \"Christchurch\"}, \"forecast\": {\"forecastday\": [{\"date\": \"2024-05-23\", \"day\": {\"maxtemp_c\": 10, \"mintemp_c\": 5, \"condition\": {\"text\": \"Sunny\"}}}]}}";
        WeatherAPIResponse forecastResponse = new WeatherAPIResponse();
        WeatherAPIResponse historyResponse = new WeatherAPIResponse();
        List<WeatherAPIResponse> historyResponses = new ArrayList<>();
        historyResponses.add(historyResponse);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok(jsonResponse));
        when(objectMapper.readValue(anyString(), eq(WeatherAPIResponse.class))).thenReturn(forecastResponse);
        when(objectMapper.readValue(anyString(), eq(WeatherAPIResponse.class))).thenReturn(historyResponse);
        doReturn(gardenWeather).when(weatherAPIService).saveWeather(anyDouble(), anyDouble(), any(Garden.class), any(WeatherAPIResponse.class), anyList());

        GardenWeather result = weatherAPIService.getWeatherData(gardenId, lat, lng);

        assertNotNull(result, "GardenWeather should be returned as not null.");
        // Check that the API is called 3 times, 1 for forecast, 2 for previous
        verify(restTemplate, times(3)).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void GetCurrentWeatherFromAPI_ValidResponseFlow_WeatherReturned() {
        double lat = -43.53;
        double lng = 172.63;
        String mockApiResponse = "{\"location\": {\"name\": \"Christchurch\"}, \"current\": {\"temp_c\": 15.0, \"condition\": {\"text\": \"Sunny\"}}}";

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok(mockApiResponse));
        WeatherAPICurrentResponse response = weatherAPIService.getCurrentWeatherFromAPI(lat, lng);

        assertNotNull(response);
        assertEquals("Christchurch", response.getLocation().getLocationName());
        assertEquals(15.0, response.getCurrent().getCurrentTemp());

        // API should only call once for current weather
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void GetCurrentWeatherFromAPI_InvalidLatLng_NoResponse() {
        double lat = 4444;
        double lng = 4444;

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok("No Response"));

        WeatherAPICurrentResponse response = weatherAPIService.getCurrentWeatherFromAPI(lat, lng);
        System.out.println(response);

        assertNotNull(response);
        // Check the contents is null
        assertNull(response.getLocation());

        // API should only call once for current weather
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void GetCurrentWeatherFromAPI_InvalidJsonReturn_JsonProcessingException() throws JsonProcessingException {
        double lat = -43.53;
        double lng = 172.63;
        String mockApiResponse = "oopsies";

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok(mockApiResponse));
        when(objectMapper.readValue(anyString(), eq(WeatherAPICurrentResponse.class))).thenThrow(new JsonProcessingException("Error processing JSON") {});

        WeatherAPICurrentResponse response = weatherAPIService.getCurrentWeatherFromAPI(lat, lng);

        assertNotNull(response);
        assertNull(response.getLocation());

        // API should only call once for current weather
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(String.class));
    }

    @ParameterizedTest
    @CsvSource({
            "400, Bad Request",
            "403, Forbidden",
            "500, Internal server error"
    })
    void RequestApi_BadLatAndLng_ReturnNoResponse(String errorCode, String errorMessage) {
        String url = "fakeurl";
        HttpClientErrorException badRequestException = HttpClientErrorException.create(HttpStatusCode.valueOf(Integer.parseInt(errorCode)) ,errorMessage , null, null, null);

        doThrow(badRequestException).when(restTemplate).getForEntity(anyString(), eq(String.class));
        String response = weatherAPIService.requestAPI(url);

        assertEquals("No Response", response);

        // API should only call once
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(String.class));
    }


    @Test
    void SaveWeather_ValidInput_WeatherSavedToGarden() {
        double lat = -43.53;
        double lng = 172.63;
        Garden garden = new Garden();
        garden.setId(1L);

        WeatherAPIResponse forecastResponse = new WeatherAPIResponse();
        WeatherAPIResponse historyResponse = new WeatherAPIResponse();

        // Make some fake parsed weather
        Location location = new Location();
        location.setLocationName("Christchurch");
        forecastResponse.setLocation(location);
        historyResponse.setLocation(location);

        Forecast forecast = new Forecast();
        ForecastDay forecastDay = new ForecastDay();
        Day day = new Day();
        Condition condition = new Condition();
        condition.setConditions("Sunny");
        condition.setIconUrl("//cdn.weatherapi.com/weather/64x64/day/116.png");
        day.setCondition(condition);
        day.setHumidity(10);
        day.setUv(10);
        day.setMaxTemp(15);
        day.setPrecipitation(10);
        day.setMinTemp(5);
        day.setWindSpeed(10);
        forecastDay.setDay(day);
        List<ForecastDay> forecastDays = new ArrayList<>();
        forecastDays.add(forecastDay);
        forecast.setForecastDays(forecastDays);
        forecastResponse.setForecast(forecast);
        WeatherAPIResponse historyDay = new WeatherAPIResponse();
        historyDay.setForecast(forecast);
        historyDay.setLocation(location);
        List<WeatherAPIResponse> historyResponses = List.of(historyDay);

        // Actual test
        GardenWeather mockGardenWeather = new GardenWeather();
        when(gardenWeatherService.addWeather(any(GardenWeather.class))).thenReturn(mockGardenWeather);

        GardenWeather gardenWeather = weatherAPIService.saveWeather(lat, lng, garden, forecastResponse, historyResponses);

        assertEquals(mockGardenWeather, gardenWeather);
    }
}
