package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * Entity class reflecting an entry of user_id, friend_id, status
 */
@Entity
public class Friends {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friend_id;

    @ManyToOne
    @JoinColumn(name = "sender")
    private GardenUser sender;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private GardenUser receiver;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column()
    private LocalDateTime lastReadMessage;

    /**
     * Creates a new FormResult object
     * @param sender first user to be added as a friend
     * @param receiver user to be added as friend to sender
     * @param status pending accepted or denied
     */
    public Friends(GardenUser sender, GardenUser receiver, Status status, LocalDateTime lastReadMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.lastReadMessage = lastReadMessage;
    }

    public Friends() {
    }

    public Long getFriend_id() {
        return friend_id;
    }

    public GardenUser getSender() {
        return sender;
    }

    public GardenUser getReceiver() {
        return receiver;
    }

    public LocalDateTime getReadLastMessage() {
        return lastReadMessage;
    }

    public void setLastReadMessage(LocalDateTime lastReadMessage) {
        this.lastReadMessage = lastReadMessage;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "friends {" +
                "sender=" + sender +
                ", receiver='" + receiver + '\'';
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        DECLINED,
    }
}

