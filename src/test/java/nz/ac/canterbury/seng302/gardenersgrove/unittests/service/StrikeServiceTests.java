package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.StrikeService;

class StrikeServiceTests {
    private static final long SECONDS_IN_DAY = 24L * 60 * 60;

    private GardenUserService userService;
    private Clock clock;
    private StrikeService strikeService;
    private GardenUser user;
    private Instant now;

    @BeforeEach
    void setUp() {
        userService = mock(GardenUserService.class);
        clock = mock(Clock.class);
        strikeService = new StrikeService(userService, null, clock);

        user = new GardenUser();
        now = LocalDateTime.parse("2024-03-13T18:00:00").atZone(ZoneId.of("Pacific/Auckland")).toInstant();
    }

    @Test
    void givenUserHasNoStrikes_whenStrikeAdded_userHasOneStrike() {
        user.setStrikeCount(0);

        strikeService.addStrike(user);

        assertEquals(1, user.getStrikeCount());
        verify(userService, atLeastOnce()).addUser(user);
    }

    @Test
    void givenUserHasFourStrikes_whenStrikeAdded_userIsNotDisabled() {
        user.setStrikeCount(4);

        strikeService.addStrike(user);

        assertEquals(5, user.getStrikeCount());
        assertFalse(user.isAccountDisabled());
        verify(userService, atLeastOnce()).addUser(user);
    }

    @Test
    void givenUserHasFiveStrikes_whenStrikeAdded_userIsDisabled() {
        user.setStrikeCount(5);
        when(clock.instant()).thenReturn(now);

        strikeService.addStrike(user);

        assertEquals(6, user.getStrikeCount());
        assertTrue(user.isAccountDisabled());
        verify(userService, atLeastOnce()).addUser(user);
    }

    @Test
    void givenUnblockedInJustOverSixDays_whenDaysUtilUnblockedCalculated_thenReturnsSevenDays() {
        when(clock.instant()).thenReturn(now);
        user.setAccountDisabledExpiryInstant(now.plusSeconds(6 * SECONDS_IN_DAY + 10));

        long daysLeft = strikeService.daysUntilUnblocked(user);

        assertEquals(7, daysLeft);
    }

    @Test
    void givenUnblockedInOneDay_whenDaysUtilUnblockedCalculated_thenReturnsOneDay() {
        when(clock.instant()).thenReturn(now);
        user.setAccountDisabledExpiryInstant(now.plusSeconds(SECONDS_IN_DAY));

        long daysLeft = strikeService.daysUntilUnblocked(user);

        assertEquals(1, daysLeft);
    }

    @Test
    void givenNotBlocked_whenDaysUtilUnblockedCalculated_thenReturnsZeroDays() {
        when(clock.instant()).thenReturn(now);

        long daysLeft = strikeService.daysUntilUnblocked(user);

        assertEquals(0, daysLeft);
    }
}
