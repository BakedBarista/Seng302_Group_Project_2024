package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import static org.mockito.Mockito.*;

public class U19PubliciseGardenFeature {
    private GardenController gardenController;
    private ProfanityService profanityService;
    private ModerationService moderationService;
    private FriendService friendService;
    private WeatherAPIService weatherAPIService;
    private PlantService plantService;
    private static GardenService gardenService;
    private static GardenUserService gardenUserService;
    private static GardenUserRepository gardenUserRepository;
    private static GardenRepository gardenRepository;
    private GardenUser gardenUser;
    private static Model model;
    private static BindingResult bindingResult;
    private Garden garden;
    private static Authentication authentication;

    @BeforeAll
    public static void beforeAll() {
        gardenUserRepository = mock(GardenUserRepository.class);
        gardenRepository = mock(GardenRepository.class);
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        gardenUserService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
    }

    @Given("I enter a new description")
    public void i_enter_a_new_description() {
        profanityService = mock(ProfanityService.class);
        moderationService = mock(ModerationService.class);
        gardenController = new GardenController(gardenService,plantService,gardenUserService,weatherAPIService,friendService,moderationService,profanityService);
        garden = new Garden();
    }

    @When("Description contains profanity")
    public void description_contains_profanity() {
        ArrayList<String> profanity = new ArrayList<>();
        profanity.add("badword");
        garden.setDescription("badword");
        when(profanityService.badWordsFound("badword")).thenReturn(profanity);
        when(moderationService.checkIfDescriptionIsFlagged("badword")).thenReturn(false);

        gardenController.checkGardenError(model, bindingResult, garden);
    }

    @Then("Error message {string} is shown")
    public void error_message_is_shown(String expectedMessage) {
        verify(model).addAttribute("profanity", expectedMessage);
    }

    @When("Description not contain profanity")
    public void description_does_not_contain_profanity() {
        garden.setDescription("good description");
        when(profanityService.badWordsFound("good description")).thenReturn(new ArrayList<>());
        when(moderationService.checkIfDescriptionIsFlagged("good description")).thenReturn(false);

        gardenController.checkGardenError(model, bindingResult, garden);
    }

    @Then("Description is added")
    public void description_is_added() {
        gardenService.addGarden(garden);
        Assertions.assertEquals(garden.getDescription(), "good description");
    }

    @When("Description contains both profanity and good words")
    public void description_contains_both_profanity_and_good_words() {
        ArrayList<String> profanity = new ArrayList<>();
        profanity.add("badword");
        garden.setDescription("good badword");
        when(profanityService.badWordsFound("good badword")).thenReturn(profanity);
        when(moderationService.checkIfDescriptionIsFlagged("good badword")).thenReturn(false);

        verify(model,times(1)).addAttribute("profanity", "The description does not match the language standards of the app.");
    }
}
