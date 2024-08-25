package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.GardenWeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class U19MakeGardenPublicFeature {
    private static RestTemplate restTemplate;
    private static FriendsRepository friendRepository;
    private static TagRepository tagRepository;
    private static PlantRepository plantRepository;
    private static GardenRepository gardenRepository;
    private static GardenUserRepository gardenUserRepository;

    private static GardenService gardenService;
    private static PlantService plantService;
    private static GardenUserService gardenUserService;
    private static WeatherAPIService weatherAPIService;
    private static FriendService friendService;
    private static ModerationService moderationService;
    private static ProfanityService profanityService;
    private static TagService tagService;
    private static LocationService locationService;
    private static ObjectMapper objectMapper;

    private static GardenUser gardenUser;
    private static GardenWeatherService gardenWeatherService;
    private static Garden garden;

    private static Model model;

    private static GardenController gardenController;

    @BeforeAll
    public static void beforeAll() {
        plantRepository = mock(PlantRepository.class);
        gardenRepository = mock(GardenRepository.class);
        restTemplate = mock(RestTemplate.class);
        friendRepository = mock(FriendsRepository.class);
        tagRepository = mock(TagRepository.class);
        tagService = new TagService(tagRepository, gardenService, profanityService);
        objectMapper = mock(ObjectMapper.class);

        friendService = new FriendService(friendRepository);
        gardenService = new GardenService(gardenRepository);
        plantService = new PlantService(plantRepository, gardenRepository,gardenUserRepository);
        gardenUserService = mock(GardenUserService.class);
        gardenWeatherService = mock(GardenWeatherService.class);
        weatherAPIService = new WeatherAPIService(restTemplate, gardenService, gardenWeatherService);
        moderationService = new ModerationService();
        profanityService = new ProfanityService();
        locationService = new LocationService(restTemplate, objectMapper);
        model = mock(Model.class);

        gardenController = new GardenController(gardenService, null, plantService, gardenUserService, weatherAPIService, friendService, moderationService, profanityService, locationService);
        gardenUser = new GardenUser();
        gardenUser.setId(1L);
        gardenUser.setFname("testUser");

        garden = new Garden("Test Garden", "1", "test", "test suburb", "test city", "test country", "1234", 0.0, 0.0, "test description", 100D);
        garden.setId(1L);
        garden.setOwner(gardenUser);

        when(gardenRepository.findById(1L)).thenReturn(Optional.of(garden));
        when(gardenRepository.save(any(Garden.class))).thenReturn(garden);
        when(gardenUserService.getCurrentUser()).thenReturn(gardenUser);
    }

    @Given("I have a garden")
    public void i_have_a_garden() {
        Garden savedGarden = gardenService.getGardenById(1L).orElseThrow(() -> new RuntimeException("Garden not found"));
        assert savedGarden.getName().equals("Test Garden");
    }

    @Given("A Garden Exists that is not mine")
    public void a_garden_exists_that_is_not_mine() {
        GardenUser anotherGardenUser = new GardenUser();
        anotherGardenUser.setId(2L);

        Garden garden = new Garden("Test Garden", "1", "test", "test suburb", "test city", "test country", "1234", 0.0, 0.0, "test description", 100D);
        garden.setId(2L);
        garden.setOwner(anotherGardenUser);
        when(gardenRepository.findById(2L)).thenReturn(Optional.of(garden));
    }

    @When("I try to edit the garden")
    public void i_try_to_edit_the_garden() {
        Garden savedGarden = gardenService.getGardenById(1L).orElseThrow(() -> new RuntimeException("Garden not found"));
        savedGarden.setName("New Garden Name");
        gardenService.addGarden(savedGarden);
    }

    @When("I make the garden public")
    public void i_make_the_garden_public() {
        Garden savedGarden = gardenService.getGardenById(1L).orElseThrow(() -> new RuntimeException("Garden not found"));
        savedGarden.setPublic(true);
        gardenService.addGarden(savedGarden);
    }

    @Then("The garden is public")
    public void the_garden_is_public() {
        Garden savedGarden = gardenService.getGardenById(1L).orElseThrow(() -> new RuntimeException("Garden not found"));
        assert savedGarden.getIsPublic();
    }

    @Then("The garden is not edited and I am shown the access denied page")
    public void the_garden_is_not_edited_and_i_am_shown_the_access_denied_page() {
        Garden savedGarden = gardenService.getGardenById(2L).orElseThrow(() -> new RuntimeException("Garden not found"));
        assert savedGarden.getName().equals("Test Garden");
        String returnPage = gardenController.getGarden(2L, model);
        assert returnPage.equals("error/accessDenied");
    }
}
