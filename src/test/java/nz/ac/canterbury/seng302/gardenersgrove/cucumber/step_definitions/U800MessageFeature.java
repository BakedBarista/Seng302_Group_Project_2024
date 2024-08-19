package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class U800MessageFeature {
    private MessageController messageController;
    private static Model model;

    private String result;

    @Given("I am viewing my friends list")
    public void i_am_viewing_my_friends_list() {
        messageController = new MessageController();
        model = null;
    }

    @When("I click the {string} button next to one of my friends")
    public void i_click_the_message_button(String button) {
        Long friendId = 1L;
        result = messageController.messageFriend(friendId, model);
    }

    @Then("I am taken to the message page")
    public void i_am_taken_to_the_message_page() {
        assertEquals("users/message", result);
    }
}
