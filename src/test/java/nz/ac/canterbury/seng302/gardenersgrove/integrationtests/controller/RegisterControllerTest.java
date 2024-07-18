package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.BindingResult;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

@SpringBootTest
class RegisterControllerTest {

    @Autowired
    private RegisterController registerController;

    @Autowired
    private GardenUserService gardenUserService;

    private GardenUser user;

    @MockBean
    private EmailSenderService emailSenderService;

    @Mock
    private BindingResult bindingResult;

    private RegisterDTO registerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerDTO = new RegisterDTO();
        registerDTO.setFname("John");
        registerDTO.setLname("Doe");
        registerDTO.setPassword("password");
        registerDTO.setConfirmPassword("password");
    }

    @Test
    void givenIRegisterWithNoDateOfBirth_ThenIDoNotHaveADateOfBirth() {
        registerDTO.setEmail("test1@mail.com");
        registerDTO.setDateOfBirth("");

        registerController.submitRegister(registerDTO, bindingResult, null);

        GardenUser user = gardenUserService.getUserByEmail(registerDTO.getEmail());
        Assertions.assertNull(user.getDateOfBirth());
    }

    @Test
    void givenIRegisterWithNoDateOfBirth_ThenIAmRedirectedToAuthenticate() {
        registerDTO.setEmail("test2@mail.com");
        registerDTO.setDateOfBirth("");

        String actualPage = registerController.submitRegister(registerDTO, bindingResult, null);

        String expectedPage = "redirect:/users/user/" + gardenUserService.obfuscateEmail(registerDTO.getEmail()) + "/authenticate-email";
        Assertions.assertEquals(expectedPage, actualPage);
    }

    @Test
    void givenIRegisterWithAValidDateOfBirth_ThenIHaveALocalDateOfTheGivenDate() {
        registerDTO.setEmail("test3@mail.com");
        String dobString = "1998-01-01";
        registerDTO.setDateOfBirth(dobString);

        registerController.submitRegister(registerDTO, bindingResult, null);

        GardenUser user = gardenUserService.getUserByEmail(registerDTO.getEmail());
        Assertions.assertEquals(LocalDate.parse(dobString), user.getDateOfBirth());
    }

    @Test
    void givenIRegisterWithAValidDateOfBirth_ThenIAmRedirectedToAuthenticate() {
        registerDTO.setEmail("test4@mail.com");
        String dobString = "1998-01-01";
        registerDTO.setDateOfBirth(dobString);

        String actualPage = registerController.submitRegister(registerDTO, bindingResult, null);
        String expectedPage = "redirect:/users/user/" + gardenUserService.obfuscateEmail(registerDTO.getEmail()) + "/authenticate-email";

        Assertions.assertEquals(expectedPage, actualPage);
    }

    @Test
    void whenSubmitRegisterIsCalled_thenAnEmailIsSent() {
        // add user to persistence and then call function to add token and time instant
        String firstName = "jane";
        String lastName = "doe";
        String email = "john.doe@mail.com";
        String password = "TESTPassword123!";
        LocalDate dob = LocalDate.of(1998, 1, 1);
        String token = "token";
        user = new GardenUser(firstName, lastName, email, password, dob);
        user.setEmailValidationToken(token);
        gardenUserService.addUser(user);
        registerController.sendRegisterEmail(user, token);

        user = gardenUserService.getUserById(user.getId());
        Mockito.verify(emailSenderService).sendEmail(
                Mockito.assertArg((GardenUser actualUser) -> {
                    Assertions.assertEquals(user.getId(), actualUser.getId());
                }),
                Mockito.eq("Welcome to Gardener's Grove"),
                Mockito.assertArg((message) -> {
                    Assertions.assertTrue(message.contains(token));
                }));
    }
}
