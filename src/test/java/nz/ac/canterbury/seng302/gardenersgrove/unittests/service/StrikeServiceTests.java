package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.StrikeService;

class StrikeServiceTests {
    private GardenUserService userService;
    private Clock clock;
    private StrikeService strikeService;
    private GardenUser user;
    private Instant now;
    private EmailSenderService emailSenderService;
    private GardenUserRepository gardenUserRepository;

    @BeforeEach
    void setUp() {
        userService = mock(GardenUserService.class);
        emailSenderService = mock(EmailSenderService.class);
        gardenUserRepository = mock(GardenUserRepository.class);
        clock = mock(Clock.class);
        strikeService = new StrikeService(userService, emailSenderService, gardenUserRepository,clock);

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
}
