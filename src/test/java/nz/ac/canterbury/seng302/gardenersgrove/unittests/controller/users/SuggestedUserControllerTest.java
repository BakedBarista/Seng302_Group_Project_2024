package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestedUserControllerTest {

    private static SuggestedUserController suggestedUserController;
    private static GardenUserService gardenUserService;
    private static Model model;
    private static Authentication authentication;

    private static SuggestedUserService suggestedUserService;
    private static Long loggedInUserId = 1L;
    private static Long suggestedUserId = 2L;
    private static GardenUser loggedInUser;
    private static GardenUser suggestedUser;
    private static Friends friendRequestReceive;
    private static Friends friendRequestSend;

    @BeforeAll
    static void setup() {
        gardenUserService = Mockito.mock(GardenUserService.class);
        authentication = Mockito.mock(Authentication.class);
        suggestedUserService = Mockito.mock(SuggestedUserService.class);
        suggestedUserController = new SuggestedUserController(gardenUserService, suggestedUserService);
        loggedInUser = new GardenUser();
        loggedInUser.setId(loggedInUserId);
        loggedInUser.setEmail("logged.in@gmail.com");
        loggedInUser.setFname("Current");
        loggedInUser.setLname("User");
        loggedInUser.setDescription("This is a description");
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);

        suggestedUser = new GardenUser();
        suggestedUser.setId(suggestedUserId);
        //sender-receiver-status
        friendRequestReceive = new Friends(suggestedUser, loggedInUser, Friends.Status.PENDING);
        friendRequestSend = new Friends(loggedInUser, suggestedUser, Friends.Status.PENDING);

        when(authentication.getPrincipal()).thenReturn(loggedInUser.getId());
        when(gardenUserService.getUserById(loggedInUser.getId())).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(suggestedUser.getId())).thenReturn(suggestedUser);

    }

    /**
     * Testing the get method of the home page
     * HARD-CODED Test!!!!!
     */
    @Test
    void whenIViewMyPublicProfile_thenIAmTakenToThePublicProfilePage() {
        model = Mockito.mock(Model.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        Mockito.when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);

        String page = suggestedUserController.home(authentication, model);

        Mockito.verify(model).addAttribute("name", "Max Doe");
        Mockito.verify(model).addAttribute("description", "I am here to meet some handsome young men who love gardening as much as I do! My passion is growing carrots and eggplants. In my spare time, I like to thrift, ice skate and hang out with my kid, Liana. She's three, and the love of my life. The baby daddy is my former sugar daddy, John Doe. He died of a heart attack on his yacht in Italy last summer");

        Assertions.assertEquals("suggestedFriends", page);
    }

    @Test
    void testHandleAcceptDecline_ValidationFails_DoNothing() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(suggestedUserService.validationCheck(loggedInUserId, suggestedUserId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.handleAcceptDecline("accept", suggestedUserId, authentication, model);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse((Boolean) response.getBody().get("success"));
    }

    @Test
    void testHandleAcceptDecline_PendingRequestExists_PendingRequestAccepted() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(suggestedUserId)).thenReturn(suggestedUser);
        when(suggestedUserService.validationCheck(loggedInUserId, suggestedUserId)).thenReturn(true);
        when(suggestedUserService.attemptToAcceptPendingRequest(loggedInUserId, suggestedUserId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.handleAcceptDecline("accept", suggestedUserId, authentication, model);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    void testHandleAcceptDecline_NoFriendshipExists_NewRequestSent() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(suggestedUserId)).thenReturn(suggestedUser);
        when(suggestedUserService.validationCheck(loggedInUserId, suggestedUserId)).thenReturn(true);
        when(suggestedUserService.attemptToAcceptPendingRequest(loggedInUserId, suggestedUserId)).thenReturn(false);
        when(suggestedUserService.sendNewPendingRequest(loggedInUser, suggestedUser)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.handleAcceptDecline("accept", suggestedUserId, authentication, model);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    void testHandleAcceptDecline_PendingRequestExists_PendingRequestDeclined() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(suggestedUserId)).thenReturn(suggestedUser);
        when(suggestedUserService.validationCheck(loggedInUserId, suggestedUserId)).thenReturn(true);
        when(suggestedUserService.attemptToDeclinePendingRequest(loggedInUserId, suggestedUserId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.handleAcceptDecline("decline", suggestedUserId, authentication, model);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    void testHandleAcceptDecline_NoFriendshipExits_DeclineStatusSet() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(suggestedUserId)).thenReturn(suggestedUser);
        when(suggestedUserService.validationCheck(loggedInUserId, suggestedUserId)).thenReturn(true);
        when(suggestedUserService.attemptToDeclinePendingRequest(loggedInUserId, suggestedUserId)).thenReturn(false);
        when(suggestedUserService.setDeclinedFriendship(loggedInUser, suggestedUser)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.handleAcceptDecline("decline", suggestedUserId, authentication, model);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue((Boolean) response.getBody().get("success"));
    }
}
