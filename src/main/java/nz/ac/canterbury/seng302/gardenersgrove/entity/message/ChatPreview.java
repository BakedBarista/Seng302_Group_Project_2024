package nz.ac.canterbury.seng302.gardenersgrove.entity.message;

/**
 * Simple class for storing data that is shown on the feed
 */
public class ChatPreview {

    private final String lastMessage;

    private final Long unreadMessages;

    public ChatPreview(String lastMessage, Long unreadMessages) {
        this.lastMessage = lastMessage;
        this.unreadMessages = unreadMessages;
    }

    public Long getUnreadMessages() {
        return unreadMessages;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
