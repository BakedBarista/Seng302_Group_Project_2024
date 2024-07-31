package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.PublicProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublicProfileControllerTest {

    @Mock
    private GardenUserService gardenUserService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PublicProfileController publicProfileController;

    private Long loggedInUserId = 1L;

    private GardenUser loggedInUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        loggedInUser = new GardenUser();
        loggedInUser.setId(loggedInUserId);
        loggedInUser.setEmail("logged.in@gmail.com");
        loggedInUser.setFname("Current");
        loggedInUser.setLname("User");
        loggedInUser.setDescription("This is a description");

        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
    }

    @Test
    public void testEditForm() {
        Model model = mock(Model.class);
        String result = publicProfileController.editPublicProfile(authentication, model);

        verify(model).addAttribute("userId", loggedInUserId);
        verify(model).addAttribute(eq("editUserDTO"), any(EditUserDTO.class));

        assertEquals("users/edit-public-profile", result);
    }

    @Test
    public void testSubmitForm_ValidationFailure() {

    }

    @Test
    public void testSubmitForm_ValidationSuccess() {

    }

    @Test
    public void testPublicProfile() {

    }

    @Test
    public void testUpdateUser() {

    }

    @Test
    void testGetProfile_ProfileNotPresent_ReturnsAccessDenied() {

    }

    @Test
    void testAccessEditProfileIfNotOwner_thenAccessDenied() {

    }
}
