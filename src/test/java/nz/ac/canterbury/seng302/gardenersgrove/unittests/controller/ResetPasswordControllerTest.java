package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ResetPasswordController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;

public class ResetPasswordControllerTest {

    private HttpServletRequest request;
    private String token;
    private ResetPasswordController controller;
    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;
    private GardenUser user;

    @BeforeEach
    public void setUp() {
        request = mock(HttpServletRequest.class);
        userService = mock(GardenUserService.class);
        tokenService = mock(TokenService.class);
        emailSenderService = mock(EmailSenderService.class);
        controller = new ResetPasswordController(userService, emailSenderService, tokenService);

        token = "abc123xyz";
        user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password", null);
    }

    @Test
    void whenGenerateUrlStringCalled_thenUrlIsGenerated() {
        // Set up the expected behaviors of the mock object
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(8080); // Non-standard port

        String url = controller.generateUrlString(request, token);

        assertEquals("https://example.com:8080/users/reset-password/callback?token=abc123xyz", url);
    }

    @Test
    void givenPort80InUse_whenGenerateUrlStringCalled_thenUrlOmitsPort() {
        // Testing with standard HTTP port
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(80); // Standard HTTP port

        String url = controller.generateUrlString(request, token);

        assertEquals("http://example.com/users/reset-password/callback?token=abc123xyz", url);
    }

    @Test
    void givenPort443InUse_whenGenerateUrlStringCalled_thenUrlOmitsPort() {
        // Testing with standard HTTPS port
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443); // Standard HTTPS port

        String url = controller.generateUrlString(request, token);

        assertEquals("https://example.com/users/reset-password/callback?token=abc123xyz", url);
    }

    @Test
    void givenEmailExists_whenResetPasswordRequested_thenEmailIsSent() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(tokenService.createAuthenticationToken()).thenReturn(token);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443); // Standard HTTPS port

        String result = controller.resetPasswordConfirmation(user.getEmail(), request);

        assertEquals("users/resetPasswordConfirmation", result);
        verify(emailSenderService).sendEmail(eq(user), any(),
                matches("https://example.com/users/reset-password/callback\\?token=abc123xyz"));
    }

    @Test
    void givenEmailDoesntExist_whenResetPasswordRequested_thenEmailIsNotSent() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(tokenService.createAuthenticationToken()).thenReturn(token);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443); // Standard HTTPS port

        String result = controller.resetPasswordConfirmation("noreply@example.com", request);

        assertEquals("users/resetPasswordConfirmation", result);
        verify(emailSenderService, times(0)).sendEmail(any(GardenUser.class), any(), any());
    }

    @Test
    void givenLinkNotExpired_whenEmailLinkFollowed_thenFormShown() {
        when(userService.getUserByResetPasswordToken(token)).thenReturn(user);

        Model model = mock(Model.class);
        String result = controller.resetPasswordCallback(token, model);

        assertEquals("users/resetPasswordCallback", result);
        verify(model).addAttribute(eq("resetPasswordDTO"), assertArg((ResetPasswordDTO resetPasswordDTO) -> {
            assertEquals(token, resetPasswordDTO.getToken());
        }));
    }

    @Test
    void givenLinkExpired_whenEmailLinkFollowed_thenRedirectedToLogin() {
        when(userService.getUserByResetPasswordToken(token)).thenReturn(null);

        Model model = mock(Model.class);
        String result = controller.resetPasswordCallback(token, model);

        assertEquals("redirect:/users/login?error=resetPasswordLinkExpired", result);
    }

    @Test
    void givenLinkNotExpired_whenFormSubmitted_thenPasswordChanged() {
        when(userService.getUserByResetPasswordToken(token)).thenReturn(user);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setToken(token);
        resetPasswordDTO.setNewPassword("newPassword");
        resetPasswordDTO.setConfirmPassword("newPassword");

        BindingResult bindingResult = mock(BindingResult.class);
        Model model = mock(Model.class);
        String result = controller.resetPasswordCallbackPost(resetPasswordDTO, bindingResult, model);

        assertEquals("redirect:/users/login", result);
        verify(userService).addUser(eq(user));
        assertTrue(user.checkPassword("newPassword"));
        verify(emailSenderService).sendEmail(eq(user), eq("Password Changed"), eq("Your password has been updated"));
    }

    @Test
    void givenLinkExpired_whenFormSubmitted_thenRedirectedToLogin() {
        when(userService.getUserByResetPasswordToken(token)).thenReturn(null);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setToken(token);
        resetPasswordDTO.setNewPassword("newPassword");
        resetPasswordDTO.setConfirmPassword("newPassword");

        BindingResult bindingResult = mock(BindingResult.class);
        Model model = mock(Model.class);
        String result = controller.resetPasswordCallbackPost(resetPasswordDTO, bindingResult, model);

        assertEquals("redirect:/users/login?error=resetPasswordLinkExpired", result);
        verify(userService, times(0)).addUser(any());
    }
}
