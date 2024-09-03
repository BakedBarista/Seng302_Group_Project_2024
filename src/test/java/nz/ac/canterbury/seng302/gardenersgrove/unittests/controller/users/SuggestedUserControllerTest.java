package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.times;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

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
    static Long loggedInUserId = 1L;
    static GardenUser loggedInUser;

    static ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        gardenUserService = Mockito.mock(GardenUserService.class);
        friendService = Mockito.mock(FriendService.class);
        authentication = Mockito.mock(Authentication.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        suggestedUserController = new SuggestedUserController(friendService, gardenUserService, objectMapper);

        loggedInUser = new GardenUser();
        loggedInUser.setId(loggedInUserId);
        loggedInUser.setEmail("logged.in@gmail.com");
        loggedInUser.setFname("Current");
        loggedInUser.setLname("User");
        loggedInUser.setDescription("This is a description");
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);

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
}
