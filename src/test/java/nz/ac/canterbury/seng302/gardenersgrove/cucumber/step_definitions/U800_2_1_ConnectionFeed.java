package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Map;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800_2_1_ConnectionFeed {
    private static Authentication authentication;
    private static Model model;
    private static HttpServletRequest req;
    private static HttpServletResponse res;
    private static ServletContext context;
    private static GardenUser userLiam;
    private static GardenUser userBen;
    private static GardenUser userImmy;
    private Boolean confirmationMessageShown;
    private String userListJson;

    @Autowired
    private SuggestedUserController suggestedUserController;
    @Autowired
    private GardenUserRepository gardenUserRepository;
    @Autowired
    private FriendsRepository friendsRepository;

    @Before
    public void setup() {
        userLiam = new GardenUser("Liam", "Dough", "liam@friends.org", "password", null);
        userBen = new GardenUser("Ben", "Dough", "ben@friends.org", "password", null);
        userImmy = new GardenUser("Immy", "Dough", "immy@friends.org", "password", null);
        gardenUserRepository.save(userLiam);
        gardenUserRepository.save(userBen);
        gardenUserRepository.save(userImmy);

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userLiam.getId());
        model = mock(Model.class);
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        context = mock(ServletContext.class);
    }

    @After
    public void teardown() {
        for (Friends f : friendsRepository.findAll()) {
            friendsRepository.deleteById(f.getFriend_id());
        }

        gardenUserRepository.deleteById(userLiam.getId());
        gardenUserRepository.deleteById(userBen.getId());
        gardenUserRepository.deleteById(userImmy.getId());
    }

    @Given("I am logged in")
    public void i_am_logged_in() {
    }

    @Given("I am on the homepage looking at the list of user profiles")
    @Given("I am logged in and I am on the homepage looking at the stack of user profiles")
    @Given("I am logged in and on the home page")
    @Given("I am logged in and I am on the homepage looking at the top profile card on the stack")
    @When("I am on the homepage looking at the top profile card on the stack")
    public void iAmOnTheHomepageLookingAtTheListOfUserProfiles() {
        when(req.getServletContext()).thenReturn(context);

        String result = suggestedUserController.home(authentication, model, req, res);

        assertEquals("suggestedFriends", result);
    }

    @When("I accept or decline a profile")
    public void iAcceptOrDeclineAProfile() {
        ResponseEntity<Map<String, Object>> result = suggestedUserController.handleAcceptDecline("accept", userBen.getId(), authentication, model);

        assertNull(result.getBody().get("success"));
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("I am not shown that profile again")
    @Then("this card is removed from the stack")
    @Then("the next user profile is shown")
    public void iAmNotShownThatProfileAgain() {
        clearInvocations(model);

        String result = suggestedUserController.home(authentication, model, req, res);

        assertEquals("suggestedFriends", result);
        verify(model).addAttribute(eq("userList"), assertArg((String jsonUsers) -> {
            assertFalse(jsonUsers.contains(userBen.getFullName()));
        }));
    }

    @When("I click the decline button on a person who I do not have a pending friend request from")
    public void i_click_the_red_cross_button_on_a_person_who_i_do_not_have_a_pending_friend_request_from() {
        ResponseEntity<Map<String, Object>> result = suggestedUserController.handleAcceptDecline("decline", userBen.getId(), authentication, model);

        assertNull(result.getBody().get("success"));
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("no friend request is sent")
    public void no_friend_request_is_sent() {
        Friends request = friendsRepository.getFriendshipBetweenUsers(userLiam.getId(), userBen.getId());
        if (request == null) {
            assertNull(request);
        } else {
            assertEquals(Friends.Status.DECLINED, request.getStatus());
        }
    }

    @When("I click the decline button on a person who I do have a pending friend request from")
    public void i_click_the_red_cross_button_on_a_person_who_i_do_have_a_pending_friend_request_from() {
        Friends request = new Friends(userBen, userLiam, PENDING);
        friendsRepository.save(request);

        ResponseEntity<Map<String, Object>> result = suggestedUserController.handleAcceptDecline("decline", userBen.getId(), authentication, model);

        assertNull(result.getBody().get("success"));
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @When("I click the accept button on a person who I have a pending friend request from")
    public void i_click_the_green_love_heart_button_on_a_person_who_i_have_a_pending_friend_request_from() {
        Friends request = new Friends(userBen, userLiam, PENDING);
        friendsRepository.save(request);

        ResponseEntity<Map<String, Object>> result = suggestedUserController.handleAcceptDecline("accept", userBen.getId(), authentication, model);

        confirmationMessageShown = (Boolean) result.getBody().get("success");
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("the friend request is accepted")
    public void the_friend_request_is_accepted() {
        Friends request = friendsRepository.getAllFriendshipsBetweenUsers(userBen.getId(), userLiam.getId());
        assertNotNull(request);
        assertEquals(ACCEPTED, request.getStatus());
    }

    @Then("their friend request is declined")
    public void their_friend_request_is_declined() {
        Friends request = friendsRepository.getAllFriendshipsBetweenUsers(userBen.getId(), userLiam.getId());
        assertNotNull(request);
        assertEquals(DECLINED, request.getStatus());
    }

    @Then("a confirmation message pops up")
    public void a_confirmation_message_pops_up() {
        assertTrue(confirmationMessageShown);
    }

    @When("I click the accept button on a person who I don’t have a pending friend request from")
    public void i_click_the_green_love_heart_button_on_a_person_who_i_don_t_have_a_pending_friend_request_from() {
        ResponseEntity<Map<String, Object>> result = suggestedUserController.handleAcceptDecline("accept", userBen.getId(), authentication, model);

        assertNull(result.getBody().get("success"));
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("a friend request is sent to the user that was on the top of the stack")
    public void a_friend_request_is_sent_to_the_user_that_was_on_the_top_of_the_stack() {
        Friends request = friendsRepository.getAllFriendshipsBetweenUsers(userLiam.getId(), userBen.getId());
        assertNotNull(request);
        assertEquals(userBen.getId(), request.getReceiver().getId());
        assertEquals(PENDING, request.getStatus());
    }

    @When("I am viewing the potential new connections stack")
    public void i_am_viewing_the_potential_new_connections_stack() {
        Friends request = new Friends(userImmy, userLiam, PENDING);
        friendsRepository.save(request);

        clearInvocations(model);
        String result = suggestedUserController.home(authentication, model, req, res);
        assertEquals("suggestedFriends", result);

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(eq("userList"), jsonCaptor.capture());
        userListJson = jsonCaptor.getValue();
    }

    @Then("the stack will start with profiles of users who have sent me a friend request")
    public void the_stack_will_start_with_profiles_of_users_who_have_sent_me_a_friend_request() {
        int immyIndex = userListJson.indexOf(userImmy.getFullName());
        int benIndex = userListJson.indexOf(userBen.getFullName());
        assertTrue(immyIndex < benIndex);
    }

    @Then("initially the profile picture, name, and description are shown.")
    public void initially_the_profile_picture_name_and_description_are_shown() {
        clearInvocations(model);
        suggestedUserController.home(authentication, model, req, res);

        verify(model).addAttribute(eq("userId"), anyLong());
        verify(model).addAttribute(eq("name"), anyString());
        verify(model).addAttribute(eq("description"), any());
    }

    @When("there are no profiles to view")
    public void there_are_no_profiles_to_view() {
        for (GardenUser user : gardenUserRepository.findAll()) {
            if (user.getId().longValue() == userLiam.getId().longValue()) {
                continue;
            }

            Friends request = new Friends(userLiam, user, PENDING);
            friendsRepository.save(request);
        }

        clearInvocations(model);
        suggestedUserController.home(authentication, model, req, res);
    }

    @Then("I am shown a message saying “Could not find any connection suggestions”.")
    public void i_am_shown_a_message_saying_could_not_find_any_connection_suggestions() {
        verify(model, never()).addAttribute(eq("userId"), any());
        verify(model, never()).addAttribute(eq("name"), any());
        verify(model, never()).addAttribute(eq("description"), any());
        verify(model, never()).addAttribute(eq("userList"), any());
    }

    @When("I tap on the card")
    public void i_tap_on_the_card() {
        // This is done purely in JS, so cannot be tested here
    }

    @Then("the card will flip over and the Best Plants will be displayed")
    public void the_card_will_flip_over_and_the_best_plants_will_be_displayed() {
        verify(model).addAttribute(eq("userList"), assertArg((String userListJson) -> {
            assertTrue(userListJson.contains("favouritePlants"));
        }));
    }

    @Then("Favourite Garden will be displayed")
    public void favourite_garden_will_be_displayed() {
        verify(model).addAttribute(eq("userList"), assertArg((String userListJson) -> {
            assertTrue(userListJson.contains("favouriteGarden"));
        }));
    }

    @Then("I am shown potential new connections who are not my friends as a swipeable stack of profile cards.")
    public void i_am_shown_potential_new_connections_who_are_not_my_friends_as_a_swipeable_stack_of_profile_cards() {
        verify(model).addAttribute(eq("userList"), assertArg((String userListJson) -> {
            assertTrue(userListJson.startsWith("[{\"id\":"));
        }));
    }
}
