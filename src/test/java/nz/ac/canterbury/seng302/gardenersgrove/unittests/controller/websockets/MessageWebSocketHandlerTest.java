package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.websockets;

import java.io.IOException;
import java.security.Principal;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.gardenersgrove.controller.websockets.MessageWebSocketHandler;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageWebSocketHandlerTest {

    private MessageService messageService;
    private MessageWebSocketHandler testWebSocketHandler;
    private WebSocketSession session1;
    private WebSocketSession session2;
    private Principal principal1;
    private Principal principal2;
    
    @BeforeEach
    void setUp() {
        messageService = mock(MessageService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        testWebSocketHandler = new MessageWebSocketHandler(messageService, objectMapper, validatorFactory);

        session1 = mock(WebSocketSession.class);
        session2 = mock(WebSocketSession.class);
        principal1 = mock(Principal.class);
        principal2 = mock(Principal.class);
    }

    @Test
    void whenPing_thenPong() throws IOException {
        TextMessage subscribe = new TextMessage("{\"type\":\"ping\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);

        verify(session1, times(1)).sendMessage(new TextMessage("{\"type\":\"pong\"}"));
    }

    @Test
    void whenSubscribe_thenSendsState() throws IOException {
        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);

        verify(session1, times(1)).sendMessage(new TextMessage("{\"type\":\"updateMessages\"}"));
    }

    @Test
    void whenSendMessage_thenSavesMessage() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session1.getPrincipal()).thenReturn(principal1);
        when(principal1.getName()).thenReturn("1");

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage increment = new TextMessage("{\"type\":\"sendMessage\",\"receiver\":2,\"message\":\"test\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session1, increment);

        verify(messageService).sendMessage(eq(1L), eq(2L), assertArg((MessageDTO message) -> {
            assertEquals("test", message.getMessage());
        }));
    }

    @Test
    void whenSendInvalidMessage_thenDoesNotSaveMessage() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session1.getPrincipal()).thenReturn(principal1);
        when(principal1.getName()).thenReturn("1");

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        String longMessage = "test.".repeat(40);
        TextMessage increment = new TextMessage("{\"type\":\"sendMessage\",\"receiver\":2,\"message\":\"" + longMessage + "\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session1, increment);

        verify(messageService, never()).sendMessage(any(), any(), any());
    }

    @Test
    void whenSendMessage_thenSendsUpdatedState() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session1.getPrincipal()).thenReturn(principal1);
        when(principal1.getName()).thenReturn("1");

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage increment = new TextMessage("{\"type\":\"sendMessage\",\"receiver\":2,\"message\":\"test\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session1, increment);

        verify(session1, times(2)).sendMessage(new TextMessage("{\"type\":\"updateMessages\"}"));
    }

    @Test
    void whenSendMessage_thenSendsUpdatedStateToAllClients() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);
        when(session1.getPrincipal()).thenReturn(principal1);
        when(session2.getPrincipal()).thenReturn(principal2);
        when(principal1.getName()).thenReturn("1");
        when(principal2.getName()).thenReturn("2");

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage increment = new TextMessage("{\"type\":\"sendMessage\",\"receiver\":2,\"message\":\"test\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session2, subscribe);
        testWebSocketHandler.handleTextMessage(session1, increment);

        verify(session1, times(2)).sendMessage(new TextMessage("{\"type\":\"updateMessages\"}"));
        verify(session2, times(2)).sendMessage(new TextMessage("{\"type\":\"updateMessages\"}"));
    }

    @Test
    void givenConnectionClosed_whenSendMessage_thenDoesNotSendUpdatedState() throws IOException {
        when(session1.isOpen()).thenReturn(false);
        when(session1.getPrincipal()).thenReturn(principal1);
        when(principal1.getName()).thenReturn("1");

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage increment = new TextMessage("{\"type\":\"sendMessage\",\"receiver\":1,\"message\":\"test\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session1, increment);

        verify(session1, times(1)).sendMessage(new TextMessage("{\"type\":\"updateMessages\"}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "[]", "{\"foo\":\"bar\"}", "{\"type\":\"invalid\"}"})
    void whenInvalidMessageReceived_thenDoesNothing(String payload) throws IOException {
        TextMessage invalid = new TextMessage(payload);

        testWebSocketHandler.handleTextMessage(session1, invalid);

        verify(session1, never()).sendMessage(any());
    }
}
