package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ResetPasswordController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordCallbackDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.URLService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ResetPasswordControllerTest {

    private HttpServletRequest request;
    private String token;
    private ResetPasswordController controller;
    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;
    private GardenUser user;

    private ResetPasswordDTO resetPasswordDTO;
    private URLService urlService;
    private Model model;

    @BeforeEach
    public void setUp() {
        request = mock(HttpServletRequest.class);
        userService = mock(GardenUserService.class);
        tokenService = mock(TokenService.class);
        emailSenderService = mock(EmailSenderService.class);
        urlService = mock(URLService.class);
        controller = new ResetPasswordController(userService, emailSenderService, tokenService, urlService);

        resetPasswordDTO = mock(ResetPasswordDTO.class);
        token = "abc123xyz";
        user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password", null);
    }


    @Test
    void givenEmailExists_whenResetPasswordRequested_thenEmailIsSent() {
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        String expectedUrl = "https://example.com/prod/users/reset-password/callback?token=abc123xyz";

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(tokenService.createAuthenticationToken()).thenReturn(token);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443); // Standard HTTPS port
        when(request.getContextPath()).thenReturn("/prod"); // Prod context path
        when(resetPasswordDTO.getEmail()).thenReturn(user.getEmail());
        when(urlService.generateResetPasswordUrlString(any(HttpServletRequest.class), anyString())).thenReturn(expectedUrl);

        String result = controller.resetPasswordConfirmation(request, resetPasswordDTO, bindingResult, model);

        assertEquals("users/resetPasswordConfirmation", result);
        verify(emailSenderService).sendEmail(eq(user), any(),
                matches("https://example.com/prod/users/reset-password/callback\\?token=abc123xyz"));

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
