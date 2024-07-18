package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

public class GardenUserServiceTest {
    private GardenUserService gardenUserService;
    private GardenUserRepository mockRepository;

    private GardenUser testUser1 = new GardenUser("John", "Doe", "jdo123@uclive.ac.nz", "password",
            null);
    private GardenUser testUser2 = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password",
            null);

    @BeforeEach
    public void setUp() {
        mockRepository = Mockito.mock(GardenUserRepository.class);
        gardenUserService = new GardenUserService(mockRepository);
    }

    @Test
    public void givenGetUsersCalled_thenReturnsUsers() {
        var allUsers = List.of(testUser1, testUser2);
        Mockito.when(mockRepository.findAll()).thenReturn(allUsers);

        var users = gardenUserService.getUser();

        Mockito.verify(mockRepository).findAll();
        assertEquals(allUsers, users);
    }

    @Test
    public void givenUserWithEmailExists_whenAddUserCalled_thenThrowsException() {
        testUser1.setEmail("email@example.com");
        testUser2.setEmail("email@example.com");
        Mockito.when(mockRepository.findByEmail(testUser1.getEmail())).thenReturn(Optional.of(testUser1));

        assertThrows(IllegalStateException.class, () -> gardenUserService.addUser(testUser2));
    }

    @Test
    public void givenUserWithEmailExists_whenGetUserByEmailCalled_thenReturnsUser() {
        var email = testUser1.getEmail();
        Mockito.when(mockRepository.findByEmail(email)).thenReturn(Optional.of(testUser1));

        var user = gardenUserService.getUserByEmail(email);

        Mockito.verify(mockRepository).findByEmail(email);
        assertEquals(testUser1, user);
    }

    @Test
    public void givenUserWithEmailDoesntExist_whenGetUserByEmailCalled_thenReturnsNull() {
        var email = testUser1.getEmail();
        Mockito.when(mockRepository.findByEmail(email)).thenReturn(Optional.empty());

        var user = gardenUserService.getUserByEmail(email);

        Mockito.verify(mockRepository).findByEmail(email);
        assertNull(user);
    }

    @Test
    public void givenGetUserByEmailAndPasswordCalledWithValidEmailAndPassword_thenReturnsUser() {
        var email = testUser1.getEmail();
        Mockito.when(mockRepository.findByEmail(email)).thenReturn(Optional.of(testUser1));

        var user = gardenUserService.getUserByEmailAndPassword(email, "password");

        Mockito.verify(mockRepository).findByEmail(email);
        assertEquals(testUser1, user);
    }

    @Test
    public void givenGetUserByEmailAndPasswordCalledWithInvalidEmail_thenReturnsNull() {
        var email = testUser1.getEmail();
        Mockito.when(mockRepository.findByEmail(email)).thenReturn(Optional.empty());

        var user = gardenUserService.getUserByEmailAndPassword(email, "password");

        Mockito.verify(mockRepository).findByEmail(email);
        assertNull(user);
    }

    @Test
    public void givenGetUserByEmailAndPasswordCalledWithInvalidPassword_thenReturnsNull() {
        var email = testUser1.getEmail();
        Mockito.when(mockRepository.findByEmail(email)).thenReturn(Optional.of(testUser1));

        var user = gardenUserService.getUserByEmailAndPassword(email, "invalid-password");

        Mockito.verify(mockRepository).findByEmail(email);
        assertNull(user);
    }

    @Test
    public void givenGetUserByIdCalledWithValidId_thenReturnsUser() {
        var id = 1L;
        Mockito.when(mockRepository.findById(id)).thenReturn(Optional.of(testUser1));

        var user = gardenUserService.getUserById(id);

        Mockito.verify(mockRepository).findById(id);
        assertEquals(testUser1, user);
    }

    @Test
    public void giveGetUserByIdCalledWithInvalidId_thenReturnsNull() {
        var id = 1L;
        Mockito.when(mockRepository.findById(id)).thenReturn(Optional.empty());

        var user = gardenUserService.getUserById(id);

        Mockito.verify(mockRepository).findById(id);
        assertNull(user);
    }

    @Test
    public void givenSetProfilePictureCalled_thenUpdatesProfilePicture() {
        var id = 1L;
        var contentType = "text/plain";
        var profilePicture = "profile-picture".getBytes();
        Mockito.when(mockRepository.findById(id)).thenReturn(Optional.of(testUser1));

        gardenUserService.setProfilePicture(id, contentType, profilePicture);

        Mockito.verify(mockRepository).save(testUser1);
        assertEquals(contentType, testUser1.getProfilePictureContentType());
        assertEquals(profilePicture, testUser1.getProfilePicture());
    }

    @Test
    public void givenSetProfilePictureCalledWithInvalidId_thenDoesNothing() {
        var id = 1L;
        var contentType = "text/plain";
        var profilePicture = "profile-picture".getBytes();
        Mockito.when(mockRepository.findById(id)).thenReturn(Optional.empty());

        gardenUserService.setProfilePicture(id, contentType, profilePicture);

        Mockito.verify(mockRepository, Mockito.never()).save(testUser1);
    }

    @Test
    public void givenEmailIsValid_whenObfuscateEmailCalled_thenReturnsBase64EncodedEmail() {
        var email = "john.doe@gmail.com";
        var expected = "am9obi5kb2VAZ21haWwuY29t";

        var obfuscatedEmail = gardenUserService.obfuscateEmail(email);

        assertEquals(expected, obfuscatedEmail);
    }

    @Test
    public void givenEmailIsInvalid_whenObfuscateEmailCalled_thenReturnsBase64EncodedEmail() {
        var email = "not an email";
        var expected = "bm90IGFuIGVtYWls";

        var obfuscatedEmail = gardenUserService.obfuscateEmail(email);

        assertEquals(expected, obfuscatedEmail);
    }

    @Test
    public void givenObfuscatedEmailIsValid_whenDeobfuscateEmailCalled_thenReturnsEmail() {
        var obfuscatedEmail = "am9obi5kb2VAZ21haWwuY29t";
        var expected = "john.doe@gmail.com";

        var email = gardenUserService.deobfuscateEmail(obfuscatedEmail);

        assertEquals(expected, email);
    }

    @Test
    public void givenObfuscatedEmailIsInvalid_whenDeobfuscateEmailCalled_thenThrowsException() {
        var obfuscatedEmail = "not-base64 :)";

        assertThrows(RuntimeException.class, () -> gardenUserService.deobfuscateEmail(obfuscatedEmail));
    }
}
