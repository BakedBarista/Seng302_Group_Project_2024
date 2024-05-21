package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.AuthenticationController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    private GardenUser user;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private GardenUserService userService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password", LocalDate.of(1970, 1, 1));
    }

    @Test
    public void testWhenTokenExistsForUser_UserIsSentToEmailAuthenticationPage() {
        long userId = 1;
        String expectedPage = "authentication/emailAuthentication";
        Instant time = Instant.now();

        user.setEmailValidationToken("000000");
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.getUserById(userId)).thenReturn(user);
        String actualPage = authenticationController.authenticateEmail(userId, model);

        Assertions.assertEquals(expectedPage, actualPage);
    }

    @Test
    public void testWhenTokenDoesNotExistsForUser_UserIsNotSentToEmailAuthenticationPage() {
        long userId = 1;
        String notExpectedPage = "authentication/emailAuthentication";
        // may change at later date
        String expectedPage = "error/404";

        // explicitly setting null here
        user.setEmailValidationToken(null);
        user.setEmailValidationTokenExpiryInstant(null);

        when(userService.getUserById(userId)).thenReturn(user);
        String actualPage = authenticationController.authenticateEmail(userId, model);

        assertNotEquals(notExpectedPage, actualPage);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    public void testWhenUserGivesCorrectToken_UserIsTakenToProfilePage() {
        long userId = 1;
        String expectedPage = "redirect:/users/login";
        String token = "000000";
        Instant time = Instant.now();

        user.setEmailValidationToken(token);
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.getUserById(userId)).thenReturn(user);
        String actualPage = authenticationController.validateAuthenticationToken(userId, token, redirectAttributes, model);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    public void testWhenUserGivesIncorrectToken_UserIsTakenBackToEmailAuthenticationPage() {
        long userId = 1;
        String expectedPage = "authentication/emailAuthentication";
        String userInputtedToken = "000000";
        String storedToken = "000001";
        Instant time = Instant.now();

        user.setEmailValidationToken(storedToken);
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.getUserById(userId)).thenReturn(user);
        String actualPage = authenticationController.validateAuthenticationToken(userId, userInputtedToken, redirectAttributes, model);

        verify(model).addAttribute("tokenIncorrect", true);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    public void testWhenUserGivesCorrectToken_TokenAndTimeInstantRemovedFromUserEntity() {
        long userId = 1;
        String token = "000000";
        Instant time = Instant.now();

        user.setEmailValidationToken(token);
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.getUserById(userId)).thenReturn(user);
        authenticationController.validateAuthenticationToken(userId, token, redirectAttributes, model);

        GardenUser user = userService.getUserById(userId);
        assertNull(user.getEmailValidationToken());
        assertNull(user.getEmailValidationTokenExpiryInstant());
    }

    @Test
    public void testWhenUserGivesIncorrectToken_TokenAndTimeInstantPersist() {
        long userId = 1;
        String userInputtedToken = "000000";
        String storedToken = "000001";
        Instant time = Instant.now();

        user.setEmailValidationToken(storedToken);
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.getUserById(userId)).thenReturn(user);
        authenticationController.validateAuthenticationToken(userId, userInputtedToken, redirectAttributes, model);

        verify(model).addAttribute("tokenIncorrect", true);
        assertEquals(storedToken, user.getEmailValidationToken());
        assertEquals(time, user.getEmailValidationTokenExpiryInstant());
    }

    @Test
    public void testWhenTokenExpired_AndUserInputsAnyToken_UserIsInformedOfTokenExpiration() {
        long userId = 1;
        String userInputtedToken = "000000";
        String expectedPage = "authentication/emailAuthentication";

        // mock that the user was deleted
        when(userService.getUserById(userId)).thenReturn(null);
        String actualPage = authenticationController.validateAuthenticationToken(userId, userInputtedToken, redirectAttributes, model);

        // check that the tokenExpired attribute was added to the model
        verify(model).addAttribute("tokenExpired", true);
        assertEquals(expectedPage, actualPage);
    }

}
