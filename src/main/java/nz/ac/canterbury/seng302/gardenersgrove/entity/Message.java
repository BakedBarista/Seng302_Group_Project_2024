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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
