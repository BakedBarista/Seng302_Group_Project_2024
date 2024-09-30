package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.ChatFeedController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("integration-tests")
class ChatFeedControllerTest {
    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatFeedController chatFeedController;

    @Autowired
    private GardenUserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    private GardenUser user1;
    private GardenUser user2;

    @BeforeEach
    void setup() {
        String dateString = "2001-01-01";
        LocalDate localDate = LocalDate.parse(dateString.trim(), DateTimeFormatter.ISO_LOCAL_DATE);

        user1 = userRepository.save(new GardenUser("John", "Doe", "postTester@gmail.com", "Password1!", localDate));
        user2 = userRepository.save(new GardenUser("test", "Doe", "test@gmail.com", "Password1!", localDate));

        Authentication auth = new UsernamePasswordAuthenticationToken(user1.getId(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void cleanup() {
        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @Test
    void testMessageRecentChat_ReturnsRecentChats() {
        Message message1 = new Message(user1.getId(), user2.getId(), LocalDateTime.now().minusDays(2), "Hello");
        Message message2 = new Message(user1.getId(), user2.getId(), LocalDateTime.now().minusDays(2), "Test");
        messageRepository.save(message1);
        messageRepository.save(message2);

        Map<String, Message> recentChats = chatFeedController.messageRecentChat(
                SecurityContextHolder.getContext().getAuthentication(),
                null,
                null
        );

        assertEquals(1, recentChats.size());
        assertEquals(message2.getMessageContent(), recentChats.get(String.valueOf(user2.getId())).getMessageContent());
    }

    @Test
    void testMessageRecentChat_ReturnsNullWhenImahe() {
        Message message1 = new Message(user1.getId(), user2.getId(), LocalDateTime.now().minusDays(2), null, "image/png", "fake-image-content".getBytes());
        Message message2 = new Message(user1.getId(), user2.getId(), LocalDateTime.now().minusDays(2), null, "image/png", "fake-image-content".getBytes());
        messageRepository.save(message1);
        messageRepository.save(message2);

        Map<String, Message> recentChats = chatFeedController.messageRecentChat(
                SecurityContextHolder.getContext().getAuthentication(),
                null,
                null
        );

        assertEquals(1, recentChats.size());
        assertEquals(message2.getMessageContent(), recentChats.get(String.valueOf(user2.getId())).getMessageContent());
    }

}
