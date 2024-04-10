package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.LoginController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class LoginControllerTest {

    @Mock
    private GardenUserService userService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testWhenIHaveNotConfirmedEmail_andITryToLogin_IAmTakenToTokenConfirmationPage() {
        long id = 1;
        String validFirstName = "jane";
        String validLastName = "doe";
        String validEmail = "jane.doe@mail.com";
        String validPassword = "TESTPassword123!";
        String validDOB = "01/01/2000";
        String expectedResource = "redirect:/users/user/"+id+"/authenticateEmail";

        GardenUser unconfirmedUser = new GardenUser(validFirstName, validLastName, validEmail, validPassword, validDOB);
        unconfirmedUser.setId(id);
        unconfirmedUser.setEmailValidationToken("123456");
        unconfirmedUser.setEmailValidationTokenExpiryInstant(Instant.now().plus(10, ChronoUnit.MINUTES));

        Mockito.doReturn(unconfirmedUser).when(userService).getUserByEmailAndPassword(validEmail, validPassword);

        String resource = loginController.authenticateLogin(validEmail, validPassword, "", model, httpServletRequest);

        Assertions.assertEquals(expectedResource, resource);
    }

    @Test
    public void testWhenIHaveConfirmedEmail_andITryToLogin_IAmSuccessfullyLoggedIn() {
        long id = 1;
        String validFirstName = "jane";
        String validLastName = "doe";
        String validEmail = "jane.doe@mail.com";
        String validPassword = "TESTPassword123!";
        String validDOB = "01/01/2000";
        String expectedResource = "redirect:/users/user";

        GardenUser confirmedUser = new GardenUser(validFirstName, validLastName, validEmail, validPassword, validDOB);
        confirmedUser.setId(id);

        Mockito.doReturn(confirmedUser).when(userService).getUserByEmailAndPassword(validEmail, validPassword);

        String resource = loginController.authenticateLogin(validEmail, validPassword, "", model, httpServletRequest);

        Assertions.assertEquals(expectedResource, resource);
    }
}
