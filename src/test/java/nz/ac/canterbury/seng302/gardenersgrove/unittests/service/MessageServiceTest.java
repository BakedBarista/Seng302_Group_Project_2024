package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.Mockito.*;

class MessageServiceTest {
    private MessageService messageService;
    private MessageRepository messageRepository;
    private Clock clock;
    private Instant timestamp;

    @BeforeEach
    public void setUp() {
        messageRepository = mock(MessageRepository.class);
        clock = mock(Clock.class);
        messageService = new MessageService(messageRepository, clock);

        timestamp = Instant.ofEpochSecond(0);
    }

    @Test
    void whenSendMessage_thenMessageCreatedCorrectly() {
        Long sender = 1L;
        Long receiver = 2L;
        String messageWord = "Hello";
        MessageDTO messageDTO = new MessageDTO(messageWord,"token");

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(timestamp);

        Message message = messageService.sendMessage(sender, receiver, messageDTO);

        Assertions.assertEquals(sender, message.getSender());
        Assertions.assertEquals(receiver, message.getReceiver());
        Assertions.assertEquals(messageWord, message.getMessageContent());
        Assertions.assertEquals(timestamp.atZone(clock.getZone()).toLocalDateTime(), message.getTimestamp());
    }

    @Test
    void whenSendMessage_thenMessageSaveCalled() {
        Long sender = 1L;
        Long receiver = 2L;
        String messageWord = "Hello";
        MessageDTO messageDTO = new MessageDTO(messageWord,"token");

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(timestamp);

        Message message = messageService.sendMessage(sender, receiver, messageDTO);

        Mockito.verify(messageRepository).save(message);
    }
}
