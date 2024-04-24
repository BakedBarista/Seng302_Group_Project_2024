package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
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

    @Spy
    private GardenUser user;

    private long id;

    @BeforeEach
    public void setUp() {
        id = 1;
        String firstName = "jane";
        String lastName = "doe";
        String email = "jane.doe@mail.com";
        String password = "TESTPassword123!";
        String dob = "01/01/2000";
        user = new GardenUser(firstName, lastName, email, password, dob);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddEmailTokenAndTimeToUser_AddsTokenAndTimeToUser() {
        // add user to persistence and then call function to add token and time instant
        gardenUserService.addUser(user);
        registerController.addEmailTokenAndTimeToUser(id);

        // check that toke and time instant persist
        Assertions.assertNotNull(gardenUserService.getUserById(id).getEmailValidationToken());
    }
}
