package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.FavouritePlantsController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.PublicProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class U800_1_1_PublicProfileFeature {

    private static GardenUserRepository userRepository;
    private static GardenRepository gardenRepository;
    private static PlantRepository plantRepository;
    private static BindingResult bindingResult;
    private EditUserDTO editUserDTO;
    private static Model model;
    private static Authentication authentication;

    private static GardenUserService userService;
    private static ProfanityService profanityService;

    private static PlantService plantService;
    private static GardenService gardenService;
    private static GardenUserRepository gardenUserRepository;

    private static PublicProfileController publicProfileController;

    private static FavouritePlantsController favouritePlantsController;

    private static BirthFlowerService birthFlowerService;

    String invalidDescription;

    public static GardenUser user;
    String validDescription = "I love gardening!";
    Garden garden;

    public static Plant plant;

    private static final String birthFlower = "Carnation";

    MultipartFile profilePic = new MockMultipartFile(
            "image",
            "profile.png",
            "image/png",
            "profile picture content".getBytes()
    );

    MultipartFile banner = new MockMultipartFile(
            "bannerImage",
            "banner.png",
            "image/png",
            "banner content".getBytes()
    );

    MultipartFile newBanner = new MockMultipartFile(
            "bannerImage",
            "newBanner.png",
            "image/png",
            "banner content".getBytes()
    );

    MultipartFile invaildBanner;
    MultipartFile invalidProfile;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll() {
        userRepository = mock(GardenUserRepository.class);
        gardenRepository = mock(GardenRepository.class);
        bindingResult = mock(BindingResult.class);
        gardenUserRepository = mock(GardenUserRepository.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        objectMapper = new ObjectMapper();

        birthFlowerService = new BirthFlowerService(objectMapper);
        userService = new GardenUserService(userRepository, birthFlowerService);
        plantService = new PlantService(plantRepository, gardenRepository);
        gardenService = new GardenService(gardenRepository, gardenUserRepository);

        profanityService = new ProfanityService();
        publicProfileController = new PublicProfileController(userService, profanityService, plantService, birthFlowerService);
    }

     @Given("I am on my edit profile page")
     public void i_am_on_edit_profile_page() {
         user = U2LogInFeature.user;
         editUserDTO = new EditUserDTO();
     }
     @When("I enter a valid description")
     public void i_enter_a_valid_description() { user.setDescription(validDescription); }

     @When("I click {string}")
     public void i_submit_or_cancel_form(String formBtn) throws IOException {
         when(authentication.getPrincipal()).thenReturn((Long) 1L);
         when(userRepository.findById(1L)).thenReturn(Optional.of(user));

         if (formBtn.equals("Submit")) {
             publicProfileController.publicProfileEditSubmit(authentication, banner, profilePic, validDescription, birthFlower, editUserDTO, bindingResult, model);
         }
         else {
             publicProfileController.viewPublicProfile(authentication, model);
         }
     }

     @Then("the description is displayed on my profile page")
     public void the_description_is_displayed_on_my_profile_page() {
         assertEquals(user.getDescription(), validDescription);
     }

     @When("I enter an invalid description that is too long")
     public void i_enter_a_description_that_is_too_long() { invalidDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis ut purus sagittis turpis aliquet sagittis. Nulla accumsan purus massa, ut porttitor nisl laoreet vel. Duis efficitur commodo turpis vel luctus. Morbi facilisis dui vitae dignissim vehicula. Nam rutrum urna sit amet tellus facilisis blandit. Suspendisse bibendum sagittis odio. Nam quis porta diam, vel rhoncus eros. Sed at justo vel turpis faucibus fermentum quis ac eros. Etiam luctus eros a nulla gravida facilisis nec a ipsum. Aliquam malesuada porttitor.";
     }

     @Then("an error message tells me “Your description must be less than 512 characters”")
     public void an_error_message_for_description_appears() throws IOException {
         // Act
         when(authentication.getPrincipal()).thenReturn(1L);
         when(userRepository.findById(1L)).thenReturn(Optional.of(user));
         when(bindingResult.hasFieldErrors("description")).thenReturn(true);
         publicProfileController.publicProfileEditSubmit(authentication, banner, profilePic, invalidDescription, birthFlower, editUserDTO, bindingResult, model);

         // Assert
         verify(model).addAttribute("editUserDTO", editUserDTO);

     }
    @Given("I have a current cover photo")
    public void i_have_a_current_cover_photo() throws IOException {
        user.setProfileBanner(banner.getContentType(), banner.getBytes());
    }
     @When("I choose a new, valid cover photo")
     public void i_choose_a_new_valid_cover_photo() throws IOException {
         user.setProfileBanner(newBanner.getContentType(), newBanner.getBytes());
     }
     @Then("the photo displays on my profile")
     public void the_photo_displays_on_my_profile() throws IOException {
         assertEquals(user.getProfileBanner(), newBanner.getBytes());
     }


     @Then("the previous profile banner is overwritten and cannot be accessed anymore")
     public void previous_profile_banner_not_there() throws IOException {
         assertNotEquals(user.getProfileBanner(), banner.getBytes());
     }

    @When("I submit a file that is not either a png, jpg or svg")
    public void iSubmitAFileThatIsNotEitherAPngJpgOrSvg() {
        invaildBanner = new MockMultipartFile(
                "bannerImage",
                "newBanner.pdf",
                "image/pdf",
                "banner content".getBytes()
        );
    }


    @Then("the banner is not submitted")
    public void theBannerIsNotSubmitted() throws IOException {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bindingResult.hasFieldErrors("description")).thenReturn(false);
        publicProfileController.publicProfileEditSubmit(authentication, profilePic, invaildBanner, validDescription, birthFlower,editUserDTO, bindingResult, model);
        assertNull(user.getProfileBanner());
    }


    @Then("the profile picture is not submitted")
    public void theProfilePictureIsNotSubmitted() throws IOException {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bindingResult.hasFieldErrors("description")).thenReturn(false);
        publicProfileController.publicProfileEditSubmit(authentication, invalidProfile, banner, validDescription, birthFlower, editUserDTO, bindingResult, model);
        assertNull(user.getProfilePicture());
    }

    @When("I submit a valid file with a size thats to large")
    public void iSubmitAValidFileWithASizeThatsToLarge() {
        int size = 10 * 1024 * 1024 + 1;
        byte[] largeContent = new byte[size];
        invalidProfile = new MockMultipartFile(
                "bannerImage",
                "newBanner.pdf",
                "image/pdf",
                largeContent
        );
    }


    @Then("I can see an editable section called “My Favourite Garden” where I can showcase my favourite public garden of mine.")
    public void iCanSeeAnEditableSectionCalledMyFavouriteGardenWhereICanShowcaseMyFavouritePublicGardenOfMine() {
        Garden favourite = new Garden();
        user.setFavoriteGarden(favourite);
        assertEquals(user.getFavoriteGarden(), favourite);
    }

    @When("I select a garden from the list of public gardens")
    public void iSelectAGardenFromTheListOfPublicGardens() {
        List<Garden> publicGardens = new ArrayList<>();
        garden = new Garden();
        garden.setOwner(user);
        garden.setPublic(true);
        publicGardens.add(garden);
        user.setFavoriteGarden(publicGardens.get(0));
    }

    @Then("The garden is displayed")
    public void theGardenIsDisplayed() {
        assertEquals(garden, user.getFavoriteGarden());
    }

    @Given("I am on my edit profile page and I already have a favourite garden")
    public void iAmOnMyEditProfilePageAndIAlreadyHaveAFavouriteGarden() {
        if(user.getFavoriteGarden() == null) {
            user.setFavoriteGarden(new Garden());
        }
    }

    @Then("My favourite garden is updated")
    public void myFavouriteGardenIsUpdated() {
        assertEquals(user.getFavoriteGarden(), garden);
    }
}






