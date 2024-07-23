package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.URLService;

class RegisterControllerUnitTests {
    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;
    private URLService urlService;
    private HttpServletRequest request;
    private BindingResult bindingResult;

    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        userService = mock(GardenUserService.class);
        tokenService = mock(TokenService.class);
        emailSenderService = mock(EmailSenderService.class);
        urlService = mock(URLService.class);
        request = mock(HttpServletRequest.class);
        bindingResult = mock(BindingResult.class);

        registerController = new RegisterController(userService, tokenService, emailSenderService, urlService);
    }

    @Test
    void whenLnameIsBlankAndNoLnameIsTicked_thenNoError() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFname("");
        registerDTO.setLname("");
        registerDTO.setNoLname(true);
        registerDTO.setEmail("");
        registerDTO.setPassword("");
        registerDTO.setConfirmPassword("");

        registerController.submitRegister(request, registerDTO, bindingResult, null);

        verify(bindingResult, times(0)).rejectValue(eq("lname"), eq(null), anyString());
    }

    @Test
    void whenLnameIsBlankAndNoLnameIsNotTicked_thenError() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFname("");
        registerDTO.setLname("");
        registerDTO.setNoLname(false);
        registerDTO.setEmail("");
        registerDTO.setPassword("");
        registerDTO.setConfirmPassword("");

        registerController.submitRegister(request, registerDTO, bindingResult, null);

        verify(bindingResult).rejectValue(eq("lname"), eq(null), anyString());
    }

    @Test
    void whenLnameIsNotBlankAndNoLnameIsTicked_thenError() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFname("");
        registerDTO.setLname("Ceelen");
        registerDTO.setNoLname(true);
        registerDTO.setEmail("");
        registerDTO.setPassword("");
        registerDTO.setConfirmPassword("");

        registerController.submitRegister(request, registerDTO, bindingResult, null);

        verify(bindingResult).rejectValue(eq("lname"), eq(null), anyString());
    }

    void whenUserRegisters_thenEmailIsSentWithTokenAndLink() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFname("John");
        registerDTO.setLname("Doe");
        registerDTO.setNoLname(false);
        registerDTO.setEmail("john.doe@gmail.com");
        registerDTO.setPassword("");
        registerDTO.setConfirmPassword("");

        when(tokenService.createEmailToken()).thenReturn("test-token");
        when(userService.obfuscateEmail("john.doe@gmail.com")).thenReturn("obfuscated-email");
        when(urlService.generateAuthenticateEmailUrlString(request, "obfuscated-email")).thenReturn("https://example.com/");

        registerController.submitRegister(request, registerDTO, bindingResult, null);

        verify(emailSenderService).sendEmail(
                assertArg((GardenUser user) -> {
                    assertEquals("john.doe@gmail.com", user.getEmail());
                }),
                eq("Welcome to Gardener's Grove"),
                assertArg((String body) -> {
                    assertTrue(body.contains("test-token"));
                    assertTrue(body.contains("https://example.com/"));
                }));
    }
}
