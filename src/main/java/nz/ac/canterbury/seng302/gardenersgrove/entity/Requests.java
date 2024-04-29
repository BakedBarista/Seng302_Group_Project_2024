package nz.ac.canterbury.seng302.gardenersgrove.entity;
import jakarta.persistence.*;


@Entity
public class Requests {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sent_userId")
    private GardenUser sent_userId;

    @ManyToOne
    @JoinColumn(name = "recieve_userId")
    private GardenUser recieve_userId;

    /**
     * Creates a new FormResult object
     * @param user1 first user to be added as a friend
     * @param user2 user to be added as friend to user1
     * @param status pending accepted or denied
     */
    public Requests(GardenUser sent, GardenUser recieve) {
        this.recieve_userId = recieve;
        this.sent_userId = sent;
    }

    public Long getRequest_id() {
        return id;
    }

    public GardenUser getSentUser() {
        return sent_userId;
    }

    public GardenUser getRecieveUser() {
        return recieve_userId;
    }

}

