package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;

import org.apache.catalina.Executor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootTest
@ActiveProfiles("integrationTest")
public class TokenServiceIntegrationTests {
    @MockBean
    private Clock clock;
    @MockBean
    @Qualifier("taskScheduler")
    private Executor executor;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private GardenUserService userService;

    private Instant now;
    private GardenUser user;
    private long userId;

    @BeforeEach
    public void setUp() {
        now = LocalDateTime.parse("2024-03-13T18:00:00").atZone(ZoneId.of("Pacific/Auckland")).toInstant();
        // Reset required here as @MockBean reuses the same mock for all tests
        Mockito.reset(clock);
    }

    @Test
    public void testCleanUpTokens_DeletesUsersWithExpiredEmailTokens() {
        user = new GardenUser("Jane", "Doe", "jdo456@uclive.ac.nz", "password123", "01/01/1970");
        userService.addUser(user);
        userId = user.getId();

        Mockito.when(clock.instant()).thenReturn(now);
        user.setEmailValidationToken("123456");
        user.setEmailValidationTokenExpiryInstant(now.minusSeconds(1));
        userService.addUser(user);

        tokenService.cleanUpTokens();

        Assertions.assertNull(userService.getUserById(userId));
        Mockito.verify(clock).instant();
    }

    @Test
    public void testCleanUpTokens_LeavesUnexpiredEmailTokens() {
        user = new GardenUser("Jane", "Doe", "jdo457@uclive.ac.nz", "password123", "01/01/1970");
        userService.addUser(user);
        userId = user.getId();

        Mockito.when(clock.instant()).thenReturn(now);
        user.setEmailValidationToken("123456");
        user.setEmailValidationTokenExpiryInstant(now.plusSeconds(1));
        userService.addUser(user);

        tokenService.cleanUpTokens();

        Assertions.assertNotNull(userService.getUserById(userId));
        Mockito.verify(clock).instant();
    }

    @Test
    public void testCleanUpTokens_DeletesTokenFromUserWithExpiredResetPasswordToken() {
        Mockito.when(clock.instant()).thenReturn(now);
        user = new GardenUser("Jane", "Doe", "jdo458@uclive.ac.nz", "password123", "01/01/1970");
        userService.addUser(user);
        userId = user.getId();

        user.setResetPasswordToken("123456");
        user.setResetPasswordTokenExpiryInstant(now.minusSeconds(1));
        userService.addUser(user);

        tokenService.cleanUpTokens();

        Assertions.assertNull(userService.getUserById(userId).getResetPasswordToken());
        Mockito.verify(clock).instant();
    }

    @Test
    public void testCleanUpTokens_LeavesUnexpiredResetPasswordToken() {
        Mockito.when(clock.instant()).thenReturn(now);
        user = new GardenUser("Jane", "Doe", "jdo459@uclive.ac.nz", "password123", "01/01/1970");
        userService.addUser(user);
        userId = user.getId();

        user.setResetPasswordToken("123456");
        user.setResetPasswordTokenExpiryInstant(now.plusSeconds(1));
        userService.addUser(user);

        tokenService.cleanUpTokens();

        Assertions.assertNotNull(userService.getUserById(userId).getResetPasswordToken());
        Mockito.verify(clock).instant();
    }
}
