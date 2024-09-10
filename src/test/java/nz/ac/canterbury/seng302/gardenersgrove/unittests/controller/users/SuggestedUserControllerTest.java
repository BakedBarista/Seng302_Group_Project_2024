package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.times;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Map;

import java.util.Map;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestedUserControllerTest {

    private static SuggestedUserController suggestedUserController;
    private static GardenUserService gardenUserService;
    private static Model model;
    private static Authentication authentication;

    private static FriendService friendService;
    private static SuggestedUserService suggestedUserService;
    private static ObjectMapper objectMapper;

    private static Long loggedInUserId = 1L;
    private static Long suggestedUserId = 2L;
    private static GardenUser loggedInUser;
    private static GardenUser suggestedUser;
    private static Friends friendRequestReceive;
    private static Friends friendRequestSend;

    @BeforeAll
    static void setup() {
        gardenUserService = Mockito.mock(GardenUserService.class);
        friendService = Mockito.mock(FriendService.class);
        authentication = Mockito.mock(Authentication.class);
        suggestedUserService = Mockito.mock(SuggestedUserService.class);
        suggestedUserController = new SuggestedUserController(friendService, gardenUserService, suggestedUserService, objectMapper);
        objectMapper = new ObjectMapper();

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
    void whenIViewMyPublicProfile_thenIAmTakenToThePublicProfilePage() throws JsonProcessingException {
        model = Mockito.mock(Model.class);

        GardenUser suggestedUser = new GardenUser();
        suggestedUser.setId(3L);
        suggestedUser.setDescription("Another description");

        List<GardenUser> suggestedUsers = Collections.singletonList(suggestedUser);


        Mockito.when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        Mockito.when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        Mockito.when(friendService.availableConnections(loggedInUser)).thenReturn(suggestedUsers);
        Mockito.when(objectMapper.writeValueAsString(suggestedUsers)).thenReturn("test");

        String page = suggestedUserController.home(authentication, model);

        Mockito.verify(model).addAttribute(eq("userId"), any());
        Mockito.verify(model).addAttribute(eq("name"), any());
        Mockito.verify(model).addAttribute(eq("description"), any());
        Mockito.verify(model).addAttribute(eq("userList"), any());

        Assertions.assertEquals("home", page);
    }

    @Test
    void testHandleAcceptDecline_ValidationFails_DoNothing() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(suggestedUserService.validationCheck(loggedInUserId, suggestedUserId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.handleAcceptDecline("accept", suggestedUserId, authentication, model);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNull(response.getBody().get("success"));
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
        Assertions.assertNull(response.getBody().get("success"));
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
        Assertions.assertNull(response.getBody().get("success"));
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
        Assertions.assertNull(response.getBody().get("success"));
    }
}
