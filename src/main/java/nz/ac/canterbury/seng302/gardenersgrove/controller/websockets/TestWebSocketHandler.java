package nz.ac.canterbury.seng302.gardenersgrove.controller.websockets;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestWebSocketHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(TestWebSocketHandler.class);

	private ObjectMapper objectMapper;
	private long counter;
	private Set<WebSocketSession> activeSessions = new HashSet<>();

	public TestWebSocketHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage wsMessage) {
		JsonNode message;
		try {
			message = objectMapper.readTree(wsMessage.getPayload());
		} catch (JacksonException e) {
			logger.error("Error decoding message", e);
			return;
		}

		switch (message.get("type").asText()) {
			case "subscribe":
				logger.info("subscribe");
				activeSessions.add(session);
				sendState(session);
				break;
			case "increment":
				logger.info("increment");
				counter++;
				broadcastState();
				break;
		}
	}

	private void broadcastState() {
		for (WebSocketSession session : Set.copyOf(activeSessions)) {
			if (session.isOpen()) {
				sendState(session);
			} else {
				activeSessions.remove(session);
			}
		}
	}

	private void sendState(WebSocketSession session) {
		ObjectNode message = JsonNodeFactory.instance.objectNode();
		message.put("type", "value");
		message.put("value", counter);

		String jsonMessage;
		try {
			jsonMessage = objectMapper.writeValueAsString(message);
		} catch (JacksonException e) {
			logger.error("Error encoding message", e);
			return;
		}

		TextMessage wsMessage = new TextMessage(jsonMessage);
		try {
			session.sendMessage(wsMessage);
		} catch (IOException e) {
			logger.error("Error sending message", e);
			return;
		}
	}

}
