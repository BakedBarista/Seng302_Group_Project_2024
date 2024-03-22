package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Class to make and deal with authentication tokens
 *
 * uses some code from https://stackoverflow.com/a/56628391
 */
public class TokenService {

    public Logger logger;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    /**
     * create a random 32-character token and return it
     * @return token
     */
    public String createToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String token = base64Encoder.encodeToString(randomBytes);
        logger.info("made new token {token}");

        return token;
    }
}
