package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.gardenersgrove.controller.websockets.MessageWebSocketHandler;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.*;

@SuppressWarnings("SpringJavaInjectionsPointsAutowiringInspection")
@SpringBootTest
public class MessageWebSocketHandlerTest {

    @Autowired
    private MessageService messageService;

    private MessageWebSocketHandler testWebSocketHandler;
    private WebSocketSession session1;
    private WebSocketSession session2;
    private Principal principal1;
    private Principal principal2;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        testWebSocketHandler = new MessageWebSocketHandler(messageService, objectMapper, validatorFactory);

        session1 = mock(WebSocketSession.class);
        session2 = mock(WebSocketSession.class);
        principal1 = mock(Principal.class);
        principal2 = mock(Principal.class);
    }


    @Test
    void whenAddEmoji_thenSavesEmojiToMessage() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session1.getPrincipal()).thenReturn(principal1);
        when(principal1.getName()).thenReturn("1");

        Long senderId = 2L;
        Long receiverId = 1L;
        String messageContent = "whenAddEmoji_thenSavesEmojiToMessage";
        MessageDTO messageDTO = new MessageDTO(messageContent, "token");
        Message savedMessage = messageService.sendMessage(senderId, receiverId, messageDTO);

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage addEmojiMessage = new TextMessage("{\"type\":\"addEmoji\",\"messageId\":"+savedMessage.getId()+",\"emoji\":\"🐝\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session1, addEmojiMessage);

        List<Message> messages = messageService.findAllRecentChats(receiverId);
        Message message = messages.stream().filter(m -> m.getMessageContent().equals(messageContent)).findFirst().get();
        Assertions.assertEquals("🐝", message.getReaction());
    }

    @Test
    void whenAddInvalidEmoji_thenSavesEmojiToMessage() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session1.getPrincipal()).thenReturn(principal1);
        when(principal1.getName()).thenReturn("1");

        Long senderId = 2L;
        Long receiverId = 1L;
        String messageContent = "whenAddInvalidEmoji_thenSavesEmojiToMessage";
        MessageDTO messageDTO = new MessageDTO(messageContent, "token");
        Message savedMessage = messageService.sendMessage(senderId, receiverId, messageDTO);

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage addEmojiMessage = new TextMessage("{\"type\":\"addEmoji\",\"messageId\":"+savedMessage.getId()+",\"emoji\":\"lol\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session1, addEmojiMessage);

        List<Message> messages = messageService.findAllRecentChats(receiverId);
        Message message = messages.stream().filter(m -> m.getMessageContent().equals(messageContent)).findFirst().get();
        Assertions.assertNull(message.getReaction());
    }
}
