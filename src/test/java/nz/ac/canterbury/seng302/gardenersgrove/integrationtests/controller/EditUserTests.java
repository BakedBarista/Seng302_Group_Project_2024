package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EditUserControllerTest {
    private EditUserController controller;

    private Authentication authentication;
    private Model model;
    private GardenUserService userService;
    private EmailSenderService emailSenderService;

    private String dateValidStr = "";
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        userService = mock(GardenUserService.class);
        emailSenderService = mock(EmailSenderService.class);
        controller = new EditUserController(userService, emailSenderService);
    }

    @Test
    void whenValidEditProfile_redirectToProfilePage() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        when(userService.getUserById(userId)).thenReturn(user); // Mock userService.getUserById(userId)
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("Jane");
        editUser.setLname("Dough");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDateOfBirth("1970-01-01");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        //Edit user details
        String result = controller.submitUser(editUser, bindingResult, authentication, dateValidStr, model);

        assertEquals("redirect:/users/user", result); // Verify that the returned view name is correct
    }

    @Test
    void whenInvalidEditProfile_redirectToEditPage() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("$2Jane");
        editUser.setLname("Dough");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDateOfBirth("1970-01-01");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        //Edit user details
        String result = controller.submitUser(editUser, bindingResult, authentication, dateValidStr, model);

        assertEquals("users/editTemplate", result);
    }

    @Test
    void whenValidEditProfile_submitUser() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com",
                 "P#ssw0rd", LocalDate.of(2000, 10, 10));
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("Jane");
        editUser.setLname("Dough");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDateOfBirth("1970-01-01");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        //Edit user details
        controller.submitUser(editUser, bindingResult, authentication, dateValidStr, model);

        assertEquals("Jane", user.getFname());
        assertEquals("Dough", user.getLname());
        assertEquals("jane@email.com", user.getEmail());
        assertEquals(LocalDate.of(1970, 1, 1), user.getDOB());
    }

    @Test
    void whenNameTooLong_doNotSaveToDB() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        editUser.setLname( "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDateOfBirth("1970-01-01");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.submitUser(editUser, bindingResult, authentication, dateValidStr, model);

        assertEquals("users/editTemplate", result);
        assertEquals("John", user.getFname()); //Checks if first name didn't change because it is not valid
        assertEquals("Doe", user.getLname()); //Checks if last name didn't change because it is not valid
    }

    @Test
    void whenPasswordChanged_sendEmail() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        EditPasswordDTO editPassword = new EditPasswordDTO();
        editPassword.setOldPassword("P#ssw0rd");
        editPassword.setNewPassword("N3wP@ssw0rd");
        editPassword.setConfirmPassword("N3wP@ssw0rd");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        controller.submitPassword(editPassword, bindingResult, authentication, model);

        assertTrue(user.checkPassword("N3wP@ssw0rd"));
        verify(emailSenderService).sendEmail(eq(user), eq("Password Changed"), any());
    }

    @Test
    void givenIChangeDateOfBirthToNull_ThenIDoNotHaveADateOfBirth() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com",
                "P#ssw0rd", LocalDate.of(2000, 10, 10));

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("Jane");
        editUser.setLname("Doe");
        editUser.setNoLname(false);
        editUser.setEmail("john@email.com");
        editUser.setDateOfBirth("");

        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        controller.submitUser(editUser, bindingResult, authentication, dateValidStr, model);

        Assertions.assertNull(user.getDOB());
    }

    @Test
    void givenIEditToANewValidDateOfBirth_ThenIHaveALocalDateOfTheGivenDate() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com",
                "P#ssw0rd", LocalDate.of(2000, 10, 10));

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("John");
        editUser.setLname("Doe");
        editUser.setNoLname(false);
        editUser.setEmail("john@email.com");
        editUser.setDateOfBirth("");
        String dobString = "1998-01-01";
        editUser.setDateOfBirth(dobString);

        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        controller.submitUser(editUser, bindingResult, authentication, dateValidStr, model);

        Assertions.assertEquals(LocalDate.parse(dobString), user.getDOB());
    }

    @Test
    void givenIEditToANewInvalidDateOfBirth_ThenMyDateOfBirthIsNotChanged() {
        LocalDate originalDOB = LocalDate.of(2000, 10, 10);
        GardenUser user = new GardenUser("John", "Doe", "john@email.com",
                "P#ssw0rd", originalDOB);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("John");
        editUser.setLname("Doe");
        editUser.setNoLname(false);
        editUser.setEmail("john@email.com");
        editUser.setDateOfBirth("");
        String dobString = "10/10/2010";
        editUser.setDateOfBirth(dobString);

        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        controller.submitUser(editUser, bindingResult, authentication, dateValidStr, model);

        Assertions.assertEquals(originalDOB, user.getDOB());
    }
}
