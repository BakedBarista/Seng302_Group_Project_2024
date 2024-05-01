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

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ResetPasswordController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordCallbackDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.springframework.web.bind.annotation.ModelAttribute;

public class ResetPasswordControllerTest {

    private HttpServletRequest request;
    private String token;
    private ResetPasswordController controller;
    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;
    private GardenUser user;

    private ResetPasswordDTO resetPasswordDTO;
    private Model model;

    @BeforeEach
    public void setUp() {
        request = mock(HttpServletRequest.class);
        userService = mock(GardenUserService.class);
        tokenService = mock(TokenService.class);
        emailSenderService = mock(EmailSenderService.class);
        controller = new ResetPasswordController(userService, emailSenderService, tokenService);

        resetPasswordDTO = mock(ResetPasswordDTO.class);
        token = "abc123xyz";
        user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password", null);
    }

    @ParameterizedTest
    @CsvSource({
        "https,example.com,8080,https://example.com:8080/users/reset-password/callback?token=abc123xyz",
        "http,example.com,80,http://example.com/users/reset-password/callback?token=abc123xyz",
        "https,example.com,443,https://example.com/users/reset-password/callback?token=abc123xyz",
    })
    void whenGenerateUrlStringCalled_thenUrlIsGenerated(String scheme, String host, int port, String expectedUrl) {
        // Set up the expected behaviors of the mock object
        when(request.getScheme()).thenReturn(scheme);
        when(request.getServerName()).thenReturn(host);
        when(request.getServerPort()).thenReturn(port);

        String url = controller.generateUrlString(request, token);

        assertEquals(expectedUrl, url);
    }

    @Test
    void givenEmailExists_whenResetPasswordRequested_thenEmailIsSent() {
        BindingResult bindingResult = Mockito.mock(BindingResult.class);

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(tokenService.createAuthenticationToken()).thenReturn(token);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443); // Standard HTTPS port
        when(resetPasswordDTO.getEmail()).thenReturn(user.getEmail());

        String result = controller.resetPasswordConfirmation(request, resetPasswordDTO, bindingResult, model);

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

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.resetPasswordConfirmation(request,resetPasswordDTO,bindingResult, model);

        assertEquals("users/resetPasswordConfirmation", result);
        verify(emailSenderService, times(0)).sendEmail(any(GardenUser.class), any(), any());
    }

    @Test
    void givenLinkNotExpired_whenEmailLinkFollowed_thenFormShown() {
        when(userService.getUserByResetPasswordToken(token)).thenReturn(user);

        Model model = mock(Model.class);
        String result = controller.resetPasswordCallback(token, model);

        assertEquals("users/resetPasswordCallback", result);
        verify(model).addAttribute(eq("resetPasswordCallbackDTO"), assertArg((ResetPasswordCallbackDTO resetPasswordCallbackDTO) -> {
            assertEquals(token, resetPasswordCallbackDTO.getToken());
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

        ResetPasswordCallbackDTO resetPasswordDTO = new ResetPasswordCallbackDTO();
        resetPasswordDTO.setToken(token);
        resetPasswordDTO.setNewPassword("newPassword");
        resetPasswordDTO.setConfirmPassword("newPassword");

        BindingResult bindingResult = mock(BindingResult.class);
        Model model = mock(Model.class);
        String result = controller.resetPasswordCallbackPost(resetPasswordDTO, bindingResult, model);

        assertEquals("redirect:/users/login", result);
        verify(userService).addUser(user);
        assertTrue(user.checkPassword("newPassword"));
        verify(emailSenderService).sendEmail(user, "Password Changed", "Your password has been updated");
    }

    @Test
    void givenLinkNotExpired_andFieldsHasErrors_whenFormSubmitted_thenPasswordNotChanged() {
        BindingResult bindingResult = mock(BindingResult.class);
        Model model = mock(Model.class);

        ResetPasswordCallbackDTO resetPasswordDTO = new ResetPasswordCallbackDTO();
        resetPasswordDTO.setToken(token);
        resetPasswordDTO.setNewPassword("newPassword");
        resetPasswordDTO.setConfirmPassword("newWrongPassword");

        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.resetPasswordCallbackPost(resetPasswordDTO, bindingResult, model);
        assertEquals("users/resetPasswordCallback", result);

    }

    @Test
    void givenLinkExpired_whenFormSubmitted_thenRedirectedToLogin() {
        when(userService.getUserByResetPasswordToken(token)).thenReturn(null);

        ResetPasswordCallbackDTO resetPasswordDTO = new ResetPasswordCallbackDTO();
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
