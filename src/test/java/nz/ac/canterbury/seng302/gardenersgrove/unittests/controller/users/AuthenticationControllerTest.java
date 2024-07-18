package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.AuthenticationController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

class AuthenticationControllerTest {

    private static final String OBFUSCATED_EMAIL = "obfuscated-email";

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
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password", LocalDate.of(1970, 1, 1));
    }

    @Test
    void testWhenTokenExistsForUser_UserIsSentToEmailAuthenticationPage() {
        String expectedPage = "authentication/emailAuthentication";
        Instant time = Instant.now();

        user.setEmailValidationToken("000000");
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        String actualPage = authenticationController.authenticateEmail(OBFUSCATED_EMAIL, model);

        Assertions.assertEquals(expectedPage, actualPage);
    }

    @Test
    void whenTokenDoesNotExistsForUser_thenErrorIsShown() {
        String expectedPage = "authentication/emailAuthentication";

        // explicitly setting null here
        user.setEmailValidationToken(null);
        user.setEmailValidationTokenExpiryInstant(null);

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        String actualPage = authenticationController.authenticateEmail(OBFUSCATED_EMAIL, model);

        assertEquals(expectedPage, actualPage);
        verify(model).addAttribute("tokenExpired", true);
    }

    @Test
    void whenUserHasBeenDeleted_thenErrorIsShown() {
        String expectedPage = "authentication/emailAuthentication";

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        String actualPage = authenticationController.authenticateEmail(OBFUSCATED_EMAIL, model);

        assertEquals(expectedPage, actualPage);
        verify(model).addAttribute("tokenExpired", true);
    }

    @Test
    void whenUserGivesCorrectToken_thenUserIsTakenToLoginPage() {
        String expectedPage = "redirect:/users/login";
        String token = "000000";
        Instant time = Instant.now();

        user.setEmailValidationToken(token);
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        String actualPage = authenticationController.validateAuthenticationToken(OBFUSCATED_EMAIL, token, redirectAttributes, model);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void whenUserGivesIncorrectToken_thenUserIsTakenBackToEmailAuthenticationPage() {
        String expectedPage = "authentication/emailAuthentication";
        String userInputtedToken = "000000";
        String storedToken = "000001";
        Instant time = Instant.now();

        user.setEmailValidationToken(storedToken);
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        String actualPage = authenticationController.validateAuthenticationToken(OBFUSCATED_EMAIL, userInputtedToken, redirectAttributes, model);

        verify(model).addAttribute("tokenIncorrect", true);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void whenUserGivesCorrectToken_thenTokenAndTimeInstantRemovedFromUserEntity() {
        String token = "000000";
        Instant time = Instant.now();

        user.setEmailValidationToken(token);
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        authenticationController.validateAuthenticationToken(OBFUSCATED_EMAIL, token, redirectAttributes, model);

        assertNull(user.getEmailValidationToken());
        assertNull(user.getEmailValidationTokenExpiryInstant());
    }

    @Test
    void whenUserGivesIncorrectToken_thenTokenAndTimeInstantPersist() {
        String userInputtedToken = "000000";
        String storedToken = "000001";
        Instant time = Instant.now();

        user.setEmailValidationToken(storedToken);
        user.setEmailValidationTokenExpiryInstant(time);

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        authenticationController.validateAuthenticationToken(OBFUSCATED_EMAIL, userInputtedToken, redirectAttributes, model);

        verify(model).addAttribute("tokenIncorrect", true);
        assertEquals(storedToken, user.getEmailValidationToken());
        assertEquals(time, user.getEmailValidationTokenExpiryInstant());
    }

    @Test
    void whenTokenExpired_AndUserInputsAnyToken_thenUserIsInformedOfTokenExpiration() {
        String userInputtedToken = "000000";
        String expectedPage = "authentication/emailAuthentication";

        // mock that the user was deleted
        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(null);
        String actualPage = authenticationController.validateAuthenticationToken(OBFUSCATED_EMAIL, userInputtedToken, redirectAttributes, model);

        // check that the tokenExpired attribute was added to the model
        verify(model).addAttribute("tokenExpired", true);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void whenUserWithOneCharEmailGivesCorrectToken_thenUserIsAuthenticated() {
        GardenUser specialUser = new GardenUser("John", "Doe", "j@gmail.com", "password", LocalDate.of(2001, 1, 1));
        String expectedPage = "redirect:/users/login";
        String token = "000000";
        Instant time = Instant.now();

        specialUser.setEmailValidationToken(token);
        specialUser.setEmailValidationTokenExpiryInstant(time);

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(specialUser.getEmail());
        when(userService.getUserByEmail(specialUser.getEmail())).thenReturn(specialUser);
        String actualPage = authenticationController.validateAuthenticationToken(OBFUSCATED_EMAIL, token, redirectAttributes, model);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void whenUserWithTwoCharEmailGivesCorrectToken_thenUserIsAuthenticated() {
        GardenUser specialUser = new GardenUser("John", "Doe", "j@gmail.com", "password", LocalDate.of(2001, 1, 1));
        String expectedPage = "redirect:/users/login";
        String token = "000000";
        Instant time = Instant.now();

        specialUser.setEmailValidationToken(token);
        specialUser.setEmailValidationTokenExpiryInstant(time);

        when(userService.deobfuscateEmail(OBFUSCATED_EMAIL)).thenReturn(specialUser.getEmail());
        when(userService.getUserByEmail(specialUser.getEmail())).thenReturn(specialUser);
        String actualPage = authenticationController.validateAuthenticationToken(OBFUSCATED_EMAIL, token, redirectAttributes, model);

        assertEquals(expectedPage, actualPage);
    }

}
