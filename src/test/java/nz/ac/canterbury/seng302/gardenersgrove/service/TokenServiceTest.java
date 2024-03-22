package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenServiceTest {

    TokenService tokenService = new TokenService();

    @Test
    public void testCreateEmailToken_IsSixDigits_OnlyContainsIntegers() {
        int expectedLength = 6;
        String token = tokenService.createEmailToken();


        Assertions.assertEquals(expectedLength, token.length());
        Assertions.assertTrue(token.matches("\\d+"));
    }
}
