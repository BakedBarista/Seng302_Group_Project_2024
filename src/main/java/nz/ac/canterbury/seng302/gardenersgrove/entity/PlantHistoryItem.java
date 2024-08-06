package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Item in the plant history table. History items are created by the user to
 * document changes to a plant.
 */
@Entity(name = "plant_history")
public class PlantHistoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plant")
    private Plant plant;

    @Column()
    private LocalDate timestamp;

    @Size(min = 0, max = 512, message = "Plant history description must be less than 512 characters")
    @Column(nullable = true, length = 512)
    protected String description;

    @Column(nullable = true)
    private String imageContentType;

    @Column(nullable = true, columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] image;

    /**
     * Constructs a plant history item with all fields set to null.
     */
    public PlantHistoryItem() {
    }

    /**
     * Constructs a plant history item with the given plant and timestamp
     */
    public PlantHistoryItem(Plant plant, LocalDate timestamp) {
        this.plant = plant;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public Plant getPlant() {
        return plant;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(String contentType, byte[] image) {
        this.imageContentType = contentType;
        this.image = image;
    }
}
