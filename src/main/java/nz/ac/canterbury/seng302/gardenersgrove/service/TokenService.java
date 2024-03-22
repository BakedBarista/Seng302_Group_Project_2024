package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Class to make and deal with authentication tokens
 *
 * uses some code from https://stackoverflow.com/a/56628391
 */
@Component
public class TokenService {

    private Logger logger = LoggerFactory.getLogger(TokenService.class);

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

    @Scheduled(fixedRate = 60_000)
    public void cleanUpTokens() {
        logger.info("cleaning up tokens");

        // TODO: clean up expired tokens
    }
}
