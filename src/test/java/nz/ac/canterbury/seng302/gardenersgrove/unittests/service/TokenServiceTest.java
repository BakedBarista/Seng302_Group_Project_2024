package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.mockito.Mockito;

public class TokenServiceTest {
    private TokenService tokenService = new TokenService(null, null);

    @Test
    public void testCreateAuthenticationToken_Is32Characters_OnlyContainsBase64Characters() {
        int expectedLength = 32;
        String token = tokenService.createAuthenticationToken();

        Assertions.assertEquals(expectedLength, token.length());
        Assertions.assertTrue(token.matches("^[A-Za-z0-9-_]+$"));
    }

    @Test
    public void testCreateEmailToken_IsSixDigits_OnlyContainsIntegers() {
        int expectedLength = 6;
        String token = tokenService.createEmailToken();

        Assertions.assertEquals(expectedLength, token.length());
        Assertions.assertTrue(token.matches("^\\d+$"));
    }

    @Test
    public void testAddEmailTokenAndTimeToUser_AddsTokenAndTimeToUser() {
        // add user to persistence and then call function to add token and time instant
        GardenUser user = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password123", "01/01/1970");
        tokenService.addEmailTokenAndTimeToUser(user);

        // check that toke and time instant persist
        Assertions.assertNotNull(user.getEmailValidationToken());
        Assertions.assertNotNull(user.getEmailValidationTokenExpiryInstant());
    }

    @Test
    public void testAddResetPasswordTokenAndTimeToUser_AddsResetPasswordTokenAndTimeToUser() {
        // add user to persistence and then call function to add token and time instant
        GardenUser user = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password123", "01/01/1970");
        tokenService.addResetPasswordTokenAndTimeToUser(user);

        // check that toke and time instant persist
        Assertions.assertNotNull(user.getResetPasswordToken());
        Assertions.assertNotNull(user.getResetPasswordTokenExpiryInstant());
    }
}
