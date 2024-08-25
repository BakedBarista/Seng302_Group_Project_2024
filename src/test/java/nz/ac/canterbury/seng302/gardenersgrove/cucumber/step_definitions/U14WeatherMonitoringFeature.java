package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.WeatherData;
import nz.ac.canterbury.seng302.gardenersgrove.model.weather.*;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.weather.GardenWeatherRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.GardenWeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class U14WeatherMonitoringFeature {
    private static Authentication authentication;
    private static BindingResult bindingResult;
    private static Model model;
    private static GardenRepository gardenRepository;
    private static GardenService gardenService;
    private static WeatherAPIService weatherAPIService;
    private Garden garden;
    public static PlantService plantService;
    private static GardenUserRepository gardenUserRepository;
    private static RestTemplate restTemplate;
    private static ModerationService mockedModerationService;
    private static GardenController gardenController;
    private static GardenWeatherService gardenWeatherService;
    private GardenWeather gardenWeather;
    private WeatherAPICurrentResponse currentResponse;
    private double lat = -43.521369;
    private double lon = 172.58492;

    @BeforeAll
    public static void beforeAll() {
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        FriendsRepository friendsRepository = mock(FriendsRepository.class);
        gardenUserRepository = mock(GardenUserRepository.class);
        PlantRepository plantRepository = mock(PlantRepository.class);
        gardenRepository = mock(GardenRepository.class);
        GardenWeatherRepository gardenWeatherRepository = mock(GardenWeatherRepository.class);
        restTemplate = mock(RestTemplate.class);
        GardenUserService userService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        gardenWeatherService = new GardenWeatherService(gardenWeatherRepository);
        FriendService friendService = new FriendService(friendsRepository);
        plantService = new PlantService(plantRepository, gardenRepository,gardenUserRepository);
        weatherAPIService = new WeatherAPIService(restTemplate, gardenService, gardenWeatherService);
        mockedModerationService = mock(ModerationService.class);
        ProfanityService profanityService = mock(ProfanityService.class);
        TagService tagService = mock(TagService.class);
        LocationService locationService = mock(LocationService.class);
        gardenController = new GardenController(gardenService, null, plantService, userService, weatherAPIService, friendService, mockedModerationService, profanityService, locationService);

        GardenUser user = new GardenUser();
        user.setId(1L);
        Garden garden = new Garden();
        garden.setId(1L);
        garden.setName("Test Garden");
        garden.setOwner(user);
        garden.setLat(43.5321);
        garden.setLon(172.6362);

        when(gardenUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gardenRepository.findById(1L)).thenReturn(Optional.of(garden));
    }

    @Given("I create a garden called {string} at {string} {string}, {string}, {string}, {string}. Latitude: {string}, Longitude: {string}")
    public void i_create_a_garden(String gardenName, String streetNumber, String streetName, String suburb, String city, String country, String lat, String lon) {
        GardenUser user = gardenUserRepository.findById(1L).get();
        garden = gardenRepository.findById(1L).get();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("submissionToken")).thenReturn("mockToken123");
        garden.setName(gardenName);
        garden.setStreetNumber(streetNumber);
        garden.setStreetName(streetName);
        garden.setSuburb(suburb);
        garden.setCity(city);
        garden.setCountry(country);
        garden.setLat(Double.parseDouble(lat));
        garden.setLon(Double.parseDouble(lon));

        when(authentication.getPrincipal()).thenReturn(1L);

        when(mockedModerationService.checkIfDescriptionIsFlagged("")).thenReturn(false);
        when(gardenRepository.save(garden)).thenReturn(garden);

        gardenController.submitCreateGardenForm(new GardenDTO(garden), bindingResult, authentication, model,session);
        assertNotNull(gardenRepository.findByOwnerId(user.getId()));
        assertNotNull(gardenRepository.findById(garden.getId()));
    }

    @When("I am on the garden details page for that garden")
    public void i_am_on_the_garden_details_page() {
        garden = gardenRepository.findById(1L).get();
        Assertions.assertNotNull(garden.getId(), "Garden ID should not be null before navigating to details page");
    }

    @Then("The current weather is displaying for my location")
    public void the_current_weather_is_displaying_for_my_location() {
        GardenWeather gardenWeather = new GardenWeather();
        gardenWeather.setLastUpdated(LocalDate.now().toString());
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
        WeatherData data = new WeatherData();
        data.setHumidity(10);
        data.setUv(10);
        data.setMaxTemp(15);
        data.setPrecipitation(10);
        data.setMinTemp(5);
        data.setWindSpeed(10);
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
        List<WeatherData> weatherData = new ArrayList<>();
        weatherData.add(data);
        weatherData.add(data);
        weatherData.add(data);
        GardenWeather mockGardenWeather = new GardenWeather();
        when(gardenWeatherService.addWeather(any(GardenWeather.class))).thenReturn(mockGardenWeather);

        gardenWeather = weatherAPIService.saveWeather(lat, lon, garden, forecastResponse, historyResponses);
        garden.setGardenWeather(gardenWeather);
        garden.getGardenWeather().setForecastWeather(weatherData);

        assertEquals(mockGardenWeather, gardenWeather);
    }

    @Then("The weather for the next three days is displaying")
    public void the_weather_for_the_next_three_days_is_displaying() {
        assertNotNull(garden.getGardenWeather());
        assertEquals(3, garden.getGardenWeather().getForecastWeather().size());
    }

    @Given("The past two days have been sunny for that location")
    public void past_weather_sunny_for_that_location() {
        gardenWeather = new GardenWeather();
        WeatherData day1 = new WeatherData();
        WeatherData day2 = new WeatherData();
        day1.setConditions("Sunny");
        day2.setConditions("Sunny");
        gardenWeather.setPreviousWeather(List.of(day1, day2));

        currentResponse = new WeatherAPICurrentResponse();
        Current current = new Current();
        Condition condition = new Condition();
        condition.setConditions("Sunny");
        current.setCondition(condition);
        currentResponse.setCurrent(current);

        assertTrue(weatherAPIService.getWateringRecommendation(gardenWeather, currentResponse));
    }

    @Then("I get a notification telling me to water my plants")
    public void notification_to_water_plants() {
        assertTrue(weatherAPIService.getWateringRecommendation(gardenWeather, currentResponse));
    }

    @Given("The current weather is {string}")
    public void the_current_weather_is_rainy(String conditions) {
        gardenWeather = new GardenWeather();
        currentResponse = new WeatherAPICurrentResponse();
        Current current = new Current();
        Condition condition = new Condition();
        condition.setConditions(conditions);
        current.setCondition(condition);
        currentResponse.setCurrent(current);
    }

    @Then("I get a notification telling me that outdoor plants do not need water that day")
    public void notification_that_outdoor_plants_do_not_need_water() {
        assertFalse(weatherAPIService.getWateringRecommendation(gardenWeather, currentResponse));
    }
}