 package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

 import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ManageFriendsController;
 import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
 import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
 import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
 import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
 import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.Mockito;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.core.Authentication;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.MockitoAnnotations;
 import org.springframework.ui.Model;
 import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

 import java.util.*;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.ArgumentMatchers.anyList;
 import static org.mockito.ArgumentMatchers.eq;
 import static org.mockito.Mockito.*;
 import static org.mockito.Mockito.verify;

 @ExtendWith(MockitoExtension.class)
 public class ManageFriendsControllerTest {
     @Mock
     private FriendService friendService;

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
     public void whenNotFriends_thenNewFriendshipMadeWithPending() {
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
         when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
         when(friendService.getAcceptedFriendship(loggedInUserId, otherUserId)).thenReturn(null);
         when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(null);
         String result = manageFriendsController.manageFriendsInvite(authentication, otherUserId);

         verify(friendService, times(1)).save(any(Friends.class));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsInvite method
      */
     @Test
     public void whenAlreadyFriends_thenNoNewFriendRecord() {
         Friends existingFriends = new Friends(loggedInUser, otherUser, "accepted");

         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
         when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
         when(friendService.getAcceptedFriendship(loggedInUserId, otherUserId)).thenReturn(existingFriends);

         String result = manageFriendsController.manageFriendsInvite(authentication, otherUserId);

         verify(friendService, never()).save(any(Friends.class));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsInvite method
      */
     @Test
     public void whenFriendshipPending_thenNoNewFriendRecord() {
         Friends friendRequest = new Friends(loggedInUser, otherUser, "Pending");

         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
         when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
         when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(friendRequest);

         String result = manageFriendsController.manageFriendsInvite(authentication, otherUserId);

         verify(friendService, never()).save(any(Friends.class));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsInvite method
      */
     @Test
     public void whenLoggedInUserRequestsThemself_thenNoNewFriendRecord() {
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);

         String result = manageFriendsController.manageFriendsInvite(authentication, loggedInUserId);

         verify(friendService, never()).save(any(Friends.class));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsAccept method
      */
     @Test
     public void whenFriendshipPending_thenFriendAccepted() {
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);

         Friends existingRequest = new Friends(loggedInUser, otherUser, "Pending");
         friendService.save(existingRequest);
         when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(existingRequest);

         String result = manageFriendsController.manageFriendsAccept(authentication, otherUserId);

         existingRequest.setStatus("accepted");
         friendService.save(existingRequest);

         // Had to trim the newFriends object because it had a newline and was failing the test
         verify(friendService, times(3)).save(existingRequest);
         verify(friendService).getFriendship(loggedInUser.getId(), otherUser.getId());
         assertEquals(friendService.getFriendship(loggedInUserId, otherUserId).getStatus(), "accepted");
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsAccept method
      */
     @Test
     public void whenNoFriendshipPending_thenDoesNotAcceptFriend() {
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(null);

         String result = manageFriendsController.manageFriendsAccept(authentication, otherUserId);

         verify(friendService, never()).save(any(Friends.class));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsAccept method
      */
     @Test
     public void whenOtherUserDoesNotExist_thenDoesNotAcceptFriend() {
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);

         String result = manageFriendsController.manageFriendsAccept(authentication, invalidUserId);

         verify(friendService, never()).save(any(Friends.class));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsAccept method
      */
     @Test
     public void whenAlreadyFriends_thenDoesNotAcceptFriend() {
         Friends newFriends = new Friends(loggedInUser, otherUser, "accepted");
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(friendService.getAcceptedFriendship(loggedInUserId, otherUserId)).thenReturn(newFriends);

         String result = manageFriendsController.manageFriendsAccept(authentication, otherUserId);
         verify(friendService, never()).save(any(Friends.class));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsDecline method
      */
     @Test
     public void whenFriendshipPending_thenFriendDeclined() {
         List<Friends> friendShip = new ArrayList<>();
         Friends existingRequest = new Friends(otherUser, loggedInUser, "Pending");
         friendShip.add(existingRequest);
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
         when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
         when(friendService.getReceivedRequests(loggedInUserId)).thenReturn(friendShip);
         String result = manageFriendsController.manageFriendsDecline(authentication, otherUserId);

         verify(friendService).save(any(Friends.class));
         assertEquals("Declined", existingRequest.getStatus());
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsDecline method
      */
     @Test
     public void whenNoFriendshipPending_thenDoesNotDeclineFriend() {
         List<Friends> friendShip = new ArrayList<>();
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
         when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
         when(friendService.getReceivedRequests(loggedInUserId)).thenReturn(friendShip);

         String result = manageFriendsController.manageFriendsDecline(authentication, otherUserId);

         verify(friendService, never()).save(any(Friends.class));
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
         assertEquals("redirect:/users/manageFriends", result);
     }


     /**
      * Testing the manageFriendsDecline method
      */
     @Test
     public void whenAlreadyFriends_thenDoesNotDeclineFriend() {
         Friends newFriends = new Friends(loggedInUser, otherUser, "accepted");

         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(friendService.getAcceptedFriendship(loggedInUserId, otherUserId)).thenReturn(newFriends);

         String result = manageFriendsController.manageFriendsAccept(authentication, otherUserId);

         verify(friendService, never()).save(any(Friends.class));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the manageFriendsSearch method
      */
     @Test
     public void whenSearchWithEmail_andUserExists_thenSearchResultsNotEmpty() {
         String searchUser = "john.doe@gmail.com";
         List<GardenUser> searchResults = Collections.singletonList(new GardenUser());
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
         assertEquals("redirect:/users/manageFriends", result);
         assertEquals(searchResults, rm.getFlashAttributes().get("searchResults"));
     }

     /**
      * Testing the manageFriendsSearch method
      */
     @Test
     public void whenSearchWithEmail_andUserDoesNotExist_thenSearchResultsEmpty() {
         String searchUser = "jane.doe@gmail.com";
         List<GardenUser> searchResults = Collections.emptyList();
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
         assertEquals("redirect:/users/manageFriends", result);
         assertEquals(searchResults, rm.getFlashAttributes().get("searchResults"));    }

     /**
      * Testing the manageFriendsSearch method
      */
     @Test
     public void whenSearchWithName_andUserExists_thenSearchResultsNotEmpty() {
         String searchUser = "john doe";
         List<GardenUser> searchResults = Collections.singletonList(new GardenUser());
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
         assertEquals("redirect:/users/manageFriends", result);
         assertEquals(searchResults, rm.getFlashAttributes().get("searchResults"));    }

     /**
      * Testing the manageFriendsSearch method
      */
     @Test
     public void whenSearchWithName_andUserDoesNotExist_thenSearchResultsEmpty() {
         String searchUser = "jane doe";
         List<GardenUser> searchResults = Collections.emptyList();
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
         assertEquals("redirect:/users/manageFriends", result);
         assertEquals(searchResults, rm.getFlashAttributes().get("searchResults"));    }

     /**
      * Testing the manageFriendsSearch method
      */
     @Test
     public void whenSearchLoggedInUsersName_thenSearchResultsEmpty() {
         String searchUser = "current user";
         List<GardenUser> searchResults = Collections.emptyList();
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
         assertEquals("redirect:/users/manageFriends", result);
         assertEquals(searchResults, rm.getFlashAttributes().get("searchResults"));    }

     /**
      * Testing the manageFriendsSearch method
      */
     @Test
     public void whenSearchLoggedInUsersEmail_thenSearchResultsEmpty() {
         String searchUser = "logged.in@gmail.com";
         List<GardenUser> searchResults = Collections.emptyList();
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);
         assertEquals("redirect:/users/manageFriends", result);
         assertEquals(searchResults, rm.getFlashAttributes().get("searchResults"));
     }

     /**
      * Testing the manageFriendsSearch method
      */
     @Test
     public void whenFriendRequestReceived_thenUserNotInSearchResults() {
         String searchUser = otherUser.getFname();
         List<GardenUser> searchResults = new ArrayList<>();
         List<Friends> friendsList = new ArrayList<>();
         GardenUser userWithReceivedRequest = loggedInUser;
         searchResults.add(userWithReceivedRequest);

         Friends friendship = new Friends(loggedInUser, otherUser, "Pending");
         friendsList.add(friendship);
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         when(friendService.getReceivedRequests(loggedInUserId)).thenReturn(friendsList);

         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);

         assertFalse(searchResults.contains(userWithReceivedRequest));
         assertEquals("redirect:/users/manageFriends", result);
     }

     @Test
     public void whenRequestPending_thenUserNotInSearchResults() {
         String searchUser = otherUser.getFname();
         List<GardenUser> searchResults = new ArrayList<>();
         List<Friends> friendsList = new ArrayList<>();
         GardenUser userWithPendingRequest = otherUser;
         searchResults.add(userWithPendingRequest);

         Friends friendship = new Friends(otherUser, loggedInUser, "Pending");
         friendsList.add(friendship);
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         when(friendService.getReceivedRequests(loggedInUserId)).thenReturn(friendsList);

         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);

         assertFalse(searchResults.contains(userWithPendingRequest));
         assertEquals("redirect:/users/manageFriends", result);
     }

     @Test
     public void whenRequestPending_andStatusNotPending_thenUserIsInSearchResult () {
         String searchUser = otherUser.getFname();
         List<GardenUser> searchResults = new ArrayList<>();
         List<Friends> friendsList = new ArrayList<>();
         searchResults.add(otherUser);

         Friends friendship = new Friends(loggedInUser, otherUser, "accepted");
         friendsList.add(friendship);
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         when(friendService.getReceivedRequests(loggedInUserId)).thenReturn(friendsList);

         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);

         assertTrue(searchResults.contains(otherUser));
         assertEquals("redirect:/users/manageFriends", result);
     }

     @Test
     public void whenRequestPendingIsNull_thenUserIsInSearchResult() {
         String searchUser = otherUser.getFname();
         List<GardenUser> searchResults = new ArrayList<>();
         searchResults.add(otherUser);
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         when(friendService.getSent(loggedInUserId, otherUserId)).thenReturn(null);

         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);

         assertTrue(searchResults.contains(otherUser));
         assertEquals("redirect:/users/manageFriends", result);
     }

     @Test
     public void whenAlreadyFriends_thenUserNotInSearchResults() {
         String searchUser = otherUser.getFname();
         List<GardenUser> searchResults = new ArrayList<>();
         searchResults.add(loggedInUser);

         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserBySearch(searchUser, loggedInUserId)).thenReturn(searchResults);
         when(friendService.getAcceptedFriendship(loggedInUserId, loggedInUserId)).thenReturn(new Friends());

         RedirectAttributes rm = new RedirectAttributesModelMap();
         String result = manageFriendsController.manageFriendsSearch(authentication, searchUser, rm);

         assertFalse(searchResults.contains(loggedInUser));
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing the viewFriendProfile method
      */
     @Test
     public void whenUserIsFriend_thenShowProfile() {
         Model model = mock(Model.class);

         Friends newFriends = new Friends(loggedInUser, otherUser, "accepted");
         friendService.save(newFriends);

         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(newFriends);
         when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);

         String result = manageFriendsController.viewFriendProfile(authentication, otherUserId, model);

         verify(model).addAttribute(eq("Friend"), any(GardenUser.class));
         assertEquals("users/friendProfile", result);
     }

     /**
      * Testing the viewFriendProfile method
      */
     @Test
     public void whenUserIsNotFriend_thenRedirectHome() {
         Model model = mock(Model.class);

         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(null);

         String result = manageFriendsController.viewFriendProfile(authentication, otherUserId, model);

         verify(model, never()).addAttribute(eq("Friend"), any(GardenUser.class));
         assertEquals("redirect:/", result);
     }

     /**
      * Testing the viewFriendProfile method
      */
     @Test
     public void whenUserIsInvalid_thenRedirectHome() {
         Model model = mock(Model.class);

         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(friendService.getFriendship(loggedInUserId, invalidUserId)).thenReturn(null);

         String result = manageFriendsController.viewFriendProfile(authentication, invalidUserId, model);

         verify(model, never()).addAttribute(eq("Friend"), any(GardenUser.class));
         assertEquals("redirect:/", result);
     }



     /**
      * Testing cancelSentRequest method
      */
     @Test
     public void whenUserCancelFriendRequest_thenFriendshipIsRemoved() {
         Friends friends = new Friends(loggedInUser,otherUser,"Pending");
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(gardenUserService.getUserById(otherUserId)).thenReturn(otherUser);
         when(friendService.getFriendship(loggedInUserId,otherUserId)).thenReturn(friends);
         String result = manageFriendsController.cancelSentRequest(authentication, otherUserId);

         verify(friendService, times(1)).removeFriendship(friends);
         assertEquals("redirect:/users/manageFriends", result);
     }

     /**
      * Testing removeFriend method
      */
     @Test
     public void whenUserRemovesFriend_thenFriendshipIsRemoved() {
         Friends friends = new Friends(loggedInUser, otherUser, "accepted");
         friendService.save(friends);
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);

         String result = manageFriendsController.removeFriend(authentication, otherUserId);

         verify(friendService, times(1)).removeFriend(loggedInUserId, otherUserId);
         assertEquals("redirect:/users/manageFriends", result);

     }
     /**
      * Testing the manageFriendsDecline method
      */

     @Test
     public void whenFriendshipExists_thenNoDeclineOption() {
         Friends friends = new Friends(loggedInUser, otherUser, "accepted");
         friendService.save(friends);
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);
         when(friendService.getAcceptedFriendship(loggedInUserId, otherUserId)).thenReturn(friends);

         String result = manageFriendsController.manageFriendsDecline(authentication, otherUserId);
         assertNotNull(friendService.getAcceptedFriendship(loggedInUserId, otherUserId));
         assertEquals("redirect:/users/manageFriends", result);

     }

     /**
      * Testing the manageFriendsAccept method
      */
     @Test
     public void testManageFriendsAccept() {
         // Arrange

         Authentication authentication = mock(Authentication.class);
         when(authentication.getPrincipal()).thenReturn(loggedInUserId);

         Friends friendRequest = new Friends(loggedInUser, otherUser, "Pending");
         when(friendService.getFriendship(loggedInUserId, otherUserId)).thenReturn(friendRequest);

         List<Friends> sentAndDeclinedList = Collections.singletonList(friendRequest);
         when(friendService.getSentRequestsDeclined(loggedInUserId)).thenReturn(sentAndDeclinedList);

         manageFriendsController.manageFriendsAccept(authentication, otherUserId);

         verify(friendService).delete(friendRequest);
     }

     /**
      * Testing the manageFriendsDecline method
      */
     @Test
     public void testGetAllFriendsElseCondition() {
         // Arrange
         Friends friendPair = new Friends(otherUser, loggedInUser, "accepted");

         List<Friends> friendsPairList = Collections.singletonList(friendPair);

         FriendsRepository friendsRepository = Mockito.mock(FriendsRepository.class);
         when(friendsRepository.getAllFriends(loggedInUserId)).thenReturn(friendsPairList);

         FriendService friendService = new FriendService(friendsRepository);

         // Act
         List<GardenUser> friendsList = friendService.getAllFriends(loggedInUserId);

         // Assert
         assertEquals(1, friendsList.size());
         assertEquals(otherUser, friendsList.get(0));
     }


 }

