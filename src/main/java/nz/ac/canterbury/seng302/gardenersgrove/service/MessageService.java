package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.ChatPreview;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.MessageRead;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageReadRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.TIMESTAMP_FORMAT;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.WEATHER_CARD_FORMAT_DATE;

/**
 * Service class for handling messaging between users
 */
@Service
public class MessageService {
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final Clock clock;

    private final GardenUserService userService;

    private final MessageReadRepository messageReadRepository;

    private static final Set<String> ACCEPTED_FILE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/svg");
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          MessageReadRepository messageReadRepository,
                          Clock clock,
                          GardenUserService userService) {
        this.messageRepository = messageRepository;
        this.clock = clock;
        this.userService = userService;
        this.messageReadRepository = messageReadRepository;
    }

    /**
     * Sends a message between users and saves it to the database.
     * 
     * @param sender     the message sender
     * @param receiver   the person who will receive the message
     * @param messageDTO the message object
     * @return the message that was sent
     */
    public Message sendMessage(Long sender, Long receiver, MessageDTO messageDTO) {
        LocalDateTime timestamp = clock.instant().atZone(clock.getZone()).toLocalDateTime();
        return sendMessageWithTimestamp(sender, receiver, messageDTO, timestamp);
    }

    /**
     * Send image message
     * @param sender the message sender
     * @param receiver the receiver
     * @param messageDTO message object
     * @param file image file
     * @return the message that was sent
     */
    public Message sendImage(Long sender, Long receiver, MessageDTO messageDTO, MultipartFile file) throws IOException {
        LocalDateTime timestamp = clock.instant().atZone(clock.getZone()).toLocalDateTime();
        return sendImageWithTimestamp(sender, receiver, messageDTO, timestamp,file);
    }

    /**
     * Sends a message between users and saves it to the database - allows the
     * specification of a timestamp.
     * 
     * @param sender     the message sender
     * @param receiver   the person who will receive the message
     * @param messageDTO the message object
     * @return the message that was sent
     */
    public Message sendMessageWithTimestamp(Long sender, Long receiver, MessageDTO messageDTO,
            LocalDateTime timestamp) {
        Message message = new Message(sender, receiver, timestamp, messageDTO.getMessage());
        messageRepository.save(message);
        return message;
    }

    /**
     * Adds timestamp to the message
     * @param sender    the message sender
     * @param receiver  the person who will receive the message
     * @param messageDTO the message object
     * @param file      the image file
     * @return the message that was sent
     */
    public Message sendImageWithTimestamp(Long sender, Long receiver, MessageDTO messageDTO,
                                          LocalDateTime timestamp, MultipartFile file) throws IOException {
        if(validateImage(file)) {
            Message message = new Message(sender, receiver, timestamp, messageDTO.getMessage());
            message.setImage(file.getContentType(),file.getBytes());
            messageRepository.save(message);
            return message;
        } else {
            logger.error("Image is too large or wrong format");
            throw new IOException("Image is too large or wrong format");

        }

    }

    /**
     * Returns a map of saved messages between user1 and user2
     * 
     * @param user1 any user
     * @param user2 any user
     * @return map of messages
     */
    public Map<LocalDate, List<Message>> getMessagesBetweenFriends(Long user1, Long user2) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(user1, user2);
        TreeMap<LocalDate, List<Message>> sortedMessageHash = new TreeMap<>();

        for (Message message : messages) {
            LocalDate date = message.getTimestamp().toLocalDate();
            if (sortedMessageHash.containsKey(date)) {
                sortedMessageHash.get(date).add(message);
            } else {
                sortedMessageHash.put(date, new ArrayList<>(List.of(message)));
            }
        }

        return sortedMessageHash;
    }

    /**
     * Retrieves all recent chats for the specified user.
     *
     * @param user1 the ID of the user whose recent chats are to be retrieved.
     * @return a list of objects representing the recent chats for the specified
     *         user.
     */
    public List<Message> findAllRecentChats(Long user1) {
        return messageRepository.findAllRecentChats(user1);
    }

    /**
     * Retrieves the ID of the other user involved in a message.
     *
     * @param userId  the ID of the user whose counterpart in the message is to be
     *                found
     * @param message the object containing the sender and receiver information.
     * @return the ID of the other user in the message
     */
    public Long getOtherUserId(Long userId, Message message) {
        if (message.getSender().equals(userId)) {
            return message.getReceiver();
        } else if (message.getReceiver().equals(userId)) {
            return message.getSender();
        } else {
            return null;
        }
    }

    /**
     * Retrieves the latest message for each user from a list of all messages.
     *
     * The sorting approach used here was suggested by ChatGPT, which helped us sort
     * the map by timestamp to prioritise recent messages.
     *
     * @param allMessages    the list of all messages to be processed.
     * @param loggedInUserId the ID of the logged-in user to exclude from the
     *                       results.
     * @return a map where the key is the ID of the other user and the value is
     *         their most recent MESSAGE
     */

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
                .sorted((entry1, entry2) -> entry2.getValue().getTimestamp()
                        .compareTo(entry1.getValue().getTimestamp()))
                .toList();

        Map<Long, Message> sortedMessagesMap = new LinkedHashMap<>();
        for (Map.Entry<Long, Message> entry : sortedEntries) {
            sortedMessagesMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMessagesMap;
    }

    /**
     * Converts a map of recent messages into a map of user previews.
     * @param receiverId id of the user who is requesting chat preview
     * @param recentMessagesMap a map of user IDs to their most recent message.
     * @return a map where each key is a user and each value is their most recent
     *         message.
     */
    public Map<GardenUser, ChatPreview> convertToPreview(Long receiverId, Map<Long, Message> recentMessagesMap) {
        Map<GardenUser, ChatPreview> recentChats = new LinkedHashMap<>();

        if (recentMessagesMap == null) {
            return recentChats;
        }

        for (Map.Entry<Long, Message> entry : recentMessagesMap.entrySet()) {
            Long senderId = entry.getKey();

            Long unreadMessages = getCountOfUnreadMessages(receiverId, senderId);
            Message msg = entry.getValue();


            GardenUser user = userService.getUserById(senderId);
            ChatPreview feedPreview = new ChatPreview(msg.getMessageContent(), unreadMessages);
            recentChats.put(user, feedPreview);
        }
        return recentChats;
    }

    /**
     * Retrieves the ID of the user with the most recent message from a map of
     * messages.
     *
     * @param recentMessagesMap a map where the key is a user ID and the value is
     *                          their most recent message
     * @return the ID of the user with the most recent message
     */
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

    /**
     * Populates the model with attributes necessary for rendering the message page.
     * 
     * @param model           the model object to be populated with attributes
     * @param loggedInUserId  the ID of the currently logged-in user
     * @param requestedUserId the ID of the user with whom the messages are being
     *                        exchanged
     * @param sentToUser      the GardenUser object representing the recipient of
     *                        the messages
     * @param recentChats     a map of users and their most recent messages for chat
     *                        previews
     * @param submissionToken a unique token used to prevent duplicate form
     *                        submissions
     */
    public void setupModelAttributes(Model model, Long loggedInUserId, Long requestedUserId, GardenUser sentToUser,
            Map<GardenUser, ChatPreview> recentChats, String submissionToken) {
        model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
        model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
        model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
        model.addAttribute("submissionToken", submissionToken);
        model.addAttribute("messagesMap", getMessagesBetweenFriends(loggedInUserId, requestedUserId));
        model.addAttribute("sentToUser", sentToUser);
        model.addAttribute("recentChats", recentChats);
        model.addAttribute("activeChat", requestedUserId);
    }

    public boolean validateImage(MultipartFile plantImage) {
        return ACCEPTED_FILE_TYPES.contains(plantImage.getContentType())
                && (plantImage.getSize() <= MAX_FILE_SIZE);
    }

    /**
     * Sets the last read time for the user who is viewing the messages
     * @param receiverId the user's id who is viewing the messages
     * @param userId the id of the user whose messages are being read
     */
    public void setReadTime(Long receiverId, Long userId) {
        logger.info("user {} read messages from user {}", receiverId, userId);
        Optional<MessageRead> optionalMessageRead = messageReadRepository.findByReceiverIdAndUserId(receiverId, userId);
        MessageRead messageRead = optionalMessageRead.orElseGet(() -> new MessageRead(receiverId, userId));
        messageRead.setLastReadMessage(LocalDateTime.now(clock));
        messageReadRepository.save(messageRead);
    }

    /**
     * Gets a count of unread messages from the senderId to the receiverId
     * @param receiverId id of the receiver
     * @param senderId id of the sender
     * @return count of unread messages from sender to receiver
     */
    public Long getCountOfUnreadMessages(Long receiverId, Long senderId) {
        Optional<MessageRead> optionalMessageRead = messageReadRepository.findByReceiverIdAndUserId(receiverId, senderId);
        MessageRead messageRead = optionalMessageRead.orElseGet(() -> new MessageRead(receiverId, senderId));
        LocalDateTime lastRead = messageRead.getLastReadMessage();
        List<Message> messages = messageRepository.findMessagesBetweenUsers(receiverId, senderId);

        if (lastRead == null) {
            return (long) messages.size();
        } else {
            return messages.stream().filter(message -> message.getTimestamp().isAfter(lastRead)).count();
        }
    }

    /**
     * Calculates all unread messages for the user
     * @param userId User ID to calculate the total number of unread message
     * @return Total number of unread messages for the user
     */
    public Long getUnreadMessageCount(Long userId) {
        List<MessageRead> messageReads = messageReadRepository.findAllByUserId(userId);
        Long unreadMessageCount = 0L;
        for (MessageRead messageRead : messageReads) {
            unreadMessageCount = messageRepository.countAllUnreadMessagesAfter(userId,messageRead.getLastReadMessage());
        }
        return unreadMessageCount;

    }

    /**
     * Removes chat history between two users
     * @param userId UserId
     * @param friendId FriendId
     */
    public void removeMessageHistory(Long userId, Long friendId) {
        logger.info("removing history");
        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId,friendId);
        messageRepository.deleteAll(messages);
    }


    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }
}
