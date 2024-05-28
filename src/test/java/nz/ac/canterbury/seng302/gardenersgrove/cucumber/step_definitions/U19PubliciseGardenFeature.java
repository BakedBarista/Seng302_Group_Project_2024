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
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import static org.mockito.Mockito.*;

public class U19PubliciseGardenFeature {
    private GardenController gardenController;
    private static ProfanityService profanityService;
    private static ModerationService moderationService;
    private FriendService friendService;
    private WeatherAPIService weatherAPIService;
    private PlantService plantService;
    private static GardenService gardenService;
    private static GardenUserService gardenUserService;
    private static GardenUserRepository gardenUserRepository;
    private static GardenRepository gardenRepository;
    private static TagService tagService;
    private static TagRepository tagRepository;
    private GardenUser gardenUser;
    private static Model model;
    private static BindingResult bindingResult;
    private Garden garden;
    private static Authentication authentication;
    private String errorMessage;


    @BeforeAll
    public static void beforeAll() {
        gardenUserRepository = mock(GardenUserRepository.class);
        gardenRepository = mock(GardenRepository.class);
        profanityService = mock(ProfanityService.class);
        moderationService = mock(ModerationService.class);
        tagRepository = mock(TagRepository.class);
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        gardenUserService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        tagService = new TagService(tagRepository, gardenService);
    }

    @Given("I enter a new description {string}")
    public void i_enter_a_new_description(String description) {
        model = mock(Model.class);
        garden = new Garden();
        garden.setDescription(description);
        bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        gardenService = mock(GardenService.class);
        gardenController = new GardenController(gardenService,plantService,gardenUserService,weatherAPIService,tagService, friendService,moderationService,profanityService);
    }

    @When("Description contains profanity")
    public void description_contains_profanity() {
        ArrayList<String> profanity = new ArrayList<>();
        profanity.add(garden.getDescription());
        when(profanityService.badWordsFound(garden.getDescription())).thenReturn(profanity);
        when(moderationService.checkIfDescriptionIsFlagged(garden.getDescription())).thenReturn(false);

        gardenController.checkGardenError(model, bindingResult, garden);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(eq("profanity"), stringCaptor.capture());
        errorMessage = stringCaptor.getValue();
    }

    @When("Description not contain profanity")
    public void description_not_contain_profanity() {
        when(profanityService.badWordsFound(garden.getDescription())).thenReturn(new ArrayList<>());
        when(moderationService.checkIfDescriptionIsFlagged(garden.getDescription())).thenReturn(false);

        gardenController.checkGardenError(model, bindingResult, garden);
    }

    @Then("Description is added")
    public void description_is_added() {
        verify(model, never()).addAttribute(eq("profanity"), anyString());
        verify(model, never()).addAttribute(eq("locationError"), anyString());
    }

    @When("Description contains both profanity and good words")
    public void description_contains_both_profanity_and_good_words() {
        ArrayList<String> profanity = new ArrayList<>();
        profanity.add("shit");
        when(profanityService.badWordsFound(garden.getDescription())).thenReturn(profanity);
        when(moderationService.checkIfDescriptionIsFlagged(garden.getDescription())).thenReturn(false);

        gardenController.checkGardenError(model, bindingResult, garden);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(eq("profanity"), stringCaptor.capture());
        errorMessage = stringCaptor.getValue();
    }

    @Then("Error message {string} is shown")
    public void error_message_is_shown(String expectedMessage) {
        Assertions.assertEquals(expectedMessage, errorMessage);
    }

}
