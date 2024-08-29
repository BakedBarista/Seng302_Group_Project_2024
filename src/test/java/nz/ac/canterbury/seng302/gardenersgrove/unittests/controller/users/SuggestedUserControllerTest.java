package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestedUserControllerTest {

    private static SuggestedUserController suggestedUserController;
    private static GardenUserService gardenUserService;
    private static Model model;
    private static Authentication authentication;
    private static GardenService gardenService;
    private static FriendService friendService;

    private static SuggestedUserService suggestedUserService;
    private static Long loggedInUserId = 1L;
    private static Long requestedUserId = 2L;
    private static GardenUser loggedInUser;
    private static GardenUser requestedUser;
    private static Friends friendRequestReceive;
    private static Friends friendRequestSend;

    @BeforeAll
    static void setup() {
        gardenUserService = Mockito.mock(GardenUserService.class);
        gardenService = Mockito.mock(GardenService.class);
        authentication = Mockito.mock(Authentication.class);
        friendService = Mockito.mock(FriendService.class);
        suggestedUserService = Mockito.mock(SuggestedUserService.class);
        suggestedUserController = new SuggestedUserController(gardenService, gardenUserService, friendService, suggestedUserService);
        loggedInUser = new GardenUser();
        loggedInUser.setId(loggedInUserId);
        loggedInUser.setEmail("logged.in@gmail.com");
        loggedInUser.setFname("Current");
        loggedInUser.setLname("User");
        loggedInUser.setDescription("This is a description");
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);

        requestedUser = new GardenUser();
        requestedUser.setId(requestedUserId);
        //sender-receiver-status
        friendRequestReceive = new Friends(requestedUser, loggedInUser, Friends.Status.PENDING);
        friendRequestSend = new Friends(loggedInUser, requestedUser, Friends.Status.PENDING);

        when(authentication.getPrincipal()).thenReturn(loggedInUser.getId());
        when(gardenUserService.getUserById(loggedInUser.getId())).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(requestedUser.getId())).thenReturn(requestedUser);

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

        Assertions.assertEquals("home", page);
    }

    @Test
    void testAcceptFriendRequest_requestStatusAccept() {
        List<Friends> receivedRequests = new ArrayList<>();
        receivedRequests.add(friendRequestReceive);
        when(friendService.getReceivedRequests(loggedInUser.getId())).thenReturn(receivedRequests);
        ResponseEntity<Map<String, Object>> response = suggestedUserController.homeAccept("accept", requestedUser.getId(), authentication, model);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertEquals(true, responseBody.get("success"));
        assertEquals(Friends.Status.ACCEPTED, friendRequestReceive.getStatus());
    }

    @Test
    void testAcceptRequest_WhenNotReceived() {
        List<Friends> receivedRequests = new ArrayList<>();
        List<Friends> sentRequests = new ArrayList<>();
        sentRequests.add(friendRequestSend);

        when(friendService.getReceivedRequests(loggedInUser.getId())).thenReturn(receivedRequests);
        when(friendService.getSentRequests(loggedInUser.getId())).thenReturn(sentRequests);
        when(suggestedUserService.friendRecordExists(loggedInUserId, requestedUserId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.homeAccept("accept", requestedUserId, authentication, model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertEquals(false, responseBody.get("success"));
//        verify(friendService, times(1)).save(any(Friends.class));
    }

    @Test
    void testAcceptRequest_WhenNotReceivedOrSent() {
        List<Friends> receivedRequests = new ArrayList<>();
        List<Friends> sentRequests = new ArrayList<>();

        when(friendService.getReceivedRequests(loggedInUser.getId())).thenReturn(receivedRequests);
        when(friendService.getSentRequests(loggedInUser.getId())).thenReturn(sentRequests);
        when(suggestedUserService.friendRecordExists(loggedInUserId, requestedUserId)).thenReturn(false);


        ResponseEntity<Map<String, Object>> response = suggestedUserController.homeAccept("accept", requestedUser.getId(), authentication, model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertEquals(false, responseBody.get("success"));
        verify(friendService, times(1)).save(any(Friends.class));
    }
}
