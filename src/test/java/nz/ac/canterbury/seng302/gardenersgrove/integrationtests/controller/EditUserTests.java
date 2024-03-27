package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EditUserControllerTest {
    private EditUserController controller;
    @Mock
    private GardenUserService userService;
    @Mock
    private Long userId = 1L;

    Authentication authentication = mock(Authentication.class);

    Model model = mock(Model.class);
    @BeforeEach
    void setUp() {
        userService = mock(GardenUserService.class);
        controller = new EditUserController();
        controller.setUserService(userService);
    }

    @Test
    void whenValidEditProfile_redirectToProfilePage() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");
        when(userService.getUserById(userId)).thenReturn(user); // Mock userService.getUserById(userId)
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("Jane");
        editUser.setLname("Dough");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDOB("01/01/1998");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        //Edit user details
        String result = controller.submitUser(editUser, bindingResult, authentication, model);

        assertEquals("redirect:/users/user", result); // Verify that the returned view name is correct
    }

    @Test
    void whenInvalidEditProfile_redirectToEditPage() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("$2Jane");
        editUser.setLname("Dough");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDOB("01/01/1998");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        //Edit user details
        String result = controller.submitUser(editUser, bindingResult, authentication, model);

        assertEquals("users/editTemplate", result);
    }

    @Test
    void whenValidEditProfile_submitUser() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com",
                 "P#ssw0rd", "10/10/2000");
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("Jane");
        editUser.setLname("Dough");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDOB("01/01/1998");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        //Edit user details
        controller.submitUser(editUser, bindingResult, authentication, model);

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

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        editUser.setLname( "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDOB("01/01/1998");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.submitUser(editUser, bindingResult, authentication, model);

        assertEquals("users/editTemplate", result);
        assertEquals("John", user.getFname()); //Checks if first name didn't change because it is not valid
        assertEquals("Doe", user.getLname()); //Checks if last name didn't change because it is not valid

    }
}
