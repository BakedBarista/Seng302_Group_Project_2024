package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.logging.Logger;

class EditUserControllerTest {

    @Mock
    private Logger logger;
    
    private EditUserController controller;

    private Authentication authentication;
    private Model model;
    private GardenUserService userService;
    private EmailSenderService emailSenderService;

    private GardenUserRepository gardenUserRepository;
    private MultipartFile file;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        gardenUserRepository = mock(GardenUserRepository.class);
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        userService = mock(GardenUserService.class);
        emailSenderService = mock(EmailSenderService.class);
        controller = new EditUserController(userService, emailSenderService);
        file = new MockMultipartFile("file", "filename.txt", "text/plain", "Some file content".getBytes());
    }

    @Test
    void whenValidEditProfile_redirectToProfilePage() throws IOException {
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
        String result = controller.submitUser(editUser, file, bindingResult, authentication, model);

        assertEquals("redirect:/users/user", result); // Verify that the returned view name is correct
    }

    @Test
    void whenInvalidEditProfile_redirectToEditPage() throws IOException {
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
        String result = controller.submitUser(editUser,file,  bindingResult, authentication, model);

        assertEquals("users/editTemplate", result);
    }

    @Test
    void whenValidEditProfile_submitUser() throws IOException {
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
        controller.submitUser(editUser,file, bindingResult, authentication, model);

        assertEquals("Jane", user.getFname());
        assertEquals("Dough", user.getLname());
        assertEquals("jane@email.com", user.getEmail());
        assertEquals("01/01/1998", user.getDOB());
    }

    @Test
    void whenNameTooLong_doNotSaveToDB() throws IOException {
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

        String result = controller.submitUser(editUser,file, bindingResult, authentication, model);

        assertEquals("users/editTemplate", result);
        assertEquals("John", user.getFname()); //Checks if first name didn't change because it is not valid
        assertEquals("Doe", user.getLname()); //Checks if last name didn't change because it is not valid

    }

    @Test
    void whenPasswordChanged_sendEmail() {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");
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
    void whenIOExceptionThenLogError() throws IOException {
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain",
                "Some file content".getBytes());
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");

        MockMultipartFile spyFile = spy(file);
        doThrow(new IOException("Simulation")).when(spyFile).getBytes();

        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);
        EditUserController controller = new EditUserController(userService, emailSenderService);
        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        editUser.setLname( "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDOB("01/01/1998");

        BindingResult bindingResult = mock(BindingResult.class);

        try {
            controller.submitUser(editUser, spyFile, bindingResult, authentication, model);
            fail("Expected IOException to be thrown, but nothing was thrown");
        } catch (IOException e) {
            System.out.println("IOException was thrown as expected");
        }
    }
}
