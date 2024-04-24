package nz.ac.canterbury.seng302.gardenersgrove.entity;
import jakarta.persistence.*;


@Entity
public class Requests {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sent_user_id")
    private GardenUser sent_user_id;

    @ManyToOne
    @JoinColumn(name = "receive_user_id")
    private GardenUser receive_user_id;

    @Column(nullable = false)
    private String status;
    
    public Requests() {
    }

    /**
     * Creates a new FormResult object
     * @param sent first user to be added as a friend
     * @param receive user to be added as friend to user1
     * @param status pending accepted or denied
     */
    public Requests(GardenUser sent, GardenUser receive, String status) {
        this.receive_user_id = receive;
        this.sent_user_id = sent;
        this.status = status;
    }

    public Long getRequest_id() {
        return id;
    }

    public GardenUser sent_user_id() {
        return sent_user_id;
    }

    public GardenUser receive_user_id() {
        return receive_user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

