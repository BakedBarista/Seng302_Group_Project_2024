package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.ForecastWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.PreviousWeather;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.weather.GardenWeatherRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.GardenWeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class U14WeatherMonitoringFeature {

    private GardenUser user;

    private static Authentication authentication;
    private static BindingResult bindingResult;
    private static Model model;
    private static GardenRepository gardenRepository;
    private static GardenService gardenService;
    private static GardenWeatherRepository gardenWeatherRepository;
    private static GardenWeatherService gardenWeatherService;
    private static WeatherAPIService weatherAPIService;
    private Garden garden;
    private static PlantRepository plantRepository;
    public static PlantService plantService;
    private static GardenUserService userService;
    private static GardenUserRepository gardenUserRepository;
    private static RestTemplate restTemplate;
    private static FriendService friendService;
    private static FriendsRepository friendsRepository;

    private static ModerationService moderationService;
    private static ModerationService mockedModerationService;
    private static ProfanityService profanityService;

    private static GardenController gardenController;
    private static Garden gardenMock;

    @BeforeAll
    public static void beforeAll() {
        bindingResult = mock(BindingResult.class);
        gardenMock = mock(Garden.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        friendsRepository = mock(FriendsRepository.class);
        gardenUserRepository = mock(GardenUserRepository.class);
        plantRepository = mock(PlantRepository.class);
        gardenRepository = mock(GardenRepository.class);
        gardenWeatherRepository = mock(GardenWeatherRepository.class);
        restTemplate = mock(RestTemplate.class);
        userService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        gardenWeatherService = new GardenWeatherService(gardenWeatherRepository);
        friendService = new FriendService(friendsRepository);
        plantService = new PlantService(plantRepository, gardenRepository);
        weatherAPIService = new WeatherAPIService(restTemplate, gardenService, gardenWeatherService);
        moderationService = new ModerationService();
        profanityService = mock(ProfanityService.class);
        gardenController = new GardenController(gardenService, plantService, userService, weatherAPIService, friendService, moderationService, profanityService);
    }


    @Given("I create a garden called {string} at {string} {string}, {string}, {string}, {string}. Latitude: {string}, Longitude: {string}")
    public void i_create_a_garden(String gardenName, String streetNumber, String streetName, String suburb, String city, String country, String lat, String lon) {
        user = new GardenUser();
        garden = new Garden();
        garden.setName(gardenName);
        garden.setStreetNumber(streetNumber);
        garden.setStreetName(streetName);
        garden.setSuburb(suburb);
        garden.setCity(city);
        garden.setCountry(country);
        Double doubleLat = Double.parseDouble(lat);
        garden.setLat(doubleLat);
        Double doubleLon = Double.parseDouble(lon);
        garden.setLat(doubleLon);

        when(authentication.getPrincipal()).thenReturn((Long) 1L);

        when(mockedModerationService.checkIfDescriptionIsFlagged("")).thenReturn(false);

        when(gardenUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gardenRepository.save(garden)).thenReturn(garden);

        gardenController.submitForm(garden, bindingResult, authentication, model);
    }

    @Then("The current weather is displaying for my location")
    public void the_current_weather_is_displaying_for_my_location() {
        verify(model).addAttribute(eq("displayWeather"), eq(true));
    }

    @Then("The weather for the next three days is displaying")
    public void the_weather_for_the_next_three_days_is_displaying() {
        assert(garden.getGardenWeather().getForecastWeather().size() == 3);
        verify(model).addAttribute(eq("forecastWeather"), anyList());
    }

    @Then("An error message displays saying that the location cannot be found.")
    public void error_message_displays_location_not_found() {
        garden.setLat(null);
        garden.setLon(null);
        when(gardenRepository.findById(garden.getId())).thenReturn(Optional.of(garden));
        gardenController.gardenDetail(garden.getId(), model);
        verify(model).addAttribute(eq("displayWeather"), eq(false));
    }

    @Given("The past two days have been sunny for that location")
    public void past_weather_sunny_for_that_location() {
        GardenWeather gardenWeather = new GardenWeather();
        gardenWeather.setPreviousWeather(List.of(new PreviousWeather(), new PreviousWeather()));
        garden.setGardenWeather(gardenWeather);
        when(gardenRepository.findById(garden.getId())).thenReturn(Optional.of(garden));
    }

    @Then("I get a notification telling me to water my plants")
    public void notification_to_water_plants() {
        verify(model).addAttribute(eq("wateringRecommendation"), eq(true));
    }

    @Given("The current weather is rainy")
    public void the_current_weather_is_rainy() {
        GardenWeather gardenWeather = new GardenWeather();
        garden.setGardenWeather(gardenWeather);
        when(gardenRepository.findById(garden.getId())).thenReturn(Optional.of(garden));
    }

    @Then("I get a notification telling me that outdoor plants do not need water that day")
    public void notification_that_outdoor_plants_do_not_need_water() {
        verify(model).addAttribute(eq("wateringRecommendation"), eq(false));
    }


    @When("I am on the garden details page for that garden")
    public void i_am_on_the_garden_details_page() {
        Assertions.assertNotNull(garden.getId(), "Garden ID should not be null before navigating to details page");
        when(gardenRepository.findById(garden.getId())).thenReturn(Optional.of(garden));
        gardenController.gardenDetail(garden.getId(), model);
    }
}
