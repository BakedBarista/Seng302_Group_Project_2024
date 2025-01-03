package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.BirthFlowerService;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EditUserControllerTest {

    @Mock
    private Logger logger;
    
    private EditUserController controller;

    private Authentication authentication;
    private Model model;
    private GardenUserService userService;
    private EmailSenderService emailSenderService;
    private BindingResult bindingResult;

    private BirthFlowerService birthFlowerService;
    private MultipartFile file;
    private MultipartFile multipartFileMock;
    private Long userId = 1L;

    private String dateValidStr = "";

    @BeforeEach
    void setUp() {
        birthFlowerService = mock(BirthFlowerService.class);
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        bindingResult = mock(BindingResult.class);
        userService = mock(GardenUserService.class);
        emailSenderService = mock(EmailSenderService.class);
        controller = new EditUserController(userService, emailSenderService, birthFlowerService);
        multipartFileMock = mock(MultipartFile.class);

        file = new MockMultipartFile("file", "filename.txt", "text/plain", "Some file content".getBytes());
    }

    @Test
    void whenValidEditProfile_redirectToProfilePage() throws IOException {
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
        String result = controller.submitUser(editUser, bindingResult, file, authentication, dateValidStr, model);

        assertEquals("redirect:/users/settings", result); // Verify that the returned view name is correct
    }

    @Test
    void whenInvalidEditProfile_redirectToEditPage() throws IOException {
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
        String result = controller.submitUser(editUser, bindingResult, file, authentication, dateValidStr, model);

        assertEquals("users/editTemplate", result);
    }

    @Test
    void whenValidEditProfile_submitUser() throws IOException {
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
        controller.submitUser(editUser,bindingResult, file, authentication, dateValidStr, model);

        assertEquals("Jane", user.getFname());
        assertEquals("Dough", user.getLname());
        assertEquals("jane@email.com", user.getEmail());
        assertEquals(LocalDate.of(1970, 1, 1), user.getDateOfBirth());
    }

    @Test
    void whenNameTooLong_doNotSaveToDB() throws IOException {
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

        String result = controller.submitUser(editUser,bindingResult, file, authentication, dateValidStr, model);

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
    void whenIOExceptionThenLogError() throws IOException {
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain",
                "Some file content".getBytes());
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));

        MockMultipartFile spyFile = spy(file);
        doThrow(new IOException("Simulation")).when(spyFile).getBytes();

        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);
        EditUserController controller = new EditUserController(userService, emailSenderService, birthFlowerService);
        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        editUser.setLname( "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");

        BindingResult bindingResult = mock(BindingResult.class);

        try {
            controller.submitUser(editUser,bindingResult,  spyFile, authentication, dateValidStr, model);
            fail("Expected IOException to be thrown, but nothing was thrown");
        } catch (IOException e) {
            System.out.println("IOException was thrown as expected");
        }
    }

    @Test
    void whenNoLnameChecked_lnameIsDisabled() throws IOException {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("Jane");
        editUser.setLname("");
        editUser.setNoLname(true);
        editUser.setEmail("jane@email.com");
        editUser.setDateOfBirth("1970-01-01");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.submitUser(editUser, bindingResult, file, authentication, dateValidStr, model);
        assertEquals("redirect:/users/settings", result);
        assertTrue(editUser.isNoLname());
        assertNull(editUser.getLname());
        assertNull(user.getLname());
    }

    @Test
    void whenNoLnameUnchecked_lnameIsEnabled() throws IOException {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
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

        String result = controller.submitUser(editUser, bindingResult, file, authentication, dateValidStr, model);

        assertEquals("redirect:/users/settings", result);
        assertFalse(editUser.isNoLname());
        assertEquals("Dough", editUser.getLname());
        assertEquals("Dough", user.getLname());
    }

    @Test
    void whenLnameIsEmpty_lnameIsRejected() throws IOException {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        when(userService.getUserById(userId)).thenReturn(user);
        when(authentication.getPrincipal()).thenReturn(userId);

        EditUserDTO editUser = new EditUserDTO();
        editUser.setFname("Jane");
        editUser.setLname("");
        editUser.setNoLname(false);
        editUser.setEmail("jane@email.com");
        editUser.setDateOfBirth("1970-01-01");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.submitUser(editUser, bindingResult, file, authentication, dateValidStr, model);

        verify(bindingResult).rejectValue("lname", null, "Last name cannot be empty");
        assertEquals("users/editTemplate", result);
    }

    @Test
    void whenLnameIsNull_lnameIsDisabled(){
        GardenUser user = new GardenUser("John", null, "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        when(authentication.getPrincipal()).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(user);
        String result = controller.edit(authentication, model);
        verify(authentication, times(1)).getPrincipal();
        verify(userService, times(1)).getUserById(userId);
        verify(model, times(1)).addAttribute("userId", userId);
        ArgumentCaptor<EditUserDTO> captor = ArgumentCaptor.forClass(EditUserDTO.class);
        verify(model, times(1)).addAttribute(eq("editUserDTO"), captor.capture());

        EditUserDTO editUserDTO = captor.getValue();
        assertEquals("John", editUserDTO.getFname());
        assertNull(editUserDTO.getLname());
        assertTrue(editUserDTO.isNoLname());
        assertEquals("john@email.com", editUserDTO.getEmail());
        assertEquals("2000-10-10", editUserDTO.getDateOfBirth());

        assertEquals("users/editTemplate", result);
    }

    @Test
    void submitUser_whenDOBIsNull_thenBirthFlowerIsRemoved() throws IOException {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));

        EditUserDTO editUserDTO = new EditUserDTO();
        editUserDTO.setFname("John");
        editUserDTO.setLname("Doe");
        editUserDTO.setEmail("john.doe@example.com");
        editUserDTO.setDateOfBirth(null);

        when(authentication.getPrincipal()).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.addUser(any(GardenUser.class))).thenReturn(user);

        String viewName = controller.submitUser(
                editUserDTO,
                bindingResult,
                multipartFileMock,
                authentication,
                null,
                model
        );

        // Capture the GardenUser passed to addUser
        ArgumentCaptor<GardenUser> userCaptor = ArgumentCaptor.forClass(GardenUser.class);
        verify(userService, times(1)).addUser(userCaptor.capture());
        GardenUser updatedUser = userCaptor.getValue();

        // Check that dateOfBirth and birthFlower are null
        assertNull(updatedUser.getDateOfBirth(), "Date of Birth should be null");
        assertNull(updatedUser.getBirthFlower(), "Birth Flower should be null");

        // Check that the right html page is returned
        assertEquals("redirect:/users/settings", viewName);
    }

    @Test
    void submitUser_whenDOBIsChanged_thenBirthFlowerIsUpdated() throws IOException {
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        String dob = "2000-01-01";
        String birthFlower = "Carnation";

        EditUserDTO editUserDTO = new EditUserDTO();
        editUserDTO.setFname("John");
        editUserDTO.setLname("Doe");
        editUserDTO.setEmail("john.doe@example.com");
        editUserDTO.setDateOfBirth(dob);

        when(authentication.getPrincipal()).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.addUser(any(GardenUser.class))).thenReturn(user);

        user.setBirthFlower(birthFlowerService.getDefaultBirthFlower(user.getDateOfBirth()));
        when(birthFlowerService.getDefaultBirthFlower(any(LocalDate.class))).thenReturn(birthFlower);

        String viewName = controller.submitUser(
                editUserDTO,
                bindingResult,
                multipartFileMock,
                authentication,
                null,
                model
        );

        // Capture the GardenUser passed to addUser
        ArgumentCaptor<GardenUser> userCaptor = ArgumentCaptor.forClass(GardenUser.class);
        verify(userService, times(1)).addUser(userCaptor.capture());
        GardenUser updatedUser = userCaptor.getValue();

        // Check that dateOfBirth and birthFlower are null
        assertEquals(LocalDate.parse(dob), updatedUser.getDateOfBirth());
        assertEquals(birthFlower, updatedUser.getBirthFlower());

        // Check that the right html page is returned
        assertEquals("redirect:/users/settings", viewName);
    }
}
