package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

class ApplicationControllerTest {
    
    @Mock
    private GardenUserService gardenUserService;

    @Mock
    private FriendService friendService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private SuggestedUserController suggestedUserController;

    private GardenUser loggedInUser;
    private GardenUser requestedUser;
    private Friends friendRequestReceive;
    private Friends friendRequestSend;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loggedInUser = new GardenUser();
        loggedInUser.setId(1L);

        requestedUser = new GardenUser();
        requestedUser.setId(2L);
        //sender-reciver-status
        friendRequestReceive = new Friends(requestedUser, loggedInUser, Friends.Status.PENDING);
        friendRequestSend = new Friends(loggedInUser, requestedUser, Friends.Status.PENDING);


        when(authentication.getPrincipal()).thenReturn(loggedInUser.getId());
        when(gardenUserService.getUserById(loggedInUser.getId())).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(requestedUser.getId())).thenReturn(requestedUser);
    }

    @Test
    void testAcceptFriendRequest_requestStatusAccept() {
        List<Friends> receivedRequests = new ArrayList<>();
        receivedRequests.add(friendRequestReceive);
        System.out.println(receivedRequests);
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

        ResponseEntity<Map<String, Object>> response = suggestedUserController.homeAccept("accept", requestedUser.getId(), authentication, model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertEquals(false, responseBody.get("success"));
        verify(friendService, times(0)).save(any(Friends.class));
    }

    @Test
    void testAcceptRequest_WhenNotReceivedOrSent() {
        List<Friends> receivedRequests = new ArrayList<>();
        List<Friends> sentRequests = new ArrayList<>();

        when(friendService.getReceivedRequests(loggedInUser.getId())).thenReturn(receivedRequests);
        when(friendService.getSentRequests(loggedInUser.getId())).thenReturn(sentRequests);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.homeAccept("accept", requestedUser.getId(), authentication, model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertEquals(false, responseBody.get("success"));
        verify(friendService, times(1)).save(any(Friends.class));
    }

}
