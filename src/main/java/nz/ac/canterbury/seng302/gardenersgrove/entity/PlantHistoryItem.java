package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;

/**
 * Item in the plant history table. A new history item is automatically created
 * by PlantService every time a new version of the plant is saved.
 *
 * Each item only contains the fields that have changed since the last version
 * of the plant and all other fields are set to null. This is to allow easy
 * detection of what changed at a given time.
 *
 * This class cannot extend BasePlant as almost all of the fields need to be
 * nullable.
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
    private Instant timestamp;

    @Size(min = 0, max = 512, message = "Plant history description must be less than 512 characters")
    @Column(nullable = false, length = 512)
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
    public PlantHistoryItem(Plant plant, Instant timestamp) {
        this.plant = plant;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public Plant getPlant() {
        return plant;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getimageContentType() {
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
