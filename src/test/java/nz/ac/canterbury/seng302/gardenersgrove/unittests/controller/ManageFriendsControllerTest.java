package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ManageFriendsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ManageFriendsControllerTest {
    @Mock
    private GardenService gardenService;

    @Mock
    private FriendService friendService;

    @Mock
    private RequestService requestService;

    @Mock
    private GardenUserService gardenUserService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ManageFriendsController manageFriendsController;

    Long loggedInUserId;
    Long otherUserId;
    Long invalidUserId;
    GardenUser loggedInUser = new GardenUser();
    GardenUser otherUser = new GardenUser();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loggedInUserId = 1L;
        otherUserId = 2L;
        invalidUserId = 123L;

        loggedInUser.setId(loggedInUserId);
        loggedInUser.setEmail("logged.in@gmail.com");
        loggedInUser.setFname("Current");
        loggedInUser.setFname("User");

        otherUser.setId(otherUserId);
        otherUser.setEmail("john.doe@gmail.com");
        otherUser.setFname("John");
        otherUser.setFname("Doe");
    }

    /**
     * Testing the manageFriends GET method
     */
    @Test
    public void testManageFriends() {
        Model model = mock(Model.class);
        String result = manageFriendsController.manageFriends(authentication, model);

        verify(model).addAttribute(eq("friends"), anyList());
        verify(model).addAttribute(eq("allUsers"), anyList());
        verify(model).addAttribute(eq("sentRequests"), anyList());
        verify(model).addAttribute(eq("receivedRequests"), anyList());

        assertEquals("users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsInvite method
     */
    @Test
    public void whenNoRequestAndNotFriends_thenNewRequestMade() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
        when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(null);
        when(requestService.getRequest(otherUserId, loggedInUserId)).thenReturn(Optional.empty());

        String result = manageFriendsController.manageFriendsInvite(authentication, otherUserId);

        verify(requestService, times(1)).save(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsInvite method
     */
    @Test
    public void whenNoRequestButAlreadyFriends_thenNoNewRequest() {
        Friends newFriends = new Friends(loggedInUser, otherUser);
        friendService.save(newFriends); // Do I need this??

        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
        when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(newFriends);
        when(requestService.getRequest(otherUserId, loggedInUserId)).thenReturn(Optional.empty());

        String result = manageFriendsController.manageFriendsInvite(authentication, otherUserId);

        verify(requestService, never()).save(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsInvite method
     */
    @Test
    public void whenRequestExists_thenNoNewRequest() {
        Requests requestEntity = new Requests(loggedInUser, otherUser, "pending");
//        requestService.save(requestEntity);

        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
        when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(null);
        when(requestService.getRequest(otherUserId, loggedInUserId)).thenReturn(Optional.of(requestEntity));

        String result = manageFriendsController.manageFriendsInvite(authentication, otherUserId);

        verify(requestService, never()).save(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsInvite method
     */
    @Test
    public void whenLoggedInUserIsRequestedUser_thenNoNewRequest() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(friendService.getFriendship(loggedInUserId, loggedInUserId)).thenReturn(null);
        when(requestService.getRequest(loggedInUserId, loggedInUserId)).thenReturn(Optional.empty());

        String result = manageFriendsController.manageFriendsInvite(authentication, loggedInUserId);

        verify(requestService, never()).save(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsAccept method
     */
    @Test
    public void whenRequestExists_thenFriendAccepted() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);

        Requests existingRequest = new Requests(loggedInUser, otherUser, "pending");
        when(requestService.getRequest(loggedInUser.getId(), otherUser.getId())).thenReturn(Optional.of(existingRequest));

        String result = manageFriendsController.manageFriendsAccept(authentication, otherUserId);

        Friends newFriends = new Friends(loggedInUser, otherUser);

        // Had to trim the newFriends object because it had a newline and was failing the test
        // Previously: verify(friendService).save(newFriends)
        verify(friendService).save(argThat(actual -> actual.toString().trim().equals(newFriends.toString().trim())));

        verify(requestService).getRequest(loggedInUser.getId(), otherUser.getId());
        verify(requestService).delete(existingRequest);
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsAccept method
     */
    @Test
    public void whenNoRequest_thenDoesNotAcceptFriend() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
        when(requestService.getRequest(loggedInUser.getId(), otherUser.getId())).thenReturn(Optional.empty());

        String result = manageFriendsController.manageFriendsAccept(authentication, otherUserId);

        verify(friendService, never()).save(any(Friends.class));
        verify(requestService, never()).delete(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsAccept method
     */
    @Test
    public void whenOtherUserDoesNotExist_thenDoesNotAcceptFriend() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(invalidUserId)).thenReturn(null);

        String result = manageFriendsController.manageFriendsAccept(authentication, invalidUserId);

        verify(friendService, never()).save(any(Friends.class));
        verify(requestService, never()).delete(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsAccept method
     */
    @Test
    public void whenAlreadyFriends_thenDoesNotAcceptFriend() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);

        Friends newFriends = new Friends(loggedInUser, otherUser);
//        friendService.save(newFriends);

        String result = manageFriendsController.manageFriendsAccept(authentication, otherUserId);

        verify(friendService, never()).save(any(Friends.class));
        verify(requestService, never()).delete(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsDecline method
     */
    @Test
    public void whenRequestExists_thenFriendDeclined() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);

        Requests existingRequest = new Requests(loggedInUser, otherUser, "pending");
        when(requestService.getRequest(loggedInUserId, otherUserId)).thenReturn(Optional.of(existingRequest));

        String result = manageFriendsController.manageFriendsDecline(authentication, otherUserId);

        verify(friendService, never()).save(any(Friends.class));
        verify(requestService, never()).delete(any(Requests.class));
        verify(requestService).save(existingRequest);
        assertEquals(requestService.getRequest(loggedInUserId, otherUserId).get().getStatus(), "declined");
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsDecline method
     */
    @Test
    public void whenNoRequest_thenDoesNotDeclineFriend() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
        when(requestService.getRequest(loggedInUserId, otherUserId)).thenReturn(Optional.empty());

        String result = manageFriendsController.manageFriendsDecline(authentication, otherUserId);

        verify(friendService, never()).save(any(Friends.class));
        verify(requestService, never()).delete(any(Requests.class));
        verify(requestService, never()).save(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsDecline method
     */
    @Test
    public void whenOtherUserDoesNotExist_thenDoesNotDeclineFriend() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(invalidUserId)).thenReturn(null);

        String result = manageFriendsController.manageFriendsDecline(authentication, invalidUserId);

        verify(friendService, never()).save(any(Friends.class));
        verify(requestService, never()).delete(any(Requests.class));
        verify(requestService, never()).save(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }


    /**
     * Testing the manageFriendsDecline method
     */
    @Test
    public void whenAlreadyFriends_thenDoesNotDeclineFriend() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);

        Friends newFriends = new Friends(loggedInUser, otherUser);
//        friendService.save(newFriends);

        String result = manageFriendsController.manageFriendsAccept(authentication, otherUserId);

        verify(friendService, never()).save(any(Friends.class));
        verify(requestService, never()).delete(any(Requests.class));
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsSearch method
     */
    @Test
    public void whenSearchWithEmail_andUserExists_thenSearchResultsNotEmpty() {
        String searchUser = "john.doe@gmail.com";
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        RedirectAttributes rm = new RedirectAttributesModelMap();
        String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
        assertEquals("redirect:/users/manageFriends", result);
    }

    /**
     * Testing the manageFriendsSearch method
     */
    @Test
    public void whenSearchWithEmail_andUserDoesNotExist_thenSearchResultsEmpty() {
        String searchUser = "jane.doe@gmail.com";
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        RedirectAttributes rm = new RedirectAttributesModelMap();
        String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);

        assertEquals("redirect:/users/manageFriends", result);    }

    /**
     * Testing the manageFriendsSearch method
     */
    @Test
    public void whenSearchWithName_andUserExists_thenSearchResultsNotEmpty() {
        String searchUser = "john doe";
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        RedirectAttributes rm = new RedirectAttributesModelMap();
        String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
        assertEquals("redirect:/users/manageFriends", result);    }

    /**
     * Testing the manageFriendsSearch method
     */
    @Test
    public void whenSearchWithName_andUserDoesNotExist_thenSearchResultsEmpty() {
        String searchUser = "jane doe";
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        RedirectAttributes rm = new RedirectAttributesModelMap();
        String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
        assertEquals("redirect:/users/manageFriends", result);    }

    /**
     * Testing the manageFriendsSearch method
     */
    @Test
    public void whenSearchLoggedInUsersName_thenSearchResultsEmpty() {
        String searchUser = "current user";
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        RedirectAttributes rm = new RedirectAttributesModelMap();
        String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
        assertEquals("redirect:/users/manageFriends", result);    }

    /**
     * Testing the manageFriendsSearch method
     */
    @Test
    public void whenSearchLoggedInUsersEmail_thenSearchResultsEmpty() {
        String searchUser = "logged.in@gmail.com";
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        RedirectAttributes rm = new RedirectAttributesModelMap();
        String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
        assertEquals("redirect:/users/manageFriends", result);    }
}
