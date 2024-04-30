package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;

public class TokenServiceTest {
    private TokenService tokenService = new TokenService(null, null);

    @Test
    public void whenCreateAuthenticationTokenCalled_thenTokenHas128bits() {
        int expectedBits = 128;
        String token = tokenService.createAuthenticationToken();
        String tokenWithoutHyphens = token.replace("-", "");

        int tokenLengthInBits = tokenWithoutHyphens.length() * 4;
        Assertions.assertEquals(expectedBits, tokenLengthInBits);
    }

    @Test
    public void whenCreateEmailTokenCalled_thenTokenIsSixDigits_andOnlyContainsIntegers() {
        int expectedLength = 6;
        String token = tokenService.createEmailToken();

        Assertions.assertEquals(expectedLength, token.length());
        Assertions.assertTrue(token.matches("^\\d+$"));
    }

    @Test
    public void whenAddEmailTokenAndTimeToUserCallen_thenTokenIsAddedToUser() {
        // add user to persistence and then call function to add token and time instant
        GardenUser user = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password123", "01/01/1970");
        tokenService.addEmailTokenAndTimeToUser(user);

        // check that toke and time instant persist
        Assertions.assertNotNull(user.getEmailValidationToken());
        Assertions.assertNotNull(user.getEmailValidationTokenExpiryInstant());
    }

    @Test
    public void givenAddResetPasswordTokenAndTimeToUserCalled_thenResetPasswordTokenIsAddedToUser() {
        // add user to persistence and then call function to add token and time instant
        GardenUser user = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password123", "01/01/1970");
        tokenService.addResetPasswordTokenAndTimeToUser(user);

        // check that toke and time instant persist
        Assertions.assertNotNull(user.getResetPasswordToken());
        Assertions.assertNotNull(user.getResetPasswordTokenExpiryInstant());
    }
}
