package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
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
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class U8CreateNewGardenFeature {

    private GardenUser user;
    private static SecurityContextHolder securityContextHolder;
    private static SecurityContext securityContext;

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
    private static FriendService friendService;
    private static FriendsRepository friendsRepository;

    private static ModerationService moderationService;

    private static GardenController gardenController;

    @BeforeAll
    public static void beforeAll() {

        //chat gpt code 2 lines bellow
        securityContext = mock(SecurityContext.class);


        securityContextHolder = mock(SecurityContextHolder.class);
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        friendsRepository = mock(FriendsRepository.class);
        gardenUserRepository = mock(GardenUserRepository.class);
        plantRepository = mock(PlantRepository.class);
        gardenRepository = mock(GardenRepository.class);
        userService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        friendService = new FriendService(friendsRepository);
        plantService = new PlantService(plantRepository, gardenRepository);
        weatherAPIService = new WeatherAPIService(restTemplate, gardenService);
        moderationService = new ModerationService();
        gardenController = new GardenController(gardenService, plantService, userService, weatherAPIService, friendService, moderationService);
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

    @And("I submit create garden form")
    public void iSubmitCreateGardenForm() {
        Mockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.getCurrentUser()).thenReturn(user);
        when(gardenUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gardenRepository.save(garden)).thenReturn(garden);

        gardenController.submitForm(garden, bindingResult, model);
    }

    @Then("A garden with that information is created")
    public void aGardenWithThatInformationIsCreated() {
        assertNotNull(gardenRepository.findByOwnerId(user.getId()));
    }
}
