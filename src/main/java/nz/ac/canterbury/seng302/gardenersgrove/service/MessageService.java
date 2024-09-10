package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling messaging between users
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final Clock clock;

    private final GardenUserService userService;

    @Autowired
    public MessageService(MessageRepository messageRepository, Clock clock, GardenUserService userService) {
        this.messageRepository = messageRepository;
        this.clock = clock;
        this.userService = userService;
    }

    /**
     * Sends a message between users and saves it to the database.
     * @param sender the message sender
     * @param receiver the person who will receive the message
     * @param messageDTO the message object
     * @return the message that was sent
     */
    public Message sendMessage(Long sender, Long receiver, MessageDTO messageDTO) {
        LocalDateTime timestamp = clock.instant().atZone(clock.getZone()).toLocalDateTime();
        return sendMessageWithTimestamp(sender, receiver, messageDTO, timestamp);
    }

    /**
     * Sends a message between users and saves it to the database - allows the specification of a timestamp.
     * @param sender the message sender
     * @param receiver the person who will receive the message
     * @param messageDTO the message object
     * @return the message that was sent
     */
    public Message sendMessageWithTimestamp(Long sender, Long receiver, MessageDTO messageDTO, LocalDateTime timestamp) {
        Message message = new Message(sender, receiver, timestamp, messageDTO.getMessage());
        messageRepository.save(message);
        return message;
    }

    /**
     * Returns a map of saved messages between user1 and user2
     * @param user1 any user
     * @param user2 any user
     * @return map of messages
     */
    public Map<LocalDate, List<Message>> getMessagesBetweenFriends(Long user1, Long user2) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(user1, user2);
        TreeMap<LocalDate, List<Message>> sortedMessageHash = new TreeMap<>();

        for (Message message: messages) {
            LocalDate date = message.getTimestamp().toLocalDate();
            if (sortedMessageHash.containsKey(date)) {
                sortedMessageHash.get(date).add(message);
            } else {
                sortedMessageHash.put(date, new ArrayList<>(List.of(message)));
            }
        }

        return sortedMessageHash;
    }

    public List<Message> findAllRecentChats(Long user1){
        return messageRepository.findAllRecentChats(user1);
    }

    public Long getOtherUserId(Long userId, Message message) {
        if (message.getSender().equals(userId)) {
            return message.getReceiver();
        } else if (message.getReceiver().equals(userId)) {
            return message.getSender();
        } else {
            return null;
        }
    }

    public Map<Long, Message> getLatestMessages(List<Message> allMessages, Long loggedInUserId) {
        Map<Long, Message> recentMessagesMap = new HashMap<>();

        for (Message message : allMessages) {
            Long otherUserId = getOtherUserId(loggedInUserId, message);

            if (otherUserId != null) {
                Message existingMessage = recentMessagesMap.get(otherUserId);
                if (existingMessage == null || message.getTimestamp().isAfter(existingMessage.getTimestamp())) {
                    recentMessagesMap.put(otherUserId, message);
                }
            }
        }

        List<Map.Entry<Long, Message>> sortedEntries = recentMessagesMap.entrySet()
            .stream()
            .sorted((entry1, entry2) -> entry2.getValue().getTimestamp().compareTo(entry1.getValue().getTimestamp()))
            .collect(Collectors.toList());

        Map<Long, Message> sortedMessagesMap = new LinkedHashMap<>();
        for (Map.Entry<Long, Message> entry : sortedEntries) {
            sortedMessagesMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMessagesMap;
    }

    public Map<GardenUser, String> convertToPreview(Map<Long, Message> recentMessagesMap) {
        Map<GardenUser, String> recentChats = new LinkedHashMap<>();

        for (Map.Entry<Long, Message> entry : recentMessagesMap.entrySet()) {
            Long userId = entry.getKey();
            Message msg = entry.getValue();

            GardenUser user = userService.getUserById(userId);
            String messagePreview = msg.getMessageContent();

            recentChats.put(user, messagePreview);
        }
        return recentChats;
    }

    public Long getActiveChat(Map<Long, Message> recentMessagesMap) {
        Long latestUserId = null;
        LocalDateTime latestTimestamp = null;
        for (Map.Entry<Long, Message> entry : recentMessagesMap.entrySet()) {
            Long userId = entry.getKey();
            Message message = entry.getValue();
            if (latestTimestamp == null || message.getTimestamp().isAfter(latestTimestamp)) {
                latestTimestamp = message.getTimestamp();
                latestUserId = userId;
            }
        }
        return latestUserId;
    }
}
