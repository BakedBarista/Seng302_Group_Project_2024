package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

public class U2LogInFeature {
    public static GardenUser user;

    @Given("I am logged in as {string}")
    public void i_am_logged_in_as(String fname) {
        user = new GardenUser();
        user.setFname(fname);
    }

    @Given("My email is {string}")
    public void my_email_is(String email) {
        user.setEmail(email);
    }

    @Given("My password is {string}")
    public void my_password_is(String password) {
        user.setPassword(password);
    }

}
