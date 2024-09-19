package nz.ac.canterbury.seng302.gardenersgrove.entity.message;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long receiverId;
    private Long userId;
    private LocalDateTime lastReadMessage;

    // Constructors, getters, and setters
    public MessageRead() {}

    public MessageRead(Long receiverId, Long userId) {
        this.receiverId = receiverId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getLastReadMessage() {
        return lastReadMessage;
    }

    public void setLastReadMessage(LocalDateTime lastReadMessage) {
        this.lastReadMessage = lastReadMessage;
    }
}