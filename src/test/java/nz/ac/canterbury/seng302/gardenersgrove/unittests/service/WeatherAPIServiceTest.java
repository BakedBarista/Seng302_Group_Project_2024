package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.PreviousWeather;
import nz.ac.canterbury.seng302.gardenersgrove.model.weather.Condition;
import nz.ac.canterbury.seng302.gardenersgrove.model.weather.Current;
import nz.ac.canterbury.seng302.gardenersgrove.model.weather.WeatherAPICurrentResponse;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.GardenWeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class WeatherAPIServiceTest {
    private WeatherAPIService weatherAPIService;

    @BeforeEach
    public void setUp() {
        weatherAPIService = new WeatherAPIService(Mockito.mock(RestTemplate.class), Mockito.mock(GardenService.class), Mockito.mock(GardenWeatherService.class));
    }

    @ParameterizedTest
    @CsvSource({
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
        "Clear, Clear, Clear",
        "Clear, Partly cloudy, Clear",
        "Sunny, Cloudy, Overcast",
        "Clear, Clear, Mist",
        "Clear, Clear, Fog"
    })
    void GetWateringRecommendation_NoPreviousOrCurrentRain_TrueReturned(String previousDay1, String previousDay2, String currentCondition) {
        GardenWeather gardenWeather = new GardenWeather();
        PreviousWeather day1 = new PreviousWeather();
        PreviousWeather day2 = new PreviousWeather();
        day1.setConditions(previousDay1);
        day2.setConditions(previousDay2);
        gardenWeather.setPreviousWeather(List.of(day1, day2));

        WeatherAPICurrentResponse currentResponse = new WeatherAPICurrentResponse();
        Current current = new Current();
        Condition condition = new Condition();
        condition.setConditions(currentCondition);
        current.setCondition(condition);
        currentResponse.setCurrent(current);

        assertTrue(weatherAPIService.getWateringRecommendation(gardenWeather, currentResponse));
    }

}
