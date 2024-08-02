package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.PublicProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class PublicProfileControllerTest {


    private static PublicProfileController publicProfileController;
    private static GardenUserService gardenUserService;
    private static GardenUser user;
    private static Model model;
    private static Authentication authentication;
    private static Long userId;

    static Long loggedInUserId = 1L;
    static GardenUser loggedInUser;

    @BeforeAll
    static void setup() {
        userId = 1L;
        gardenUserService = Mockito.mock(GardenUserService.class);
        authentication = Mockito.mock(Authentication.class);
        user = new GardenUser();
        publicProfileController = new PublicProfileController(gardenUserService);

        loggedInUser = new GardenUser();
        loggedInUser.setId(loggedInUserId);
        loggedInUser.setEmail("logged.in@gmail.com");
        loggedInUser.setFname("Current");
        loggedInUser.setLname("User");
        loggedInUser.setDescription("This is a description");
        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"I love plants", "", "    "})
    @NullSource
    void givenThatIHaveANameAndDescription_whenIViewMyPublicProfile_thenMyNameAndDescriptionAreInTheModel(String description) {
        model = Mockito.mock(Model.class);
        user.setFname("John");
        user.setLname("Doe");
        user.setDescription(description);
        Mockito.when(authentication.getPrincipal()).thenReturn(userId);
        Mockito.when(gardenUserService.getUserById(1L)).thenReturn(user);

        publicProfileController.viewPublicProfile(authentication, model);

        Mockito.verify(model).addAttribute("userId", userId);
        Mockito.verify(model).addAttribute("name", "John Doe");
        Mockito.verify(model).addAttribute("description", description);
    }

    @Test
    void whenIViewMyPublicProfile_thenIAmTakenToThePublicProfilePage() {
        model = Mockito.mock(Model.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userId);
        Mockito.when(gardenUserService.getUserById(userId)).thenReturn(user);

        String page = publicProfileController.viewPublicProfile(authentication, model);

        Assertions.assertEquals("users/public-profile", page);
    }

    @Test
    void givenThatIHaveABanner_whenIRequestMyBanner_thenMyBannerIsSentInAResponseEntity() {
        String path = "/my/path";
        user.setProfileBanner(null, null);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getContextPath()).thenReturn(path);
        Mockito.when(gardenUserService.getUserById(userId)).thenReturn(user);

        ResponseEntity<byte[]> bannerContent = publicProfileController.getPublicProfileBanner(userId, request);

        Assertions.assertEquals(path + "/img/default-banner.svg", bannerContent.getHeaders().getLocation().toString());
    }

    @Test
    void givenThatIHaveNoBanner_whenIRequestMyBanner_thenADefaultBannerIsSentInAResponseEntity() {
        byte[] imageBytes = "image".getBytes();
        user.setProfileBanner("img/png", imageBytes);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(gardenUserService.getUserById(userId)).thenReturn(user);

        ResponseEntity<byte[]> bannerContent = publicProfileController.getPublicProfileBanner(userId, request);

        Assertions.assertEquals(imageBytes, bannerContent.getBody());
    }

@Test
public void testEditForm() {
    Model model = mock(Model.class);
    when(authentication.getPrincipal()).thenReturn(loggedInUserId);
    String result = publicProfileController.editPublicProfile(authentication, model);

    verify(model).addAttribute("userId", loggedInUserId);
    verify(model).addAttribute(eq("editUserDTO"), any(EditUserDTO.class));

    assertEquals("users/edit-public-profile", result);
}
//
//    // @Test
//    // public void testSubmitForm_ValidationFailure() throws IOException {
//    //     // Arrange
//    //     String invalidDescription = "";  // Assume empty description is invalid
//    //     MultipartFile profilePic = mock(MultipartFile.class);
//    //     MultipartFile banner = mock(MultipartFile.class);
//
//    //     doThrow(new IOException("Simulated IOException")).when(publicProfileController).editProfilePicture(loggedInUserId, profilePic);
//
//    //     // Act
//    //     String viewName = publicProfileController.publicProfileEditSubmit(
//    //         authentication,
//    //         profilePic,
//    //         banner,
//    //         invalidDescription,
//    //         mock(Model.class)
//    //     );
//
//    //     // Assert
//    //     assertEquals("redirect:/users/public-profile", viewName);
//    // }
//
@Test
public void testSubmitForm_ValidationSuccess() throws IOException {
    Model model = mock(Model.class);

    MultipartFile profilePic = new MockMultipartFile(
        "image",
        "profile.png",
        "image/png",
        "profile picture content".getBytes()
    );

    String description = "New Description";

    MultipartFile banner = new MockMultipartFile(
        "bannerImage",
        "banner.png",
        "image/png",
        "banner content".getBytes()
    );

    String viewName = publicProfileController.publicProfileEditSubmit(authentication, profilePic, banner, description, model);

    verify(gardenUserService).setProfilePicture(loggedInUserId, profilePic.getContentType(), profilePic.getBytes());
    verify(gardenUserService).setProfileBanner(loggedInUserId, banner.getContentType(), banner.getBytes());
    verify(model).addAttribute("userId", loggedInUserId);

    assertEquals(description, loggedInUser.getDescription());
    assertEquals("redirect:/users/public-profile", viewName);
}
}
