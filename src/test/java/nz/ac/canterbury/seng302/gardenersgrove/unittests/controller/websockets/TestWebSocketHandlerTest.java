package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.websockets;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.ac.canterbury.seng302.gardenersgrove.controller.websockets.TestWebSocketHandler;

import static org.mockito.Mockito.*;

class TestWebSocketHandlerTest {

    private TestWebSocketHandler testWebSocketHandler;
    private WebSocketSession session1;
    private WebSocketSession session2;
    
    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        testWebSocketHandler = new TestWebSocketHandler(objectMapper);

        session1 = mock(WebSocketSession.class);
        session2 = mock(WebSocketSession.class);
    }

    @Test
    void whenSubscribe_thenSendsState() throws IOException {
        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);

        verify(session1).sendMessage(new TextMessage("{\"type\":\"value\",\"value\":0}"));
    }

    @Test
    void whenIncrement_thenSendsUpdatedState() throws IOException {
        when(session1.isOpen()).thenReturn(true);

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage increment = new TextMessage("{\"type\":\"increment\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session1, increment);

        verify(session1).sendMessage(new TextMessage("{\"type\":\"value\",\"value\":1}"));
    }

    @Test
    void whenIncrement_thenSendsUpdatedStateToAllClients() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage increment = new TextMessage("{\"type\":\"increment\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session2, subscribe);
        testWebSocketHandler.handleTextMessage(session1, increment);

        verify(session1).sendMessage(new TextMessage("{\"type\":\"value\",\"value\":1}"));
        verify(session2).sendMessage(new TextMessage("{\"type\":\"value\",\"value\":1}"));
    }

    @Test
    void givenConnectionClosed_whenIncrement_thenDoesNotSendUpdatedState() throws IOException {
        when(session1.isOpen()).thenReturn(false);

        TextMessage subscribe = new TextMessage("{\"type\":\"subscribe\"}");
        TextMessage increment = new TextMessage("{\"type\":\"increment\"}");

        testWebSocketHandler.handleTextMessage(session1, subscribe);
        testWebSocketHandler.handleTextMessage(session1, increment);

        verify(session1, never()).sendMessage(new TextMessage("{\"type\":\"value\",\"value\":1}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "[]", "{\"foo\":\"bar\"}", "{\"type\":\"invalid\"}"})
    void whenInvalidMessageReceived_thenDoesNothing(String payload) throws IOException {
        TextMessage invalid = new TextMessage(payload);

        testWebSocketHandler.handleTextMessage(session1, invalid);

        verify(session1, never()).sendMessage(any());
    }
}
