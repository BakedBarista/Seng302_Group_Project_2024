package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EditUserControllerTest {
    private EditUserController controller;

    private Authentication authentication;
    private Model model;
    private GardenUserService userService;
    private EmailSenderService emailSenderService;

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
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");
        when(userService.getUserById(userId)).thenReturn(user); // Mock userService.getUserById(userId)
        when(authentication.getPrincipal()).thenReturn(userId);

        //Edit user details
        String result = controller.submitUser("Jane", "Dough", false, "jane@email.com",
                "01/01/1998", authentication, model);

        assertEquals("redirect:/users/user", result); // Verify that the returned view name is correct
    }

    @Test
    void whenInvalidEditProfile_redirectToEditPage() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        //Edit user details
        String result = controller.submitUser("$2Jane", "Dough", false,
                "jane@email.com", "01/01/1998", authentication, model);

        assertEquals("users/editTemplate", result);
    }

    @Test
    void whenValidEditProfile_submitUser() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com",
                 "P#ssw0rd", "10/10/2000");
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        //Edit user details
        controller.submitUser("Jane", "Dough", false, "jane@email.com", "01/01/1998", authentication, model);

        assertEquals("Jane", user.getFname());
        assertEquals("Dough", user.getLname());
        assertEquals("jane@email.com", user.getEmail());
        assertEquals("01/01/1998", user.getDOB());
    }

    @Test
    void whenNameTooLong_doNotSaveToDB() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        String result = controller.submitUser("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", false, "jane@email.com"
                , "01/01/1998", authentication, model);

        assertEquals("users/editTemplate", result);
        assertEquals("John", user.getFname()); //Checks if first name didn't change because it is not valid
        assertEquals("Doe", user.getLname()); //Checks if last name didn't change because it is not valid

    }

    @Test
    void whenPasswordChanged_sendEmail() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        controller.submitPassword("P#ssw0rd", "N3wP@ssword", "N3wP@ssword", authentication, model);

        verify(emailSenderService, times(1)).sendEmail(eq(user), eq("Password Changed"), any());
    }
}
