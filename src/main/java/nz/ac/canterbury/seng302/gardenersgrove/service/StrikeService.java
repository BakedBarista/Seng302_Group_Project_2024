package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;

/**
 * Handles strikes against user accounts and whether they should be disabled.
 */
@Component
public class StrikeService {
    private static final int STRIKES_FOR_WARNING = 5;
    private static final int STRIKES_FOR_DISABLING = 6;
    private static final long DISABLE_DURATION_DAYS = 7;
    private static final long SECONDS_IN_DAY = 24L * 60 * 60;

    private Logger logger = LoggerFactory.getLogger(StrikeService.class);

    private GardenUserService userService;
    private GardenUserRepository userRepository;
    private Clock clock;

    public StrikeService(GardenUserService userService, GardenUserRepository userRepository, Clock clock) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    /**
     * Adds a strike to the given user and saves the user to the database.
     * 
     * <p>
     * After calling this method, you should check the user's `isAccountDisabled()`
     * property to see if the user needs to be signed out.
     *
     * @param user The user to add a strike to.'
     * @return The consequence for the strike; either warned, blocked or nothing
     */
    public AddStrikeResult addStrike(GardenUser user) {
        int count = user.getStrikeCount();
        count += 1;
        user.setStrikeCount(count);
        userService.addUser(user);

        if (count == STRIKES_FOR_WARNING) {
            // TODO: send warning email

            return AddStrikeResult.WARNING;
        } else if (count >= STRIKES_FOR_DISABLING) {
            Instant expiryInstant = clock.instant().plusSeconds(DISABLE_DURATION_DAYS * SECONDS_IN_DAY);

            user.setAccountDisabled(true);
            user.setAccountDisabledExpiryInstant(expiryInstant);
            userService.addUser(user);

            // TODO: send confirmation email

            return AddStrikeResult.BLOCK;
        }
        return AddStrikeResult.NO_ACTION;
    }

    public long daysUntilUnblocked(GardenUser user) {
        Instant now = clock.instant();
        Instant expiry = user.getAccountDisabledExpiryInstant();
        if (expiry == null) {
            return 0;
        }

        Duration timeRemaining = Duration.between(now, expiry);

        return (long) Math.ceil(timeRemaining.getSeconds() / (float) SECONDS_IN_DAY);
    }

    /**
     * Scheduled re-enabling of user accounts.
     *
     * <p>
     * Runs every hour.
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void reenableScheduledUsers() {
        logger.debug("re-enabling user accounts");

        Instant now = clock.instant();

        int reenabledUsers = userRepository.reenableScheduledUsers(now);
        if (reenabledUsers != 0) {
            logger.info("re-enabled {} users whose accounts were disabled", reenabledUsers);
        }
    }

    /**
     * The consequence for the strike; either warned, blocked or nothing
     */
    public enum AddStrikeResult {
        /**
         * There was no consequence for the strike
         */
        NO_ACTION,
        /**
         * The user was warned that they will be blocked on their next strike
         */
        WARNING,
        /**
         * The user was blocked
         */
        BLOCK,
    }
}
