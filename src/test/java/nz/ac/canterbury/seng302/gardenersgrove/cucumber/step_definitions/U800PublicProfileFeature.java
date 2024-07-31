package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class U800PublicProfileFeature {

    @Given("I am on my edit profile page")
    public void i_am_on_my_edit_profile_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("I enter a valid description \\(that is not longer than {int} characters) about myself")
    public void i_enter_a_valid_description_that_is_not_longer_than_characters_about_myself(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I click {string}")
    public void i_click(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the description is displayed on my profile page")
    public void the_description_is_displayed_on_my_profile_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I enter a description that is longer than {int} characters")
    public void i_enter_a_description_that_is_longer_than_characters(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("an error message tells me “Your description must be less than {int} characters”")
    public void an_error_message_tells_me_your_description_must_be_less_than_characters(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I choose a new cover photo of type \\(.jpg, .jpeg, .png, .svg) and less than 10MB")
    public void i_choose_a_new_cover_photo_of_type_jpg_jpeg_png_svg_and_less_than_10mb() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the photo displays in the cover photo section behind my profile picture \\(e.g. like a LinkedIn profile)")
    public void the_photo_displays_in_the_cover_photo_section_behind_my_profile_picture_e_g_like_a_linked_in_profile() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I submit a file that is not either a png, jpg or svg")
    public void i_submit_a_file_that_is_not_either_a_png_jpg_or_svg() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("an error message tells me “Image must be of type png, jpg or svg”")
    public void an_error_message_tells_me_image_must_be_of_type_png_jpg_or_svg() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I submit a valid file with a size of more than 10MB")
    public void i_submit_a_valid_file_with_a_size_of_more_than_10mb() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("an error message tells me “Image must be less than 10MB”")
    public void an_error_message_tells_me_image_must_be_less_than_10mb() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the previous profile banner is overwritten and cannot be accessed anymore")
    public void the_previous_profile_banner_is_overwritten_and_cannot_be_accessed_anymore() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
