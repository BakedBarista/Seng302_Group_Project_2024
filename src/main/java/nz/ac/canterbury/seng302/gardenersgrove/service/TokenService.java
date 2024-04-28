package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

/**
 * Class to make and deal with authentication tokens*
 * uses some code from https://stackoverflow.com/a/56628391
 * for createAuthenticationToken method
 */
@Component
public class TokenService {

    private Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    private GardenUserRepository userRepository;
    private Clock clock;

    public TokenService(GardenUserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

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
        System.out.println(token);
        return token.toString();
    }

    /**
     *  Scheduled clean up of expired tokens every 60 seconds
     */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void cleanUpTokens() {
        logger.debug("cleaning up tokens");

        int deletedEmailTokens = userRepository.deleteUsersWithExpiredEmailTokens(clock.instant());
        if (deletedEmailTokens != 0) {
            logger.info("deleted {} users with expired tokens", deletedEmailTokens);
        }

        int deletedPasswordTokens = userRepository.removeExpiredResetPasswordTokens(clock.instant());
        if (deletedPasswordTokens != 0) {
            logger.info("removed {} reset password tokens", deletedPasswordTokens);
        }
    }
//
//    /**
//     * adds a random token and this time instance to a given user in the DB
//     * @param userId
//     * @return
//     */
//    public void addEmailTokenAndTimeToUser(Long userId) {
//        logger.info("called addTokenAndTimeToUser");
//        String token = createEmailToken();
//
//        GardenUser user = userService.getUserById(userId);
//        Instant time = Instant.now().plus(10, ChronoUnit.MINUTES);
//        user.setResetPasswordToken(token);
//        user.setResetPasswordTokenExpiryInstant(time);
//    }
}
