package nz.ac.canterbury.seng302.gardenersgrove.unittests.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.MessageRead;

public class MessageReadTest {
    @Test
    void testIdGetterAndSetter() {
        MessageRead messageRead = new MessageRead();
        Long expectedId = 1L;
        messageRead.setId(expectedId);
        assertEquals(expectedId, messageRead.getId());
    }

    @Test
    void testReceiverIdGetterAndSetter() {
        MessageRead messageRead = new MessageRead();
        Long expectedReceiverId = 2L;
        messageRead.setReceiverId(expectedReceiverId);
        assertEquals(expectedReceiverId, messageRead.getReceiverId());
    }

    @Test
    void testUserIdGetterAndSetter() {
        MessageRead messageRead = new MessageRead();
        Long expectedUserId = 3L;
        messageRead.setUserId(expectedUserId);
        assertEquals(expectedUserId, messageRead.getUserId());
    }

    @Test
    void testLastReadMessageGetterAndSetter() {
        MessageRead messageRead = new MessageRead();
        LocalDateTime expectedLastReadMessage = LocalDateTime.now();
        messageRead.setLastReadMessage(expectedLastReadMessage);
        assertEquals(expectedLastReadMessage, messageRead.getLastReadMessage());
    }

    @Test
    void testConstructor() {
        Long expectedReceiverId = 4L;
        Long expectedUserId = 5L;

        MessageRead messageRead = new MessageRead(expectedReceiverId, expectedUserId);
        assertEquals(expectedReceiverId, messageRead.getReceiverId());
        assertEquals(expectedUserId, messageRead.getUserId());
    }
}
