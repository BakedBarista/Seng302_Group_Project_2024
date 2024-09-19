package nz.ac.canterbury.seng302.gardenersgrove.entity.message;

/**
 * Simple store for storing data that is shown on the feed
 * @param lastMessage string of the last sent messages
 * @param unreadMessages number of messages unread by the viewing user
 */
public record ChatPreview(String lastMessage, Long unreadMessages) {
}
