package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;

@SpringBootTest
public class RegisterControllerTest {

    @Autowired
    private RegisterController registerController;

    @Autowired
    private GardenUserService gardenUserService;

    private GardenUser user;

    @MockBean
    private EmailSenderService emailSenderService;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenSubmitRegisterIsCalled_thenAnEmailIsSent() {
        // add user to persistence and then call function to add token and time instant
        String firstName = "jane";
        String lastName = "doe";
        String email = "john.doe@mail.com";
        String password = "TESTPassword123!";
        LocalDate dob = LocalDate.of(1998, 1, 1);
        String token = "token";
        user = new GardenUser(firstName, lastName, email, password, dob);
        user.setEmailValidationToken(token);
        gardenUserService.addUser(user);
        registerController.sendRegisterEmail(user, token);

        user = gardenUserService.getUserById(user.getId());
        Mockito.verify(emailSenderService).sendEmail(
                Mockito.assertArg((GardenUser actualUser) -> {
                    Assertions.assertEquals(user.getId(), actualUser.getId());
                }),
                Mockito.eq("Welcome to Gardener's Grove"),
                Mockito.assertArg((message) -> {
                    Assertions.assertTrue(message.contains(token));
                }));
    }
}
