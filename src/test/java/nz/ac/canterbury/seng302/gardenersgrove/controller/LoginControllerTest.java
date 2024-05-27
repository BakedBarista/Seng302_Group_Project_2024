package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.LoginController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.LoginDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.time.LocalDate;
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

    @Spy
    private GardenUser user;

    private String email;

    private String password;

    private long id;

    @BeforeEach
    public void setUp() {
        id = 1;
        String firstName = "jane";
        String lastName = "doe";
        email = "jane.doe@mail.com";
        password = "TESTPassword123!";
        LocalDate dob = LocalDate.of(1970, 1, 1);
        user = new GardenUser(firstName, lastName, email, password, dob);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testWhenIHaveNotConfirmedEmail_andITryToLogin_IAmTakenToTokenConfirmationPage() {
        String expectedResource = "redirect:/users/user/"+id+"/authenticateEmail";

        user.setEmailValidationToken("123456");
        user.setEmailValidationTokenExpiryInstant(Instant.now().plus(10, ChronoUnit.MINUTES));

        Mockito.doReturn(id).when(user).getId();
        Mockito.doReturn(user).when(userService).getUserByEmailAndPassword(email, password);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        String resource = loginController.authenticateLogin(loginDTO, bindingResult, model, httpServletRequest);

        Assertions.assertEquals(expectedResource, resource);
    }

    @Test
    public void testWhenIHaveConfirmedEmail_andITryToLogin_IAmSuccessfullyLoggedIn() {
        String expectedResource = "redirect:/";

        Mockito.doReturn(user).when(userService).getUserByEmailAndPassword(email, password);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        String resource = loginController.authenticateLogin(loginDTO, bindingResult, model, httpServletRequest);

        Assertions.assertEquals(expectedResource, resource);
    }
}
