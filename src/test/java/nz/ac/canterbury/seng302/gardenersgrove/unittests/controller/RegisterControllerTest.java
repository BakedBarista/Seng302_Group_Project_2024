package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
public class RegisterControllerTest {

    @Mock
    private GardenUserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GardenUserService userController;

    private RegisterDTO registerDTO;

    @BeforeEach
    public void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setFname("John");
        registerDTO.setLname("Doe");
        registerDTO.setEmail("john.doe@example.com");
        registerDTO.setPassword("password");
        registerDTO.setConfirmPassword("password");
        registerDTO.setDateOfBirth("15-05-1990");
    }

//    @Test
//    public void testEmailAlreadyInUse() {
//        when(userService.getUserByEmail(registerDTO.getEmail())).thenReturn(new GardenUser());
//
//        String view = userController.submitRegister(registerDTO, bindingResult, null);
//
//        verify(bindingResult).rejectValue("email", null, "This email address is already in use");
//        assertThat(view).isEqualTo("users/registerTemplate");
//    }
//
//    @Test
//    public void testDOBParsing() {
//        when(userService.getUserByEmail(registerDTO.getEmail())).thenReturn(null);
//
//        String view = userController.submitRegister(registerDTO, bindingResult, null);
//
//        verify(bindingResult, never()).rejectValue(eq("DOB"), any(), anyString());
//        assertThat(view).isNotEqualTo("users/registerTemplate");
//    }
}