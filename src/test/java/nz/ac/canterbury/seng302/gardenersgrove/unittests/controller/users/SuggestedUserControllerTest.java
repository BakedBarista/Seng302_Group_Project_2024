package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.SuggestedUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.CompatibilityService;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SuggestedUserControllerTest {

    private SuggestedUserController suggestedUserController;
    private GardenUserService gardenUserService;
    private Model model;
    private Authentication authentication;

    @Autowired
    private GardenUserRepository gardenUserRepository;
    

    private FriendService friendService;
    private SuggestedUserService suggestedUserService;
    private CompatibilityService compatibilityService;
    private ObjectMapper objectMapper;
    private TemplateEngine templateEngine;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext context;

    private Long loggedInUserId = 1L;
    private Long suggestedUserId = 2L;
    private GardenUser loggedInUser;
    private GardenUser suggestedUser;
    private Friends friendRequestReceive;
    private Friends friendRequestSend;

    @BeforeEach
    void setup() {
        gardenUserService = Mockito.mock(GardenUserService.class);
        friendService = Mockito.mock(FriendService.class);
        authentication = Mockito.mock(Authentication.class);
        suggestedUserService = Mockito.mock(SuggestedUserService.class);
        compatibilityService = Mockito.mock(CompatibilityService.class);
        objectMapper = new ObjectMapper();
        templateEngine = Mockito.mock(TemplateEngine.class);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        context = Mockito.mock(ServletContext.class);
        when(request.getServletContext()).thenReturn(context);

        suggestedUserController = new SuggestedUserController(friendService, gardenUserService, suggestedUserService, compatibilityService, objectMapper, templateEngine);

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
        friendRequestReceive = new Friends(suggestedUser, loggedInUser, PENDING);
        friendRequestSend = new Friends(loggedInUser, suggestedUser, PENDING);

        when(authentication.getPrincipal()).thenReturn(loggedInUser.getId());
        when(gardenUserService.getUserById(loggedInUser.getId())).thenReturn(loggedInUser);
        when(gardenUserService.getUserById(suggestedUser.getId())).thenReturn(suggestedUser);

    }

    /**
     * Testing the get method of the home page
     */
    @Test
    void whenIViewMyPublicProfile_thenIAmTakenToThePublicProfilePage() throws JsonProcessingException {
        model = Mockito.mock(Model.class);

        GardenUser suggestedUser = new GardenUser();
        suggestedUser.setId(3L);
        suggestedUser.setDescription("Another description");

        List<GardenUser> suggestedUsers = Collections.singletonList(suggestedUser);


        Mockito.when(request.getServletContext()).thenReturn(context);
        Mockito.when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        Mockito.when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        Mockito.when(friendService.availableConnections(loggedInUser)).thenReturn(suggestedUsers);

        String page = suggestedUserController.home(authentication, model, request, response);

        verify(compatibilityService).friendshipCompatibilityQuotient(any(), any());
        verify(model).addAttribute(eq("userId"), any());
        verify(model).addAttribute(eq("name"), any());
        verify(model).addAttribute(eq("description"), any());
        verify(model).addAttribute(eq("userList"), assertArg((String s) -> {
            Assertions.assertTrue(s.contains("favouriteGarden"));
            Assertions.assertTrue(s.contains("favouritePlants"));
        }));

        Assertions.assertEquals("suggestedFriends", page);
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

    @Test
    void testGetHome_NoLoggedIn_LandingPageReturned() {
        String result = suggestedUserController.home(null, model, request, response);

        Assertions.assertEquals("home", result);
    }

    @Test
    void testHandleAcceptDecline_NotLoggedIn_NothingHappens() {
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(null);
        when(gardenUserService.getUserById(suggestedUserId)).thenReturn(suggestedUser);

        ResponseEntity<Map<String, Object>> response = suggestedUserController.handleAcceptDecline("decline", suggestedUserId, authentication, model);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(false, response.getBody().get("success"));
    }

    @Test
    public void testGetSortedSuggestedUserDTOs_NonEmptyList() {
        GardenUser user = new GardenUser();
        GardenUser user1 = new GardenUser();
        GardenUser user2 = new GardenUser();
        user1.setId(1L);
        user2.setId(2L);

        Mockito.when(compatibilityService.friendshipCompatibilityQuotient(user, user1)).thenReturn(70.0);
        Mockito.when(compatibilityService.friendshipCompatibilityQuotient(user, user2)).thenReturn(90.0);
        List<GardenUser> connectionListMinusFriends = Arrays.asList(user1, user2);

        List<SuggestedUserDTO> result = suggestedUserController.getSortedSuggestedUserDTOs(connectionListMinusFriends, user, request, response);

        // checking the right order
        assertEquals(90, result.get(0).getCompatibility());
        assertEquals(70, result.get(1).getCompatibility());
    }

//    @Test
//    public void givenLowCompatibility_andUserSentRequest_whenCardsShown_thenSentRequestShowsFirst() throws JsonProcessingException {
//        GardenUser Liam = new GardenUser("liam", "user", "laims@gmail.com", "password", LocalDate.of(1970, 10, 10));
//
//        GardenUser highCompatibilityUser = new GardenUser("test", "user", "test@gmail.com", "password", LocalDate.of(1970, 10, 10));
//        GardenUser lowCompatibilitySentRequest = new GardenUser("test2", "user", "test2@gmail.com", "password", LocalDate.of(1970, 10, 10));
//
//        List<SuggestedUserDTO> combinedList = Arrays.asList(
//                new SuggestedUserDTO(highCompatibilityUser),
//                new SuggestedUserDTO(lowCompatibilitySentRequest)
//        );
//
//        String jsonUsers = objectMapper.writeValueAsString(combinedList);
//
//        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
//        when(compatibilityService.friendshipCompatibilityQuotient(Liam, highCompatibilityUser)).thenReturn(70.0);
//        when(compatibilityService.friendshipCompatibilityQuotient(Liam, lowCompatibilitySentRequest)).thenReturn(30.0);
//        Mockito.when(friendService.availableConnections(loggedInUser)).thenReturn(List.of(highCompatibilityUser));
//        when(friendService.receivedConnectionRequests(loggedInUser)).thenReturn(List.of(lowCompatibilitySentRequest));
//
//        String result = suggestedUserController.home(null, model, request, response);
//
//        Mockito.verify(model).addAttribute("userList", jsonUsers);
//        assertEquals("suggestedFriends", result);
//    }
}
