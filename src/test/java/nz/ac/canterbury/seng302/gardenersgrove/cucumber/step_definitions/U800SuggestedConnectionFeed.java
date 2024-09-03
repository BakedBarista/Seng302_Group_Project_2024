package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ApplicationController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800SuggestedConnectionFeed {
    private static Authentication authentication;
    private static Model model;

    @Autowired
    private SuggestedUserController suggestedUserController;
    @Autowired
    private ApplicationController applicationController;
    @Autowired
    private GardenUserRepository gardenUserRepository;

    private static GardenUser userLiam;
    private static GardenUser userBen;

    @Before
    public void setup() {
        userLiam = new GardenUser("Liam", "Dough", "liam@friends.org", "password", null);
        userBen = new GardenUser("Ben", "Dough", "ben@friends.org", "password", null);
        gardenUserRepository.save(userLiam);
        gardenUserRepository.save(userBen);

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userLiam.getId());
        model = mock(Model.class);
    }


    @Given("I am on the homepage looking at the list of user profiles")
    public void iAmOnTheHomepageLookingAtTheListOfUserProfiles() {
        String result = suggestedUserController.home(authentication, model);
        assertEquals("home", result);
    }

    @When("I accept or decline a profile")
    public void iAcceptOrDeclineAProfile() {
        ResponseEntity<Map<String, Object>> result = applicationController.homeAccept("accept", 2L, authentication, model);
        assertFalse((Boolean) result.getBody().get("success"));
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Then("I am not shown that profile again")
    public void iAmNotShownThatProfileAgain() {
    }
}
