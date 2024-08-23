package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for handling messaging between users
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final Clock clock;

    @Autowired
    public MessageService(MessageRepository messageRepository, Clock clock) {
        this.messageRepository = messageRepository;
        this.clock = clock;
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
        Message message = new Message(sender, receiver, timestamp, messageDTO.getMessage());
        messageRepository.save(message);
        return message;
    }

    /**
     * Find's a message by it's unique ID
     * @param messageId the unique ID of a message
     * @return an optional of the message at that ID.
     */
    public Optional<Message> getMessageById(Long messageId) {
        return messageRepository.findById(messageId);
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
}
