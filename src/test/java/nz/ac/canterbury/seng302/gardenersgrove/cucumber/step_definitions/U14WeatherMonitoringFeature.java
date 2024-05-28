package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.weather.GardenWeatherRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.GardenWeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class U14WeatherMonitoringFeature {

    private GardenUser user;

    private static Authentication authentication;
    private static BindingResult bindingResult;
    private static Model model;
    private static GardenRepository gardenRepository;
    private static GardenService gardenService;
    private static GardenWeatherRepository gardenWeatherRepository;
    private static GardenWeatherService gardenWeatherService;
    private Garden garden;
    private static PlantRepository plantRepository;
    public static PlantService plantService;
    private static GardenUserService userService;
    private static GardenUserRepository gardenUserRepository;
    private static WeatherAPIService weatherAPIService;
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
        profanityService = mock(ProfanityService.class);
        userService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        gardenWeatherService = new GardenWeatherService(gardenWeatherRepository);
        friendService = new FriendService(friendsRepository);
        plantService = new PlantService(plantRepository, gardenRepository);
        weatherAPIService = new WeatherAPIService(restTemplate, gardenService, gardenWeatherService);
        moderationService = new ModerationService();
        mockedModerationService = mock(ModerationService.class);
        gardenController = new GardenController(gardenService, plantService, userService, weatherAPIService, friendService, moderationService, profanityService);
    }

    @Given("I create a garden called {string} in {string}, {string}")
    public void i_create_a_garden(String gardenName, String city, String country) {
        user = U2LogInFeature.user;
        garden = new Garden();
        garden.setName(gardenName);
        garden.setCity(city);
        garden.setCountry(country);

        when(authentication.getPrincipal()).thenReturn((Long) 1L);

        when(mockedModerationService.checkIfDescriptionIsFlagged("")).thenReturn(false);

        when(gardenUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gardenRepository.save(garden)).thenReturn(garden);

        gardenController.submitForm(garden, bindingResult, authentication, model);
    }

    @When("I am on the garden details page for {string}")
    public void i_am_on_the_garden_details_page(String gardenName) {
        gardenRepository.findById(garden.getId());
    }

    @Then("The current weather is displaying for my location")
    public void the_current_weather_is_displaying_for_my_location() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The weather for the next three days is displaying")
    public void the_weather_for_the_next_three_days_is_displaying() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("An error message displays saying that the location cannot be found.")
    public void error_message_displays_location_not_found() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("The past two days have been sunny for that location")
    public void past_weather_sunny_for_that_location() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I get a notification telling me to water my plants")
    public void notification_to_water_plants() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("The current weather is rainy")
    public void the_current_weather_is_rainy() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I get a notification telling me that outdoor plants do not need water that day")
    public void notification_that_outdoor_plants_do_not_need_water() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
