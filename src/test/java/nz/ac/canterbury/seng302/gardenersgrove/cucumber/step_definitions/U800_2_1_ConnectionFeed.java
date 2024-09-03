package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import static org.mockito.Mockito.mock;

public class U800_2_1_ConnectionFeed {
    private static Authentication authentication;
    private static Model model;

    @BeforeAll
    public static void setup() {
        authentication = mock(Authentication.class);
        model = mock(Model.class);
    }


    @Given("I am on the homepage looking at the list of user profiles")
    public void iAmOnTheHomepageLookingAtTheListOfUserProfiles() {
    }

    @When("I accept or decline a profile")
    public void iAcceptOrDeclineAProfile() {

    }

    @Then("I am not shown that profile again")
    public void iAmNotShownThatProfileAgain() {
    }
}
