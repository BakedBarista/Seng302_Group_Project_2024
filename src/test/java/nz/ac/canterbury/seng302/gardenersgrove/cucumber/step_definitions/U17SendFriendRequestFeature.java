package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;
import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.PENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


import io.cucumber.java.en.And;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ManageFriendsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.BirthFlowerService;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;





import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.BeforeAll;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class U17SendFriendRequestFeature {
    private static ManageFriendsController friendsController;
    private static FriendsRepository friendsRepository;
    private static GardenUserRepository gardenUserRepository;
    private static BirthFlowerService birthFlowerService;
    private static GardenUserService userService;

    private static MessageService messageService;
    private static  FriendService friendService;
    private GardenUser user;
    private static Long loggedInUserId;
    private static GardenUser friend = new GardenUser();
    private static Authentication authentication;

    private RedirectAttributes rm = new RedirectAttributesModelMap();

    private List<GardenUser> searchResults;



    @BeforeAll
    public static void beforeAll() {
        System.out.println("Before all");
        gardenUserRepository = mock(GardenUserRepository.class);
        friendsRepository = mock(FriendsRepository.class);
        birthFlowerService = new BirthFlowerService(new ObjectMapper());
        userService = new GardenUserService(gardenUserRepository, birthFlowerService);
        friendService = new FriendService(friendsRepository);
        friendsController = new ManageFriendsController(friendService, userService,messageService);
        authentication = mock(Authentication.class);

        loggedInUserId = 1L;

        friend.setId(loggedInUserId);
        friend.setEmail("logged.in@gmail.com");
        friend.setFname("John");
        friend.setLname("Doe");
    }

    @Given("I am on the manage friends page")
    public void i_am_on_the_manage_friends_page() {
        user = U2LogInFeature.user;
    }
    @When("I search {string}")
    public void i_search(String searchName) {
        rm = new RedirectAttributesModelMap();
        searchResults = List.of(friend);
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(userService.getUserBySearch(searchName, loggedInUserId)).thenReturn(searchResults);
        System.out.println(searchResults);

        String result = friendsController.manageFriendsSearch(authentication, searchName, rm);
        System.out.println(result);

    }
    @Then("I can see {string} on the search list")
    public void i_can_see_on_the_search_list(String searchName) {
        assertEquals(searchResults, rm.getFlashAttributes().get("searchResults"));

        List<GardenUser> actualResults = (List<GardenUser>) rm.getFlashAttributes().get("searchResults");
        assertTrue(actualResults.stream().anyMatch(user -> user.getFullName().equals(searchName)));

    }
    @Then("I don't see {string} on the search list")
    public void i_don_t_see_on_the_search_list(String searchName) {
        assertEquals(searchResults, rm.getFlashAttributes().get("searchResults"));

        List<GardenUser> actualResults = (List<GardenUser>) rm.getFlashAttributes().get("searchResults");
        assertFalse(actualResults.stream().anyMatch(user -> user.getFullName().equals(searchName)));
    }


}
