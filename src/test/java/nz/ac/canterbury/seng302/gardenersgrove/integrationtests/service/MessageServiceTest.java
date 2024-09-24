package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageReadRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    GardenUserService gardenUserService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MessageReadRepository messageReadRepository;

    @MockBean
    Clock clock;

    MessageService messageService;

    Long receiverId;
    Long otherId;

    @BeforeEach
    void setup() {
        // Clear the repository to avoid state being carried over between tests
        messageReadRepository.deleteAll();

        // Ensure users exist before running the tests
        GardenUser john = gardenUserService.getUserByEmail("johnmessageservice@gmail.com");
        if (john == null) {
            john = new GardenUser("John", "Doe", "johnmessageservice@gmail.com", "password", null);
            receiverId = gardenUserService.addUser(john).getId();
        } else {
            receiverId = john.getId();
        }

        GardenUser jan = gardenUserService.getUserByEmail("janmessageservice@gmail.com");
        if (jan == null) {
            jan = new GardenUser("Jan", "Doe", "janmessageservice@gmail.com", "password", null);
            otherId = gardenUserService.addUser(jan).getId();
        } else {
            otherId = jan.getId();
        }

        messageService = new MessageService(messageRepository, messageReadRepository, clock, gardenUserService);
    }

    @Test
    void givenIHaveFriend_whenIReadMessages_thenMyLastReadUpdated_andTheirLastReadNotUpdated() {
        Instant currentInstant = Instant.parse("2023-09-01T12:00:00Z");
        ZoneId zone = ZoneId.systemDefault();

        Mockito.when(clock.instant()).thenReturn(currentInstant);
        Mockito.when(clock.getZone()).thenReturn(zone);

        messageService.setReadTime(receiverId, otherId);
        LocalDateTime expectedReadTimeForReceiver = LocalDateTime.ofInstant(currentInstant, zone);

        Assertions.assertEquals(expectedReadTimeForReceiver,
                messageReadRepository.findByReceiverIdAndUserId(receiverId, otherId).get().getLastReadMessage());

        Instant oneHourLater = currentInstant.plus(1, ChronoUnit.HOURS);
        Mockito.when(clock.instant()).thenReturn(oneHourLater);

        messageService.setReadTime(otherId, receiverId);
        LocalDateTime expectedReadTimeForOther = LocalDateTime.ofInstant(oneHourLater, zone);

        Assertions.assertEquals(expectedReadTimeForReceiver,
                messageReadRepository.findByReceiverIdAndUserId(receiverId, otherId).get().getLastReadMessage());
        Assertions.assertEquals(expectedReadTimeForOther,
                messageReadRepository.findByReceiverIdAndUserId(otherId, receiverId).get().getLastReadMessage());
    }

    @Test
    void givenReceiverIdDoesNotExist_whenIUpdateLastReadMessage_thenThisIsHandled() {
        Instant currentInstant = Instant.parse("2023-09-01T12:00:00Z");
        ZoneId zone = ZoneId.systemDefault();
        Mockito.when(clock.instant()).thenReturn(currentInstant);
        Mockito.when(clock.getZone()).thenReturn(zone);

        Assertions.assertDoesNotThrow(() -> messageService.setReadTime(null, otherId));
    }

    @Test
    void givenOtherIdDoesNotExist_whenIUpdateLastReadMessage_thenThisIsHandled() {
        Instant currentInstant = Instant.parse("2023-09-01T12:00:00Z");
        ZoneId zone = ZoneId.systemDefault();
        Mockito.when(clock.instant()).thenReturn(currentInstant);
        Mockito.when(clock.getZone()).thenReturn(zone);

        Assertions.assertDoesNotThrow(() -> messageService.setReadTime(receiverId, null));
    }
}
