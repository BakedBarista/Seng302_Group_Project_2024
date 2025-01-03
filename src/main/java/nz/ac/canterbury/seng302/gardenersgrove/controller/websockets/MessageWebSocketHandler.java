package nz.ac.canterbury.seng302.gardenersgrove.controller.websockets;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A WebSocketHandler for real-time messaging.
 */
public class MessageWebSocketHandler extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(MessageWebSocketHandler.class);

	private MessageService messageService;
	private ObjectMapper objectMapper;
	private ValidatorFactory validatorFactory;
	private Set<WebSocketSession> activeSessions = new HashSet<>();

	private static final List<String> allowedEmojis = List.of("😂", "😢", "🔥", "💀", "🐝");

	public MessageWebSocketHandler(MessageService messageService, ObjectMapper objectMapper, ValidatorFactory validatorFactory) {
		this.messageService = messageService;
		this.objectMapper = objectMapper;
		this.validatorFactory = validatorFactory;
	}

	/**
	 * Handles an incoming WebSocket message.
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage wsMessage) {
		if (!wsMessage.getPayload().contains("\"type\":\"ping\"")) {
			logger.info("Received message: {}", wsMessage.getPayload());
		}
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

				// Refresh messages on page load to avoid a race condition
				updateMessages(session);
				break;
			case "readMessage":
				Long messageId = message.get("receiver").asLong();
				Long userId = getCurrentUserId(session);
				messageService.setReadTime(messageId, userId);
				logger.info("Messages marked as read by user {}", userId);
				break;
			case "sendMessage":
				Long sender = getCurrentUserId(session);
				Long receiver = message.get("receiver").asLong();
				String messageText = message.get("message").asText();
				logger.info("sendMessage {} {} {}", sender, receiver, messageText);

				MessageDTO messageDTO = new MessageDTO(messageText, null);
				Set<ConstraintViolation<MessageDTO>> errors = validatorFactory.getValidator().validate(messageDTO);
				if (!errors.isEmpty()) {
					logger.error("Invalid message: {}", errors);
					sendError(session, errors, messageText);
					return;
				}
				messageService.sendMessage(sender, receiver, messageDTO);

				updateMessagesBroadcast(List.of(sender, receiver));
				break;
			case "markRead":
				Long currentUserId = getCurrentUserId(session);
				Long unreadMessageCount = messageService.getUnreadMessageCount(currentUserId);

				ObjectNode newMessage = JsonNodeFactory.instance.objectNode();
				newMessage.put("type", "updateUnread");
				newMessage.put("unreadMessageCount", unreadMessageCount);
				sendMessage(session, newMessage);
				break;
			case "ping":
				try {
					session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
				} catch (IOException e) {
					// Ignore errors when responding to ping messages
					return;
				}
				break;
			case "addEmoji":
				Long emojiMessageId = message.get("messageId").asLong();
				String emoji = message.get("emoji").asText();

				if (allowedEmojis.contains(emoji)) {
					Message emojiMessage = messageService.getMessageById(emojiMessageId);
					emojiMessage.setReaction(emoji);
					messageService.save(emojiMessage);

					updateMessagesBroadcast(List.of(emojiMessage.getSender(), emojiMessage.getReceiver()));
				} else {
					logger.warn("unknown emoji: {}", emoji);
				}

				break;
			default:
				logger.error("Unknown message type: {}", message.get("type").asText());
				break;
		}
	}

	/**
	 * Invoked after the WebSocket connection has been closed by either side, or after an error has occurred.
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		activeSessions.remove(session);
	}

	private long getCurrentUserId(WebSocketSession session) {
		Principal principal = session.getPrincipal();
		if (principal == null) {
			logger.error("Session is missing principal");
			throw new IllegalStateException("Websocket session is missing principal");
		}
		return Long.parseLong(principal.getName());
	}

	/**
	 * Sends an `updateMessages` message to each of the given users.
	 * @param userIds A list of user IDs to broadcast the message to
	 */
	public void updateMessagesBroadcast(List<Long> userIds) {
		for (WebSocketSession session : Set.copyOf(activeSessions)) {
			long userId = getCurrentUserId(session);
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
		Long currentUserId = getCurrentUserId(session);
		Long unreadMessageCount = messageService.getUnreadMessageCount(currentUserId);

		ObjectNode message = JsonNodeFactory.instance.objectNode();
		message.put("type", "updateMessages");
		message.put("unreadMessageCount", unreadMessageCount);
		sendMessage(session, message);
	}

	private void sendError(WebSocketSession session, Set<ConstraintViolation<MessageDTO>> errors, String messageText) {
		String error = errors.toArray(new ConstraintViolation[] {})[0].getMessage();

		ObjectNode message = JsonNodeFactory.instance.objectNode();
		message.put("type", "error");
		message.put("error", error);
		message.put("message", messageText);

		sendMessage(session, message);
	}

	private void sendMessage(WebSocketSession session, ObjectNode message) {
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
