package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entity class reflec
 */
@Entity(name = "plant_history")
public class PlantHistoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plant")
    private Plant plant;

    public Long getId() {
        return id;
    }

    public Plant getPlant() {
        return plant;
    }
}
