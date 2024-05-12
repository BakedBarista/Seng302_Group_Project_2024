package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.time.Instant;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

/**
 * Handles strikes against user accounts and whether they should be disabled.
 */
public class StrikeService {
    private static final int STRIKES_FOR_WARNING = 5;
    private static final int STRIKES_FOR_DISABLING = 10;
    private static final int DISABLE_DURATION_DAYS = 7;

    private GardenUserService gardenUserService;

    public StrikeService(GardenUserService gardenUserService) {
        this.gardenUserService = gardenUserService;
    }

    /**
     * Adds a strike to the user with the given username.
     *
     * @param user The user to add a strike to.
     */
    public void addStrike(GardenUser user, Instant now) {
        int count = user.getStrikeCount();
        count += 1;
        user.setStrikeCount(0);

        if (count == STRIKES_FOR_WARNING) {
            // TODO: send warning email
        } else if (count >= STRIKES_FOR_DISABLING) {
            user.setAccountDisabled(true);
            user.setAccountDisabledExpiryInstant(now.plusSeconds(DISABLE_DURATION_DAYS * 24 * 60 * 60));
        }
    }
}
