package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PublicProfileControllerTest {

//    @Mock
//    private GardenUserService gardenUserService;
//
//    @Mock
//    private Authentication authentication;
//
//    @InjectMocks
//    private PublicProfileController publicProfileController;
//
//    Long loggedInUserId = 1L;
//
//    GardenUser loggedInUser;
//
//    @BeforeEach
//    public void setUp() {
//        loggedInUser = new GardenUser();
//        loggedInUser.setId(loggedInUserId);
//        loggedInUser.setEmail("logged.in@gmail.com");
//        loggedInUser.setFname("Current");
//        loggedInUser.setLname("User");
//        loggedInUser.setDescription("This is a description");
//        when(gardenUserService.getUserById(loggedInUserId)).thenReturn(loggedInUser);
//        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
//    }
//
//    @Test
//    public void testEditForm() {
//        Model model = mock(Model.class);
//        when(authentication.getPrincipal()).thenReturn(loggedInUserId);
//        String result = publicProfileController.editPublicProfile(authentication, model);
//
//        verify(model).addAttribute("userId", loggedInUserId);
//        verify(model).addAttribute(eq("editUserDTO"), any(EditUserDTO.class));
//
//        assertEquals("users/edit-public-profile", result);
//    }
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
//    @Test
//    public void testSubmitForm_ValidationSuccess() throws IOException {
//        Model model = mock(Model.class);
//
//        MultipartFile profilePic = new MockMultipartFile(
//            "image",
//            "profile.png",
//            "image/png",
//            "profile picture content".getBytes()
//        );
//
//        String description = "New Description";
//
//        MultipartFile banner = new MockMultipartFile(
//            "bannerImage",
//            "banner.png",
//            "image/png",
//            "banner content".getBytes()
//        );
//
//        String viewName = publicProfileController.publicProfileEditSubmit(authentication, profilePic, banner, description, model);
//
//        verify(gardenUserService).setProfilePicture(loggedInUserId, profilePic.getContentType(), profilePic.getBytes());
//        verify(gardenUserService).setProfileBanner(loggedInUserId, banner.getContentType(), banner.getBytes());
//        verify(gardenUserService).addUser(loggedInUser);
//        verify(model).addAttribute("userId", loggedInUserId);
//
//        assertEquals(description, loggedInUser.getDescription());
//        assertEquals("redirect:/users/public-profile", viewName);
//    }
//
//    // @Test
//    // public void testPublicProfile() {
//
//    // }
//
//    // @Test
//    // public void testUpdateUser() {
//
//    // }
//
//    // @Test
//    // void testGetProfile_ProfileNotPresent_ReturnsAccessDenied() {
//
//    // }
//
}
