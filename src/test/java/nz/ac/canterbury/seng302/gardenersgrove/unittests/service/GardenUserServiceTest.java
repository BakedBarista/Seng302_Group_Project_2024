package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

import static org.junit.jupiter.api.Assertions.*;

class GardenUserServiceTest {
    private GardenUserService gardenUserService;
    private GardenUserRepository mockRepository;
    private Plant plant1;
    private Plant plant2;

    private GardenUser testUser1 = new GardenUser("John", "Doe", "jdo123@uclive.ac.nz", "password",
            null);
    private GardenUser testUser2 = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password",
            null);

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(GardenUserRepository.class);
        gardenUserService = new GardenUserService(mockRepository);
        // Initialize test plants
        plant1 = new Plant("Tomato", "1", null, null);
        plant2 = new Plant("Carrot", "2", null, null);

        // Initialize and set favorite plants
        Set<Plant> favoritePlants = new HashSet<>();
        favoritePlants.add(plant1);
        favoritePlants.add(plant2);

        testUser1.setId(1L);
        testUser1.setFavouritePlants(favoritePlants); // Explicitly set favorite plants

        Mockito.when(mockRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
    }

    @Test
    void givenGetUsersCalled_thenReturnsUsers() {
        var allUsers = List.of(testUser1, testUser2);
        when(mockRepository.findAll()).thenReturn(allUsers);

        var users = gardenUserService.getUser();

        Mockito.verify(mockRepository).findAll();
        assertEquals(allUsers, users);
    }

    @Test
    void givenUserWithEmailExists_whenAddUserCalled_thenThrowsException() {
        testUser1.setEmail("email@example.com");
        testUser2.setEmail("email@example.com");
        when(mockRepository.findByEmail(testUser1.getEmail())).thenReturn(Optional.of(testUser1));

        assertThrows(IllegalStateException.class, () -> gardenUserService.addUser(testUser2));
    }

    @Test
    void givenUserWithEmailExists_whenGetUserByEmailCalled_thenReturnsUser() {
        var email = testUser1.getEmail();
        when(mockRepository.findByEmail(email)).thenReturn(Optional.of(testUser1));

        var user = gardenUserService.getUserByEmail(email);

        Mockito.verify(mockRepository).findByEmail(email);
        assertEquals(testUser1, user);
    }

    @Test
    void givenUserWithEmailDoesntExist_whenGetUserByEmailCalled_thenReturnsNull() {
        var email = testUser1.getEmail();
        when(mockRepository.findByEmail(email)).thenReturn(Optional.empty());

        var user = gardenUserService.getUserByEmail(email);

        Mockito.verify(mockRepository).findByEmail(email);
        assertNull(user);
    }

    @Test
    void givenGetUserByEmailAndPasswordCalledWithValidEmailAndPassword_thenReturnsUser() {
        var email = testUser1.getEmail();
        when(mockRepository.findByEmail(email)).thenReturn(Optional.of(testUser1));

        var user = gardenUserService.getUserByEmailAndPassword(email, "password");

        Mockito.verify(mockRepository).findByEmail(email);
        assertEquals(testUser1, user);
    }

    @Test
    void givenGetUserByEmailAndPasswordCalledWithInvalidEmail_thenReturnsNull() {
        var email = testUser1.getEmail();
        when(mockRepository.findByEmail(email)).thenReturn(Optional.empty());

        var user = gardenUserService.getUserByEmailAndPassword(email, "password");

        Mockito.verify(mockRepository).findByEmail(email);
        assertNull(user);
    }

    @Test
    void givenGetUserByEmailAndPasswordCalledWithInvalidPassword_thenReturnsNull() {
        var email = testUser1.getEmail();
        when(mockRepository.findByEmail(email)).thenReturn(Optional.of(testUser1));

        var user = gardenUserService.getUserByEmailAndPassword(email, "invalid-password");

        Mockito.verify(mockRepository).findByEmail(email);
        assertNull(user);
    }

    @Test
    void givenGetUserByIdCalledWithValidId_thenReturnsUser() {
        var id = 1L;
        when(mockRepository.findById(id)).thenReturn(Optional.of(testUser1));

        var user = gardenUserService.getUserById(id);

        Mockito.verify(mockRepository).findById(id);
        assertEquals(testUser1, user);
    }

    @Test
    void giveGetUserByIdCalledWithInvalidId_thenReturnsNull() {
        var id = 1L;
        when(mockRepository.findById(id)).thenReturn(Optional.empty());

        var user = gardenUserService.getUserById(id);

        Mockito.verify(mockRepository).findById(id);
        assertNull(user);
    }

    @Test
    void givenSetProfilePictureCalled_thenUpdatesProfilePicture() {
        var id = 1L;
        var contentType = "text/plain";
        var profilePicture = "profile-picture".getBytes();
        when(mockRepository.findById(id)).thenReturn(Optional.of(testUser1));

        gardenUserService.setProfilePicture(id, contentType, profilePicture);

        Mockito.verify(mockRepository).save(testUser1);
        assertEquals(contentType, testUser1.getProfilePictureContentType());
        assertEquals(profilePicture, testUser1.getProfilePicture());
    }

    @Test
    void givenSetBannerCalled_thenUpdatesBanner() {
        var id = 1L;
        var contentType = "text/plain";
        var banner = "banner".getBytes();
        when(mockRepository.findById(id)).thenReturn(Optional.of(testUser1));

        gardenUserService.setProfileBanner(id, contentType, banner);

        Mockito.verify(mockRepository).save(testUser1);
        assertEquals(contentType, testUser1.getProfileBannerContentType());
        assertEquals(banner, testUser1.getProfileBanner());
    }

    @Test
    void givenSetProfilePictureCalledWithInvalidId_thenDoesNothing() {
        var id = 1L;
        var contentType = "text/plain";
        var profilePicture = "profile-picture".getBytes();
        when(mockRepository.findById(id)).thenReturn(Optional.empty());

        gardenUserService.setProfilePicture(id, contentType, profilePicture);

        Mockito.verify(mockRepository, Mockito.never()).save(testUser1);
    }

    @Test
    void givenEmailIsValid_whenObfuscateEmailCalled_thenReturnsBase64EncodedEmail() {
        var email = "john.doe@gmail.com";
        var expected = "am9obi5kb2VAZ21haWwuY29t";

        var obfuscatedEmail = gardenUserService.obfuscateEmail(email);

        assertEquals(expected, obfuscatedEmail);
    }

    @Test
    void givenEmailIsInvalid_whenObfuscateEmailCalled_thenReturnsBase64EncodedEmail() {
        var email = "not an email";
        var expected = "bm90IGFuIGVtYWls";

        var obfuscatedEmail = gardenUserService.obfuscateEmail(email);

        assertEquals(expected, obfuscatedEmail);
    }

    @Test
    void givenObfuscatedEmailIsValid_whenDeobfuscateEmailCalled_thenReturnsEmail() {
        var obfuscatedEmail = "am9obi5kb2VAZ21haWwuY29t";
        var expected = "john.doe@gmail.com";

        var email = gardenUserService.deobfuscateEmail(obfuscatedEmail);

        assertEquals(expected, email);
    }

    @Test
    void givenObfuscatedEmailIsInvalid_whenDeobfuscateEmailCalled_thenThrowsException() {
        var obfuscatedEmail = "not-base64 :)";

        assertThrows(RuntimeException.class, () -> gardenUserService.deobfuscateEmail(obfuscatedEmail));
    }

    @Test
    void givenGetfavouriteGardenById_thenReturnFavouriteGardens() {
        Garden garden = new Garden();
        testUser1.setFavoriteGarden(garden);
        testUser1.setId(1L);
        when(mockRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
        Garden favouriteGarden = gardenUserService.getFavoriteGarden(testUser1.getId());
        assertEquals(garden, favouriteGarden);
    }

    @Test
    void givenUserHasFavoritePlants_whenGetFavoritePlantsCalled_thenReturnsFavoritePlants() {
        Set<Plant> result = gardenUserService.getFavoritePlants(testUser1.getId());

        assertEquals(2, result.size());
        assertTrue(result.contains(plant1));
        assertTrue(result.contains(plant2));

    }

}
