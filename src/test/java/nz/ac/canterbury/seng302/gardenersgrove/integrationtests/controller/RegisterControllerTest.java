package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegisterControllerTest {

    @Autowired
    private RegisterController registerController;

    @Autowired
    private GardenUserService gardenUserService;

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testAddEmailTokenAndTimeToUser_AddsTokenAndTimeToUser() {
        long id = 1;
        String validFirstName = "jane";
        String validLastName = "doe";
        String validEmail = "jane.doe@mail.com";
        String validPassword = "TESTPassword123!";
        String validDOB = "01/01/2000";
        GardenUser user = new GardenUser(validFirstName, validLastName, validEmail, validPassword, validDOB);
        user.setId(id);

        // add user to persistence and then call function to add token and time instant
        gardenUserService.addUser(user);
        registerController.addEmailTokenAndTimeToUser(id);

        // check that toke and time instant persist
        Assertions.assertNotNull(gardenUserService.getUserById(id).getEmailValidationToken());
    }
}
