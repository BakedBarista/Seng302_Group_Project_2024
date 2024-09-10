package nz.ac.canterbury.seng302.gardenersgrove.controller.websockets;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.node.JsonNodeType;
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

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;

/**
 * A WebSocketHandler for real-time messaging.
 */
public class MessageWebSocketHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(MessageWebSocketHandler.class);

	private MessageService messageService;
	private ObjectMapper objectMapper;
	private Set<WebSocketSession> activeSessions = new HashSet<>();

	public MessageWebSocketHandler(MessageService messageService, ObjectMapper objectMapper) {
		this.messageService = messageService;
		this.objectMapper = objectMapper;
	}

	/**
	 * Handles an incoming WebSocket message.
	 * @throws IOException 
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage wsMessage) {
		logger.info("Received message: {}", wsMessage.getPayload());
		JsonNode message;
		try {
			message = objectMapper.readTree(wsMessage.getPayload());
		} catch (JacksonException e) {
			logger.error("Error decoding message", e);
			return;
		}

		if (!message.has("type") || message.get("type").getNodeType() != JsonNodeType.STRING) {
			logger.error("Message is missing type field");
			return;
		}

		switch (message.get("type").asText()) {
			case "subscribe":
				logger.info("subscribe");
				activeSessions.add(session);
				updateMessages(session);
				break;
			case "sendMessage":
				Long sender = Long.parseLong(session.getPrincipal().getName());
				Long reciever = message.get("reciever").asLong();
				String messageText = message.get("message").asText();
				logger.info("sendMessage {} {} {}", sender, reciever, messageText);

				// TODO: validate DTO
				MessageDTO messageDTO = new MessageDTO(messageText);
				messageService.sendMessage(sender, reciever, messageDTO);

				updateMessagesBroadcast(List.of(sender, reciever));
				break;
			case "ping":
				try {
					session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
				} catch (IOException e) {
					// Ignore errors when responding to ping messages
					return;
				}
				break;
			default:
				logger.error("Unknown message type: {}", message.get("type").asText());
				break;
		}
	}

	/**
	 * Sends an `updateMessages` message to each of the given users.
	 * @param userIds A list of user IDs to broadcast the message to
	 */
	private void updateMessagesBroadcast(List<Long> userIds) {
		for (WebSocketSession session : Set.copyOf(activeSessions)) {
			long userId = Long.parseLong(session.getPrincipal().getName());
			if (userIds == null || !userIds.contains(userId)) {
				continue;
			}

			if (session.isOpen()) {
				updateMessages(session);
			} else {
				activeSessions.remove(session);
			}
		}
	}

	/**
	 * Sends an `updateMessages` to a given session.
	 * @param session the session to send the message to
	 */
	private void updateMessages(WebSocketSession session) {
		ObjectNode message = JsonNodeFactory.instance.objectNode();
		message.put("type", "updateMessages");

		String jsonMessage;
		try {
			jsonMessage = objectMapper.writeValueAsString(message);
		} catch (JacksonException e) {
			logger.error("Error encoding message", e);
			return;
		}

		TextMessage wsMessage = new TextMessage(jsonMessage);
		logger.info("Sending message: {}", wsMessage.getPayload());
		try {
			session.sendMessage(wsMessage);
		} catch (IOException e) {
			logger.error("Error sending message", e);
		}
	}

}
