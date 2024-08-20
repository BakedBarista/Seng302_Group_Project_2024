package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800MessageFeature {
    @Autowired
    private MessageController messageController;
    private static Model model;

    private String result;

    @Autowired
    private GardenUserService gardenUserService;

    @Autowired
    private FriendService friendService;

    private static Authentication authentication;

    private static FriendService mockedFriendService;
    private static GardenUser liam = new GardenUser();
    private GardenUser liamFriend;

    @BeforeAll
    public static void setup() {
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        mockedFriendService = mock(FriendService.class);
        liam.setId(1L);
    }

    @Given("I am viewing my friends list")
    public void i_am_viewing_my_friends_list() {
        messageController = new MessageController(gardenUserService, mockedFriendService);
    }

    @When("I click the {string} button next to one of my friends")
    public void i_click_the_message_button(String button) {
        Friends dummy = new Friends();

        Mockito.when(authentication.getPrincipal()).thenReturn(1L);
        Mockito.when(mockedFriendService.getFriendship(any(), any())).thenReturn(dummy);
        result = messageController.messageFriend(1L, authentication, model);
    }

    @Then("I am taken to the message page")
    public void i_am_taken_to_the_message_page() {
        assertEquals("users/message", result);
    }
}
