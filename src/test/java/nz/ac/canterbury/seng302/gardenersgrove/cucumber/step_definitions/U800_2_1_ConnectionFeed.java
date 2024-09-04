package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ApplicationController;
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

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.ACCEPTED;
import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.PENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800_2_1_ConnectionFeed {
    private static Authentication authentication;
    private static Model model;
    private static GardenUser userLiam;
    private static GardenUser userBen;
    private static GardenUser userImmy;
    private boolean confirmationMessageShown;
    private String userListJson;

    @Autowired
    private SuggestedUserController suggestedUserController;
    @Autowired
    private ApplicationController applicationController;
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


    @Given("I am on the homepage looking at the list of user profiles")
    @Given("I am logged in and I am on the homepage looking at the stack of user profiles")
    @Given("I am logged in and on the home page")
    public void iAmOnTheHomepageLookingAtTheListOfUserProfiles() {
        String result = suggestedUserController.home(authentication, model);

        assertEquals("home", result);
    }

    @When("I accept or decline a profile")
    public void iAcceptOrDeclineAProfile() {
        ResponseEntity<Map<String, Object>> result = applicationController.homeAccept("accept", userBen.getId(), authentication, model);

        assertFalse((Boolean) result.getBody().get("success"));
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("I am not shown that profile again")
    @Then("this card is removed from the stack")
    @Then("the next user profile is shown")
    public void iAmNotShownThatProfileAgain() {
        clearInvocations(model);

        String result = suggestedUserController.home(authentication, model);

        assertEquals("home", result);
        verify(model).addAttribute(eq("userList"), assertArg((String jsonUsers) -> {
            assertFalse(jsonUsers.contains(userBen.getFullName()));
        }));
    }

    @When("I click the red cross button on a person who I do not have a pending friend request from")
    public void i_click_the_red_cross_button_on_a_person_who_i_do_not_have_a_pending_friend_request_from() {
        ResponseEntity<Map<String, Object>> result = applicationController.homeAccept("decline", userBen.getId(), authentication, model);

        assertFalse((Boolean) result.getBody().get("success"));
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("no friend request is sent")
    public void no_friend_request_is_sent() {
        Friends request = friendsRepository.getAllFriendshipsBetweenUsers(userLiam.getId(), userBen.getId());
        assertNull(request);
    }

    @When("I click the red cross button on a person who I do have a pending friend request from")
    public void i_click_the_red_cross_button_on_a_person_who_i_do_have_a_pending_friend_request_from() {
        Friends request = new Friends(userBen, userLiam, PENDING);
        friendsRepository.save(request);

        ResponseEntity<Map<String, Object>> result = applicationController.homeAccept("decline", userBen.getId(), authentication, model);

        assertFalse((Boolean) result.getBody().get("success"));
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("their friend request is declined")
    public void their_friend_request_is_declined() {
        // TODO: Not yet implemented
        // Friends request = friendsRepository.getAllFriendshipsBetweenUsers(userLiam.getId(), userBen.getId());
        // assertNotNull(request);
        // assertEquals(DECLINED, request.getStatus());
    }

    @When("I click the green love heart button on a person who I have a pending friend request from")
    public void i_click_the_green_love_heart_button_on_a_person_who_i_have_a_pending_friend_request_from() {
        Friends request = new Friends(userBen, userLiam, PENDING);
        friendsRepository.save(request);

        ResponseEntity<Map<String, Object>> result = applicationController.homeAccept("accept", userBen.getId(), authentication, model);

        confirmationMessageShown = (Boolean) result.getBody().get("success");
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("the friend request is accepted")
    public void the_friend_request_is_accepted() {
        Friends request = friendsRepository.getAllFriendshipsBetweenUsers(userBen.getId(), userLiam.getId());
        assertNotNull(request);
        assertEquals(ACCEPTED, request.getStatus());
    }

    @Then("a confirmation message pops up")
    public void a_confirmation_message_pops_up() {
        assertTrue(confirmationMessageShown);
    }

    @When("I click the green love heart button on a person who I don’t have a pending friend request from")
    public void i_click_the_green_love_heart_button_on_a_person_who_i_don_t_have_a_pending_friend_request_from() {
        ResponseEntity<Map<String, Object>> result = applicationController.homeAccept("accept", userBen.getId(), authentication, model);

        assertFalse((Boolean) result.getBody().get("success"));
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
        String result = suggestedUserController.home(authentication, model);
        assertEquals("home", result);

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
}
