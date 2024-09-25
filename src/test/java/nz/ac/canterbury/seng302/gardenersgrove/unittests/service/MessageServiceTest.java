package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.ChatPreview;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.MessageRead;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageReadRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {
    private MessageService messageService;
    private MessageRepository messageRepository;
    private Clock clock;
    private Instant timestamp;
    private GardenUserService userService;
    private LocalDateTime fixedTimestamp;
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;


    private MessageReadRepository messageReadRepository;

    @BeforeEach
    public void setUp() {
        messageReadRepository = mock(MessageReadRepository.class);
        messageRepository = mock(MessageRepository.class);
        clock = mock(Clock.class);
        userService = mock(GardenUserService.class);
        messageService = new MessageService(messageRepository, messageReadRepository, clock, userService);

        timestamp = Instant.now();
        when(clock.instant()).thenReturn(timestamp);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        fixedTimestamp = LocalDateTime.of(2024, 9, 22, 12, 0); // Fixed date and time
    }

    @Test
    void whenSendMessage_thenMessageCreatedCorrectly() {
        Long sender = 1L;
        Long receiver = 2L;
        String messageWord = "Hello";
        MessageDTO messageDTO = new MessageDTO(messageWord, "token");

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
        MessageDTO messageDTO = new MessageDTO(messageWord, "token");

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

        Map<GardenUser, ChatPreview> result = messageService.convertToPreview(userId2, recentMessagesMap);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hey", result.get(user).lastMessage());
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

    @Test
    void whenSendImage_thenImageMessageIsSaved() throws IOException {
        Long senderId = 1L;
        Long receiverId = 2L;
        MessageDTO messageDTO = new MessageDTO("Hello with image", "token");
        MockMultipartFile file = new MockMultipartFile("image", "image.png", "image/png", new byte[]{1, 2, 3});

        Message message = messageService.sendImage(senderId, receiverId, messageDTO, file);

        assertNotNull(message);
        assertEquals("Hello with image", message.getMessageContent());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void whenSendImageWithTimestamp_thenMessageCreatedWithTimestamp() throws IOException {
        Long senderId = 1L;
        Long receiverId = 2L;
        MessageDTO messageDTO = new MessageDTO("Hello with image and timestamp", "token");
        MockMultipartFile file = new MockMultipartFile("image", "image.png", "image/png", new byte[]{1, 2, 3});

        Message message = messageService.sendImageWithTimestamp(senderId, receiverId, messageDTO, fixedTimestamp, file);

        assertNotNull(message);
        assertEquals(senderId, message.getSender());
        assertEquals(receiverId, message.getReceiver());
        assertEquals("Hello with image and timestamp", message.getMessageContent());
        assertEquals(fixedTimestamp, message.getTimestamp());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void whenSendImageWithInvalidFile_thenExceptionIsThrown() {
        Long senderId = 1L;
        Long receiverId = 2L;
        MessageDTO messageDTO = new MessageDTO("Invalid image", "token");
        MockMultipartFile file = new MockMultipartFile("image", "image.txt", "text/plain", new byte[]{1, 2, 3});

        IOException exception = assertThrows(IOException.class, () ->
                messageService.sendImageWithTimestamp(senderId, receiverId, messageDTO, fixedTimestamp, file));

        assertEquals("Image is too large or wrong format", exception.getMessage());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void whenValidateImage_thenValidationIsSuccessful() {
        MockMultipartFile validImage = new MockMultipartFile("image", "valid.png", "image/png", new byte[1024]);
        boolean isValid = messageService.validateImage(validImage);
        assertTrue(isValid);
    }

    @Test
    void whenValidateImageWithInvalidType_thenValidationFails() {
        MockMultipartFile invalidImage = new MockMultipartFile("image", "invalid.txt", "text/plain", new byte[1024]);
        boolean isValid = messageService.validateImage(invalidImage);
        assertFalse(isValid);
    }

    @Test
    void whenValidateImageWithOversizedFile_thenValidationFails() {
        byte[] largeImage = new byte[MAX_FILE_SIZE + 1];
        MockMultipartFile oversizedImage = new MockMultipartFile("image", "oversize.png", "image/png", largeImage);
        boolean isValid = messageService.validateImage(oversizedImage);
        assertFalse(isValid);
    }

    @Test
    void testSetReadTime_MessageReadExists() {
        Long receiverId = 1L;
        Long userId = 2L;
        MessageRead existingMessageRead = new MessageRead(receiverId, userId);
        LocalDateTime previousTime = LocalDateTime.of(2023, 9, 20, 10, 0);
        existingMessageRead.setLastReadMessage(previousTime);

        when(messageReadRepository.findByReceiverIdAndUserId(receiverId, userId))
                .thenReturn(Optional.of(existingMessageRead));

        messageService.setReadTime(receiverId, userId);

        verify(messageReadRepository, times(1)).findByReceiverIdAndUserId(receiverId, userId);
        verify(messageReadRepository, times(1)).save(existingMessageRead);


        assertNotEquals(previousTime, existingMessageRead.getLastReadMessage());
        assertTrue(existingMessageRead.getLastReadMessage().isAfter(previousTime));
    }

    @Test
    void testSetReadTime_MessageReadDoesNotExist() {
        Long receiverId = 1L;
        Long userId = 2L;

        // Mock the repository to return empty
        when(messageReadRepository.findByReceiverIdAndUserId(receiverId, userId))
                .thenReturn(Optional.empty());

        messageService.setReadTime(receiverId, userId);


        verify(messageReadRepository, times(1)).findByReceiverIdAndUserId(receiverId, userId);

        // Capture the MessageRead object that was passed to the save method
        ArgumentCaptor<MessageRead> messageReadCaptor = ArgumentCaptor.forClass(MessageRead.class);
        verify(messageReadRepository, times(1)).save(messageReadCaptor.capture());

        MessageRead savedMessageRead = messageReadCaptor.getValue();

        assertNotNull(savedMessageRead.getLastReadMessage());
        assertEquals(receiverId, savedMessageRead.getReceiverId());
        assertEquals(userId, savedMessageRead.getUserId());
    }

    @Test
    void givenIHaveJustReadMyMessages_whenICheckTheUnreadMessagesCount_thenItIsZero() {
        Long receiverId = 1L;
        Long senderId = 1L;

        MessageRead messageRead = new MessageRead(receiverId, senderId);
        messageRead.setLastReadMessage(LocalDateTime.now());
        Mockito.when(messageReadRepository.findByReceiverIdAndUserId(receiverId, senderId))
                .thenReturn(Optional.of(messageRead));

        List<Message> messages = new ArrayList<>(List.of(
                new Message(senderId, receiverId, LocalDateTime.now().minusHours(4), "one"),
                new Message(senderId, receiverId, LocalDateTime.now().minusHours(3), "two"),
                new Message(senderId, receiverId, LocalDateTime.now().minusHours(2), "three"),
                new Message(senderId, receiverId, LocalDateTime.now().minusHours(1), "four")
        ));
        Mockito.when(messageRepository.findMessagesBetweenUsers(receiverId, senderId)).thenReturn(messages);

        Long count = messageService.getCountOfUnreadMessages(receiverId, senderId);

        Assertions.assertEquals(0, count);
    }

    @Test
    void givenIHaveJustReadMyMessages_andMyFriendSendsAnotherMessage_whenICheckTheUnreadMessagesCount_thenItIsOne() {
        Long receiverId = 1L;
        Long senderId = 1L;

        MessageRead messageRead = new MessageRead(receiverId, senderId);
        messageRead.setLastReadMessage(LocalDateTime.now().minusSeconds(1)); // minus 1 seconds so that it is before the 5th msg
        Mockito.when(messageReadRepository.findByReceiverIdAndUserId(receiverId, senderId))
                .thenReturn(Optional.of(messageRead));

        List<Message> messages = new ArrayList<>(List.of(
                new Message(senderId, receiverId, LocalDateTime.now().minusHours(4), "one"),
                new Message(senderId, receiverId, LocalDateTime.now().minusHours(3), "two"),
                new Message(senderId, receiverId, LocalDateTime.now().minusHours(2), "three"),
                new Message(senderId, receiverId, LocalDateTime.now().minusHours(1), "four"),
                new Message(senderId, receiverId, LocalDateTime.now(), "I just sent this now!")
        ));
        Mockito.when(messageRepository.findMessagesBetweenUsers(receiverId, senderId)).thenReturn(messages);

        Long count = messageService.getCountOfUnreadMessages(receiverId, senderId);

        Assertions.assertEquals(1, count);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 5, 99, 9999})
    void givenIHaveNeverReadMyMessages_andMyFriendHasSentMessages_whenICheckTheUnreadMessagesCount_thenItIsEqualToNumberOfMessagesSent(Long numberOfMessages) {
        Long receiverId = 1L;
        Long senderId = 1L;

        MessageRead messageRead = new MessageRead(receiverId, senderId);
        messageRead.setLastReadMessage(null);
        Mockito.when(messageReadRepository.findByReceiverIdAndUserId(receiverId, senderId))
                .thenReturn(Optional.of(messageRead));

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < numberOfMessages; i++) {
            messages.add(new Message(senderId, receiverId, LocalDateTime.now(), "blah blah blah"));
        }
        Mockito.when(messageRepository.findMessagesBetweenUsers(receiverId, senderId)).thenReturn(messages);

        Long count = messageService.getCountOfUnreadMessages(receiverId, senderId);

        Assertions.assertEquals(numberOfMessages, count);
    }
}