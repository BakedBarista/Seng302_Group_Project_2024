package nz.ac.canterbury.seng302.gardenersgrove.entity.message;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Handles the storage of messages between users
 */
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long sender;

    @Column
    private Long receiver;

    @Column
    private LocalDateTime timestamp;

    @Column(length = 512)
    private String messageContent;

    @Column
    private String imageContentType;

    @Column(columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] imageContent;

    /**
     * Empty constructor - required for JPA since this is an @Entity
     */
    public Message() {
    }

    public Message(Long sender, Long receiver, LocalDateTime timestamp, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.messageContent = message;
    }

    public Long getId() {
        return id;
    }

    public Long getSender() {
        return sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getImageContentType() { return imageContentType; }

    public byte[] getImageContent() { return imageContent; }

    public void setImage(String imageContentType, byte[] imageContent) {
        this.imageContentType = imageContentType;
        this.imageContent = imageContent;
    }

    /**
     * To string for logging and debugging
     * @return string version of Message object
     */
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + sender +
                ", receiverId=" + receiver +
                ", timestamp=" + timestamp +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }

}
