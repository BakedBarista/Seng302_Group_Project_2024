package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;

class RegisterControllerUnitTests {
    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;
    private BindingResult bindingResult;

    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        userService = mock(GardenUserService.class);
        tokenService = mock(TokenService.class);
        emailSenderService = mock(EmailSenderService.class);
        bindingResult = mock(BindingResult.class);

        registerController = new RegisterController(userService, tokenService, emailSenderService);
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

        registerController.submitRegister(registerDTO, bindingResult, null, null);

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

        registerController.submitRegister(registerDTO, bindingResult, null, null);

        verify(bindingResult).rejectValue(eq("lname"), eq(null), anyString());
    }
}
