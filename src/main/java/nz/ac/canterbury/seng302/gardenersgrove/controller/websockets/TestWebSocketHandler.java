package nz.ac.canterbury.seng302.gardenersgrove.controller.websockets;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestWebSocketHandler extends TextWebSocketHandler {

	private ObjectMapper objectMapper = new ObjectMapper();
	private long counter;
	private Set<WebSocketSession> activeSessions = new HashSet<>();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage wsMessage) {
		JsonNode message;
		try {
			message = objectMapper.readTree(wsMessage.getPayload());
		} catch (JacksonException e) {
			e.printStackTrace();
			return;
		}

		switch (message.get("type").asText()) {
			case "subscribe":
				System.out.println("subscribe");
				activeSessions.add(session);
				sendState(session);
				break;
			case "increment":
				System.out.println("increment");
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
			e.printStackTrace();
			return;
		}

		TextMessage wsMessage = new TextMessage(jsonMessage);
		try {
			session.sendMessage(wsMessage);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

}
