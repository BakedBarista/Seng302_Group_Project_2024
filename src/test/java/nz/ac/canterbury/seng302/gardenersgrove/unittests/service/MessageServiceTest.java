package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class MessageServiceTest {
    private MessageService messageService;
    private MessageRepository messageRepository;
    private Clock clock;
    private Instant timestamp;

    private GardenUserService userService;

    @BeforeEach
    public void setUp() {
        messageRepository = mock(MessageRepository.class);
        clock = mock(Clock.class);
        userService = mock(GardenUserService.class);
        messageService = new MessageService(messageRepository, clock, userService);
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

        assertEquals(sender, message.getSender());
        assertEquals(receiver, message.getReceiver());
        assertEquals(messageWord, message.getMessageContent());
        assertEquals(timestamp.atZone(clock.getZone()).toLocalDateTime(), message.getTimestamp());
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

    @Test
    void whenFindAllRecentChats_thenReturnRecentChats() {
        Long user1 = 1L;

        Message message1 = new Message(user1, 2L, LocalDateTime.now().minusDays(1), "Hello");
        when(messageRepository.findAllRecentChats(user1))
                .thenReturn(List.of(message1));

        List<Message> result = messageService.findAllRecentChats(user1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void whenGetOtherUserIdWithSender_thenReturnReceiverId() {
        Long user1 = 1L;
        Long user2 = 2L;

        Message message = new Message(user1, user2, LocalDateTime.now(), "Hello");

        Long result = messageService.getOtherUserId(user1, message);
        assertEquals(user2, result);
    }

    @Test
    void whenGetOtherUserIdWithReceiver_thenReturnSenderId() {
        Long user1 = 1L;
        Long user2 = 2L;

        Message message = new Message(user2, user1, LocalDateTime.now(), "Hello");

        Long result = messageService.getOtherUserId(user1, message);
        assertEquals(user2, result);
    }

    @Test
    void whenGetLatestMessages_thenReturnLatestMessagePerUser() {
        Long loggedInUserId = 1L;
        Message message1 = new Message(loggedInUserId, 2L, LocalDateTime.now().minusDays(1), "Hi");
        Message message2 = new Message(2L, loggedInUserId, LocalDateTime.now(), "Hello");

        List<Message> allMessages = List.of(message1, message2);

        Map<Long, Message> result = messageService.getLatestMessages(allMessages, loggedInUserId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(message2, result.get(2L)); // Latest message from user2
    }

    @Test
    void whenConvertToPreview_thenReturnMessagePreviews() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        GardenUser user = new GardenUser();
        user.setId(userId2);
        Message message = new Message(userId1, userId2, LocalDateTime.now(), "Hey");

        when(userService.getUserById(userId2)).thenReturn(user);
        Map<Long, Message> recentMessagesMap = new HashMap<>();
        recentMessagesMap.put(userId2, message);

        Map<GardenUser, String> result = messageService.convertToPreview(recentMessagesMap);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hey", result.get(user));
    }

    @Test
    void whenGetActiveChat_thenReturnUserWithLatestMessage() {
        Message message1 = new Message(1L, 2L, LocalDateTime.now().minusDays(1), "Hi");
        Message message2 = new Message(1L, 3L, LocalDateTime.now(), "Hello");

        Map<Long, Message> recentMessagesMap = new HashMap<>();
        recentMessagesMap.put(2L, message1);
        recentMessagesMap.put(3L, message2);

        Long result = messageService.getActiveChat(recentMessagesMap);

        assertEquals(3L, result);
    }
}

