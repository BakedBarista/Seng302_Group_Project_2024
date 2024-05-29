package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

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
    private static  RestTemplate restTemplate;

    private static LocationService locationService;
    private static FriendService friendService;
    private static FriendsRepository friendsRepository;

    private static ModerationService moderationService;
    private static ModerationService mockedModerationService;
    private static ProfanityService profanityService;

    private static TagService tagService;
    private static TagRepository   tagRepository;

    private static GardenController gardenController;

    private static Garden gardenMock;
    @BeforeAll
    public static void beforeAll() {
        System.out.println("test");
        bindingResult = mock(BindingResult.class);
        gardenMock = mock(Garden.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        friendsRepository = mock(FriendsRepository.class);
        gardenUserRepository = mock(GardenUserRepository.class);
        plantRepository = mock(PlantRepository.class);
        gardenRepository = mock(GardenRepository.class);
        profanityService = mock(ProfanityService.class);
        tagRepository = mock(TagRepository.class);
        restTemplate = mock(RestTemplate.class);
        userService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        friendService = new FriendService(friendsRepository);
        plantService = new PlantService(plantRepository, gardenRepository);
        weatherAPIService = new WeatherAPIService(restTemplate, gardenService);
        tagService = new TagService(tagRepository, gardenService, profanityService);
        moderationService = new ModerationService();
        mockedModerationService = mock(ModerationService.class);
        locationService = mock(LocationService.class);
        gardenController = new GardenController(gardenService, plantService, userService, weatherAPIService, tagService, friendService, moderationService, profanityService, locationService);
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
        when(authentication.getPrincipal()).thenReturn((Long) 1L);

        when(mockedModerationService.checkIfDescriptionIsFlagged("")).thenReturn(false);

        when(gardenUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gardenRepository.save(garden)).thenReturn(garden);
        when(locationService.getLatLng(anyString())).thenReturn(new ArrayList<>());

        gardenController.submitForm(garden, bindingResult, authentication, model);
    }

    @Then("A garden with that information is created")
    public void aGardenWithThatInformationIsCreated() {

        assertNotNull(gardenRepository.findByOwnerId(user.getId()));
        assertNotNull(gardenRepository.findById(garden.getId()));
    }
}
