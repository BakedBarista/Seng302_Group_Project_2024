package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ApplicationController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

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
    private ApplicationController applicationController;

    private GardenUser loggedInUser;
    private GardenUser requestedUser;
    private Friends friendRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loggedInUser = new GardenUser();
        loggedInUser.setId(1L);

        requestedUser = new GardenUser();
        requestedUser.setId(2L);

        friendRequest = new Friends(loggedInUser, requestedUser, Friends.Status.PENDING);

        when(authentication.getPrincipal()).thenReturn(loggedInUser.getId());
        when(gardenUserService.getUserById(loggedInUser.getId())).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(requestedUser.getId())).thenReturn(requestedUser);
    }

    @Test
    void testAcceptFriendRequest_requestStatusAccept() {
        List<Friends> receivedRequests = new ArrayList<>();
        receivedRequests.add(friendRequest);
        System.out.println(receivedRequests);
        when(friendService.getReceivedRequests(loggedInUser.getId())).thenReturn(receivedRequests);

        String result = applicationController.homeAccept("accept", requestedUser.getId(), authentication, model);

        assertEquals("home", result);
         assertEquals(Friends.Status.ACCEPTED, friendRequest.getStatus());
    }

    @Test
    public void testSendFriendRequest() {
        List<Friends> receivedRequests = new ArrayList<>();
        List<Friends> sentRequests = new ArrayList<>();

        when(friendService.getReceivedRequests(loggedInUser.getId())).thenReturn(receivedRequests);
        when(friendService.getSentRequests(loggedInUser.getId())).thenReturn(sentRequests);

        String result = applicationController.homeAccept("accept", requestedUser.getId(), authentication, model);

        assertEquals("home", result);
        verify(friendService, times(1)).save(any(Friends.class));
    }

    @Test
    public void testAcceptRequest_WhenNotReceived() {
        List<Friends> receivedRequests = new ArrayList<>();
        List<Friends> sentRequests = new ArrayList<>();
        sentRequests.add(friendRequest);

        when(friendService.getReceivedRequests(loggedInUser.getId())).thenReturn(receivedRequests);
        when(friendService.getSentRequests(loggedInUser.getId())).thenReturn(sentRequests);

        String result = applicationController.homeAccept("accept", requestedUser.getId(), authentication, model);

        assertEquals("home", result);
        verify(friendService, times(0)).save(any(Friends.class));
    }

    @Test
    public void testAcceptRequest_WhenNotReceivedOrSent() {
        List<Friends> receivedRequests = new ArrayList<>();
        List<Friends> sentRequests = new ArrayList<>();

        when(friendService.getReceivedRequests(loggedInUser.getId())).thenReturn(receivedRequests);
        when(friendService.getSentRequests(loggedInUser.getId())).thenReturn(sentRequests);

        String result = applicationController.homeAccept("accept", requestedUser.getId(), authentication, model);

        assertEquals("home", result);
        verify(friendService, times(1)).save(any(Friends.class));
    }

}
