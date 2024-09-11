package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


/**
 * Entity class for Plants
 */
@Entity
public class Plant extends BasePlant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    protected Garden garden;

    @Column()
    protected LocalDate plantedDate;

    @Column(nullable = true)
    protected String plantImageContentType;

    @Column(nullable = true, columnDefinition = "MEDIUMBLOB")
    @Lob
    protected byte[] plantImage;

    @JsonIgnore
    @OneToMany(mappedBy = "plant")
    private Set<PlantHistoryItem> history = new HashSet<>();

    public Plant(String name, String count, String description, LocalDate plantedDate) {
        this.name = name;
        this.count = count;
        this.description = description;
        this.plantedDate = plantedDate;
    }

    public Plant(PlantDTO plantDTO) {
        plantDTO.normalize();
        this.name = plantDTO.getName();
        this.count = plantDTO.getCount();
        this.description = plantDTO.getDescription();
        this.plantedDate = plantDTO.getParsedPlantedDate();
    }

    public Plant() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Garden getGarden() {
        return garden;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }

    public LocalDate getPlantedDate() {
        return plantedDate;
    }

    public void setPlantedDate(LocalDate plantedDate) {
        this.plantedDate = plantedDate;
    }

    public String getPlantImageContentType() {
        return plantImageContentType;
    }

    public byte[] getPlantImage() {
        return plantImage;
    }

    public void setPlantImage(String contentType, byte[] plantImage) {
        this.plantImageContentType = contentType;
        this.plantImage = plantImage;
    }

    public Set<PlantHistoryItem> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", name='" + this.name + '\'' +
                ", count='" + this.count + '\'' +
                ", description='" + this.description + '\'' +
                ", plantedDate='" + this.plantedDate + '\'' +
                ", favouritedGardenUser='" + this.getFavourite() + '\'' +
                '}';
    }
}
