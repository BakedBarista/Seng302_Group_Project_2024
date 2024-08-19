package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.PublicProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ProfanityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SuggestedUserControllerTest {

    private static SuggestedUserController suggestedUserController;
    private static GardenUserService gardenUserService;
    private static GardenUser user;
    private static Model model;
    private static Authentication authentication;
    private static Long userId;
    private static GardenService gardenService;
    static Long loggedInUserId = 1L;
    static GardenUser loggedInUser;
    static GardenUser suggestedUser;
    static Long suggestedUserId = 2L;
    private List<GardenUser> suggestedUserList;

    @BeforeAll
    static void setup() {
        userId = 1L;
        gardenUserService = Mockito.mock(GardenUserService.class);
        authentication = Mockito.mock(Authentication.class);
        user = new GardenUser();
        suggestedUserController = new SuggestedUserController(gardenService, gardenUserService);
        loggedInUser = new GardenUser();
        loggedInUser.setId(loggedInUserId);
        loggedInUser.setEmail("logged.in@gmail.com");
        loggedInUser.setFname("Current");
        loggedInUser.setLname("User");
        loggedInUser.setDescription("This is a description");
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);

        List<GardenUser> suggestedUserList = new ArrayList<>();
        suggestedUser = new GardenUser();
        suggestedUser.setId(suggestedUserId);
        suggestedUser.setEmail("suggested.user@gmail.com");
        suggestedUser.setFname("Suggested");
        suggestedUser.setLname("User");
        suggestedUser.setDescription("This is the about me description that will show up on the card");
        suggestedUserList.add(suggestedUser);
    }

    /**
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
}
