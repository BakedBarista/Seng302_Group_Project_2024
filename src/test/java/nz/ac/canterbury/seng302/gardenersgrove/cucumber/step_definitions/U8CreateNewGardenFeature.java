package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class U8CreateNewGardenFeature {

    private GardenUser user;
    private static Authentication authentication;
    private static GardenRepository gardenRepository;
    private static GardenService gardenService;
    private static BindingResult bindingResult;
    private static Model model;
    private Garden garden;
    private static GardenUserService userService;
    private static GardenUserRepository userRepository;
    @BeforeAll
    public static void beforeAll() {
        userService = new GardenUserService(userRepository);
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        userRepository = mock(GardenUserRepository.class);

        gardenRepository = mock(GardenRepository.class);
        gardenService = new GardenService(gardenRepository);

        gardenController = new GardenController(gardenRepository, gardenService);
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

        when(userService.getUserById(1L)).thenReturn(user);
        GardenController.submitForm(garden, bindingResult, model);

    }

    @Then("A garden with that information is created")
    public void aGardenWithThatInformationIsCreated() {

    }
}
