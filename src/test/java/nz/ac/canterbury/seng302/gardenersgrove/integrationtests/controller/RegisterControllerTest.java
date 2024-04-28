package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

@SpringBootTest
public class RegisterControllerTest {

    @Autowired
    private RegisterController registerController;

    @Autowired
    private GardenUserService gardenUserService;

    private GardenUser user;

    @MockBean
    private EmailSenderService emailSenderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenAddEmailTokenAndTimeToUserCalled_thenTokenAndTimeAreAddedToUser() {
        // add user to persistence and then call function to add token and time instant
        String firstName = "jane";
        String lastName = "doe";
        String email = "jane.doe@mail.com";
        String password = "TESTPassword123!";
        String dob = "01/01/2000";
        user = new GardenUser(firstName, lastName, email, password, dob);
        gardenUserService.addUser(user);

        registerController.addEmailTokenAndTimeToUser(user.getId());

        // check that toke and time instant persist
        Assertions.assertNotNull(gardenUserService.getUserById(user.getId()).getEmailValidationToken());
    }

    @Test
    public void whenAddEmailTokenAndTimeToUserCalled_thenAnEmailIsSent() {
        // add user to persistence and then call function to add token and time instant
        String firstName = "jane";
        String lastName = "doe";
        String email = "john.doe@mail.com";
        String password = "TESTPassword123!";
        String dob = "01/01/2000";
        user = new GardenUser(firstName, lastName, email, password, dob);
        gardenUserService.addUser(user);

        registerController.addEmailTokenAndTimeToUser(user.getId());

        Mockito.verify(emailSenderService).sendEmail(Mockito.any(GardenUser.class), Mockito.eq("Welcome to Gardener's Grove"),
                Mockito.any());
    }
}
