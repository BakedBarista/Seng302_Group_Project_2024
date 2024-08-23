package nz.ac.canterbury.seng302.gardenersgrove.entity;

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

    /**
     * For the JPA thing
     */
    public Message() {
    }

    public Message(Long sender, Long receiver, LocalDateTime timestamp, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.messageContent = message;
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

}
