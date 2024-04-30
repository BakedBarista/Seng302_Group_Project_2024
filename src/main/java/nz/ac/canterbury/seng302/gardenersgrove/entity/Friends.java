package nz.ac.canterbury.seng302.gardenersgrove.entity;

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
    @JoinColumn(name = "user1_id")
    private GardenUser user1;

    @ManyToOne
    @JoinColumn(name = "user2_id")
    private GardenUser user2;
    
    @Column(nullable = false)
    private String status;

    /**
     * Creates a new FormResult object
     * @param user1 first user to be added as a friend
     * @param user2 user to be added as friend to user1
     * @param status pending accepted or denied
     */
    public Friends(GardenUser user1, GardenUser user2, String status) {
        this.user1 = user1;
        this.user2 = user2;
        this.status = status;
    }

    public Friends() {
    }

    public Long getFriend_id() {
        return friend_id;
    }

    public GardenUser getUser1() {
        return user1;
    }

    public GardenUser getUser2() {
        return user2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "friends {" +
                "user1=" + user1 +
                ", user2='" + user2 + '\'';
    }
}
