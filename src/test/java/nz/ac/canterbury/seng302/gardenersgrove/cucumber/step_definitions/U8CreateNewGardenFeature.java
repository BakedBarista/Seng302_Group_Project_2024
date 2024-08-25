package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.GardenWeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class U8CreateNewGardenFeature {

    private GardenUser user;

    private static Authentication authentication;
    private static BindingResult bindingResult;
    private static Model model;
    private static GardenRepository gardenRepository;
    private static GardenService gardenService;
    private Garden garden;
    private static PlantRepository plantRepository;
    public static PlantService plantService;
    private static GardenUserService userService;
    private static GardenUserRepository gardenUserRepository;

    private static WeatherAPIService weatherAPIService;
    private static RestTemplate restTemplate;
    private static ObjectMapper objectMapper;

    private static LocationService locationService;
    private static FriendService friendService;
    private static FriendsRepository friendsRepository;

    private static ModerationService moderationService;
    private static ModerationService mockedModerationService;
    private static ProfanityService profanityService;

    private static TagService tagService;
    private static TagRepository   tagRepository;

    private static GardenController gardenController;
    private static GardenWeatherService gardenWeatherService;

    @BeforeAll
    public static void beforeAll() {
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        friendsRepository = mock(FriendsRepository.class);
        gardenUserRepository = mock(GardenUserRepository.class);
        plantRepository = mock(PlantRepository.class);
        gardenRepository = mock(GardenRepository.class);
        profanityService = mock(ProfanityService.class);
        tagRepository = mock(TagRepository.class);
        restTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        userService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        friendService = new FriendService(friendsRepository);
        plantService = new PlantService(plantRepository, gardenRepository,gardenUserRepository);
        tagService = new TagService(tagRepository, gardenService, profanityService);
        weatherAPIService = new WeatherAPIService(restTemplate, gardenService, gardenWeatherService);
        moderationService = new ModerationService(null, objectMapper, restTemplate);
        mockedModerationService = mock(ModerationService.class);
        locationService = mock(LocationService.class);
        gardenController = new GardenController(gardenService, null, plantService, userService, weatherAPIService, friendService, moderationService, profanityService, locationService);
    }


    @Given("I am on the create garden form")
    public void iAmOnTheCreateGardenForm() {
        user = U2LogInFeature.user;
        garden = new Garden();
    }

    @When("I enter valid name {string}")
    public void iEnterValidName(String name) {
        garden.setName(name);
    }

    @And("I enter a valid street number {string}")
    public void iEnterAValidStreetNumber(String streetNumber) {
        garden.setStreetNumber(streetNumber);
    }

    @And("I enter a valid street name {string}")
    public void iEnterAValidStreetName(String streetName) {
        garden.setStreetName(streetName);
    }

    @And("I enter a valid suburb {string}")
    public void iEnterAValidSuburb(String suburb) {
        garden.setSuburb(suburb);
    }

    @And("I enter a valid city {string}")
    public void iEnterAValidCity(String city) {
        garden.setCity(city);
    }

    @And("I enter a valid country {string}")
    public void iEnterAValidCountry(String country) {
        garden.setCountry(country);
    }

    @And("I enter a valid lat {string}")
    public void iEnterAValidLat(String lat) {
        Double doubleLat = Double.parseDouble(lat);
        garden.setLat(doubleLat);
    }

    @And("I enter a valid lon {string}")
    public void iEnterAValidLon(String lon) {
        Double doubleLon = Double.parseDouble(lon);
        garden.setLat(doubleLon);
    }

    @And("I submit create garden form")
    public void iSubmitCreateGardenForm() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("submissionToken")).thenReturn("mockToken123");
        when(authentication.getPrincipal()).thenReturn(1L);

        when(mockedModerationService.checkIfDescriptionIsFlagged("")).thenReturn(false);

        when(gardenUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gardenRepository.save(Mockito.any())).thenReturn(garden);
        when(locationService.getLatLng(anyString())).thenReturn(new ArrayList<>());

        gardenController.submitCreateGardenForm(new GardenDTO(garden), bindingResult, authentication, model, session);
    }

    @Then("A garden with that information is created")
    public void aGardenWithThatInformationIsCreated() {

        assertNotNull(gardenRepository.findByOwnerId(user.getId()));
        assertNotNull(gardenRepository.findById(garden.getId()));
    }
}
