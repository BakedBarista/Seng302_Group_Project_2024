package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;

/**
 * Class to make and deal with authentication tokens
 *
 * uses some code from https://stackoverflow.com/a/56628391
 * for createAuthenticationToken method
 */
@Component
public class TokenService {

    private Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Autowired
    private GardenUserRepository userRepository;

    @Autowired
    private Clock clock;

    /**
     * create a random 32-character authentication token and return it
     * 
     * @return token
     */
    public String createAuthenticationToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String token = base64Encoder.encodeToString(randomBytes);
        logger.info("made new authentication token {}", token);

        return token;
    }

    /**
     * Create a random 6-digit token for email verification on signup
     * 
     * @return token
     */
    public String createEmailToken() {
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            token.append(secureRandom.nextInt(10));
        }

        logger.info("made new email token {}", token);

        return token.toString();
    }

    @Scheduled(fixedRate = 1_000)
    @Transactional
    public void cleanUpTokens() {
        logger.debug("cleaning up tokens");

        int deleted = userRepository.deleteUsersWithExpiredEmailTokens(clock.instant());
        if (deleted != 0) {
            logger.info("deleted {} users with expired tokens", deleted);
        }
    }
}
