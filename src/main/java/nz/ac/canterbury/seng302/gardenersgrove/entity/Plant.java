package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import java.time.LocalDate;


/**
 * Entity class for Plants
 */
@Entity
public class Plant extends BasePlant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    protected LocalDate plantedDate;

    public Plant(String name, String count, String description, LocalDate plantedDate) {
        this.name = name;
        this.count = count;
        this.description = description;
        this.plantedDate = plantedDate;
    }

    public Plant(PlantDTO plantDTO) {
        this.name = plantDTO.getName();
        this.count = plantDTO.getCount();
        this.description = plantDTO.getDescription();

        LocalDate localDate = null;
        if (plantDTO.getPlantedDate() != null && !plantDTO.getPlantedDate().isEmpty()) {
            localDate = LocalDate.parse(plantDTO.getPlantedDate());
        }
        this.plantedDate = localDate;
    }

    public Plant() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPlantedDate() {
        return plantedDate;
    }

    public void setPlantedDate(LocalDate plantedDate) {
        this.plantedDate = plantedDate;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", name='" + this.name + '\'' +
                ", count='" + this.count + '\'' +
                ", description='" + this.description + '\'' +
                ", plantedDate='" + this.plantedDate + '\'' +
                '}';
    }
}
