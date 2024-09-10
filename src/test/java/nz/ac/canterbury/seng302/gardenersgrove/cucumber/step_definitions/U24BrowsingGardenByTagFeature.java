package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.BeforeAll;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.exceptions.ProfanityDetectedException;
import nz.ac.canterbury.seng302.gardenersgrove.service.ProfanityService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class U24BrowsingGardenByTagFeature {

    public static Garden testGarden1;
    public static Tag testTag1;

    public static GardenService gardenService;
    public static TagService tagService;
    public static TagService mockTagService;
    public static GardenRepository mockGardenRepository;
    public static GardenRepository gardenRepository;
    public static TagRepository tagRepository;
    public static ProfanityService profanityService;

    public static GardenUserRepository mockGardenUserRepository;

    public static Pageable pageable;
    public static Page<Garden> tester;
    public static ArrayList<String> searchItems;
    @BeforeAll
    public static void beforeAll() {

        System.out.println("Before all");
        profanityService = mock(ProfanityService.class);
        mockTagService = mock(TagService.class);
        tagRepository = mock(TagRepository.class);
        mockGardenRepository = mock(GardenRepository.class);
        mockGardenUserRepository = mock(GardenUserRepository.class);
        gardenService = new GardenService(mockGardenRepository, mockGardenUserRepository);
        tagService = new TagService(tagRepository, gardenService, profanityService);
        pageable = mock(Pageable.class);
    }

    @And("garden {string} has a tag {string}")
    public void gardenHasATag(String garden, String tag) throws ProfanityDetectedException {
        String streetNumber = "1";
        String streetName = "Test Street";
        String suburb = "Test Suburb";
        String city = "Test City";
        String country = "Test Country";
        String postCode = "1234";
        Double lon = 1.0;
        Double lat = 2.0;
        Double gardenSize = 100D;
        String gardenDescription = "Test Description";
        when(profanityService.badWordsFound(tag)).thenReturn(new ArrayList<>());
        when(mockTagService.isValidTag(any())).thenReturn(true);
        try{
            tagService.getOrCreateTag(tag);
        } catch(ProfanityDetectedException doNothing){
        }
        testTag1 = tagService.getTag(tag);
        testGarden1 = new Garden(garden, streetNumber,streetName,suburb,city,country,postCode,lon,lat, gardenDescription, gardenSize, null, null);
        gardenService.addGarden(testGarden1);
        testGarden1.getTags().add(testTag1);
        assertTrue(testGarden1.getTags().contains(testTag1));
    }


    @Given("I am on the search public garden tag form")
    public void iAmOnTheSearchPublicGardenTagForm() {
    }

    @When("I enter a tag to search {string}")
    public void iEnterATagToSearch(String tagSearch) {

        ArrayList<Garden> testGardens = new ArrayList<>();
        testGardens.add(testGarden1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Garden> page = new PageImpl<>(testGardens, pageable, testGardens.size());
        searchItems = new ArrayList<>();
        searchItems.add(tagSearch);
        when(mockGardenRepository.findGardensBySearchAndTags("", searchItems, pageable)).thenReturn(page);

        tester = gardenService.findGardensBySearchAndTags("", searchItems, pageable);
        assertEquals(10, tester.getSize());
    }

    @When("I enter a tag to search {string} and {string}")
    public void iEnterATagToSearchAnd(String tagSearch, String search) {

        ArrayList<Garden> testGardens = new ArrayList<>();
        testGardens.add(testGarden1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Garden> page = new PageImpl<>(testGardens, pageable, testGardens.size());
        searchItems = new ArrayList<>();
        searchItems.add(tagSearch);
        when(mockGardenRepository.findGardensBySearchAndTags(search, searchItems, pageable)).thenReturn(page);

        tester = gardenService.findGardensBySearchAndTags(search, searchItems, pageable);
        assertEquals(10, tester.getSize());
    }

    @And("I submit the public garden tag form")
    public void iSubmitThePublicGardenTagForm() {
    }

    @Then("garden {string} is shown")
    public void gardenIsShown(String gardenName) {
        List<Garden> gardensInPage = tester.getContent();
        assertTrue(gardensInPage.stream().anyMatch(garden -> garden.getName().equals(gardenName)));
    }


}
