package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.StrikeService;

@SpringBootTest
@ActiveProfiles("integrationTest")
class StrikeServiceIntegrationTests {
    @MockBean
    private Clock clock;
    @MockBean
    @Qualifier("taskScheduler")
    // This is not directly used by the test, but is required to instantiate the controllers
    private Executor executor;

    @Autowired
    private StrikeService strikeService;
    @Autowired
    private GardenUserService userService;

    private Instant now;
    private GardenUser user;
    private long userId;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.parse("2024-03-13T18:00:00").atZone(ZoneId.of("Pacific/Auckland")).toInstant();
        // Reset required here as @MockBean reuses the same mock for all tests
        reset(clock);
    }

    @Test
    void givenUserScheduledForReenabling_whenReenableCalled_thenUserIsReenabled() {
        user = new GardenUser("Jane", "Doe", "jdo458@uclive.ac.nz", "password123", "01/01/1970");
        userService.addUser(user);
        userId = user.getId();

        when(clock.instant()).thenReturn(now);
        user.setStrikeCount(3);
        user.setAccountDisabled(true);
        user.setAccountDisabledExpiryInstant(now.minusSeconds(1));
        userService.addUser(user);

        strikeService.reenableScheduledUsers();

        user = userService.getUserById(userId);
        assertEquals(0, user.getStrikeCount());
        assertFalse(user.isAccountDisabled());
        assertNull(user.getAccountDisabledExpiryInstant());
        verify(clock).instant();
    }

    @Test
    void givenUserNotScheduledForReenabling_whenReenableCalled_thenUserIsNotReenabled() {
        user = new GardenUser("Jane", "Doe", "jdo459@uclive.ac.nz", "password123", "01/01/1970");
        userService.addUser(user);
        userId = user.getId();

        when(clock.instant()).thenReturn(now);
        user.setStrikeCount(3);
        user.setAccountDisabled(true);
        user.setAccountDisabledExpiryInstant(now.plusSeconds(1));
        userService.addUser(user);

        strikeService.reenableScheduledUsers();

        user = userService.getUserById(userId);
        assertEquals(3, user.getStrikeCount());
        assertTrue(user.isAccountDisabled());
        assertNotNull(user.getAccountDisabledExpiryInstant());
        verify(clock).instant();
    }
}
