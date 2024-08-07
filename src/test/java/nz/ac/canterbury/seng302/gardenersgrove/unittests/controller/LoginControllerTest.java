package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.LoginController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.LoginDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.StrikeService;

import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class LoginControllerTest {

    @Mock
    private GardenUserService userService;

    @Mock
    private StrikeService strikeService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoginController loginController;

    @Spy
    private GardenUser user;

    private String email;

    private String password;

    private long id;

    @BeforeEach
    void setUp() {
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
    void givenIHaveNotConfirmedEmail_whenITryToLogin_thenIAmTakenToTokenConfirmationPage() {
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
    void givenWhenIHaveConfirmedEmail_whenITryToLogin_thenIAmSuccessfullyLoggedIn() {
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

    @Test
    void givenIAmBlockedForThreeDays_whenIAmShownTheBlockedMessage_thenItSaysThreeDays() throws ServletException {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(strikeService.daysUntilUnblocked(user)).thenReturn(3L);

        String result = loginController.blocked(httpServletRequest, authentication, model);

        assertEquals("users/blocked", result);
        verify(model).addAttribute("daysLeft", 3L);
    }
}
