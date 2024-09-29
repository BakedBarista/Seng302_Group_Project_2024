package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;

import java.time.LocalDate;

class RegisterControllerUnitTests {
    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;
    private URLService urlService;
    private BirthFlowerService birthFlowerService;
    private HttpServletRequest request;
    private BindingResult bindingResult;

    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        userService = mock(GardenUserService.class);
        tokenService = mock(TokenService.class);
        emailSenderService = mock(EmailSenderService.class);
        urlService = mock(URLService.class);
        birthFlowerService = mock(BirthFlowerService.class);
        request = mock(HttpServletRequest.class);
        bindingResult = mock(BindingResult.class);

        registerController = new RegisterController(userService, tokenService, emailSenderService, urlService, birthFlowerService);
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

    @Test
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

    @Test
    void whenUserRegistersWithDOB_thenBirthFlowerSet() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFname("John");
        registerDTO.setLname("Doe");
        registerDTO.setNoLname(false);
        registerDTO.setEmail("john.doe@gmail.com");
        registerDTO.setDateOfBirth("2004-12-10");
        registerDTO.setPassword("");
        registerDTO.setConfirmPassword("");

        String expectedFlower = "Holly";

        when(tokenService.createEmailToken()).thenReturn("test-token");
        when(userService.obfuscateEmail("john.doe@gmail.com")).thenReturn("obfuscated-email");
        when(birthFlowerService.getDefaultBirthFlower(LocalDate.of(2004, 12, 10))).thenReturn(expectedFlower);

        registerController.submitRegister(request, registerDTO, bindingResult, null);

        ArgumentCaptor<GardenUser> userCaptor = ArgumentCaptor.forClass(GardenUser.class);
        verify(userService).addUser(userCaptor.capture());

        GardenUser capturedUser = userCaptor.getValue();

        assertNotNull(capturedUser);
        assertEquals(LocalDate.of(2004, 12, 10), capturedUser.getDateOfBirth());
        assertEquals(expectedFlower, capturedUser.getBirthFlower());

        verify(birthFlowerService).getDefaultBirthFlower(LocalDate.of(2004, 12, 10));
    }

    @Test
    void whenUserRegistersWithoutDOB_thenBirthFlowerIsNull() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFname("John");
        registerDTO.setLname("Doe");
        registerDTO.setNoLname(false);
        registerDTO.setEmail("john.doe@gmail.com");
        registerDTO.setPassword("password123");
        registerDTO.setConfirmPassword("password123");

        when(tokenService.createEmailToken()).thenReturn("test-token");
        when(userService.obfuscateEmail("john.doe@gmail.com")).thenReturn("obfuscated-email");
        when(birthFlowerService.getDefaultBirthFlower(null)).thenReturn(null);

        registerController.submitRegister(request, registerDTO, bindingResult, null);

        ArgumentCaptor<GardenUser> userCaptor = ArgumentCaptor.forClass(GardenUser.class);
        verify(userService).addUser(userCaptor.capture());

        GardenUser capturedUser = userCaptor.getValue();

        assertNotNull(capturedUser);
        assertNull(capturedUser.getDateOfBirth());
        assertNull(capturedUser.getBirthFlower());

        verify(birthFlowerService).getDefaultBirthFlower(null);
    }
}
