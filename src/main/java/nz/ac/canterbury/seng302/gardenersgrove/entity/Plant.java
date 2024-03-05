package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;


/**
 * Entity class for Plants
 */

@Entity
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please enter a name")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Count cannot be null")
    @Min(value = 1, message = "Count must be greater than 0")
    @Column(nullable = false)
    private int count;

    @NotBlank(message = "Please enter a description")
    @Size(min = 1, max = 512, message = "Description must be less than 512 characters")
    @Column(nullable = false)
    private String description;

    @NotBlank(message = "Please enter a date")
    @Column(nullable = false)
    private String plantedDate;

    @ManyToOne
    @JoinColumn
    private Garden garden;

    public Plant() {}

    public Plant(String name, int count, String description, String plantedDate) {
        this.name = name;
        this.count = count;
        this.description = description;
        this.plantedDate = plantedDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlantedDate() {
        return plantedDate;
    }

    public void setPlantedDate(String plantedDate) {
        this.plantedDate = plantedDate;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }

    public Garden getGarden() {
        return garden;
    }


    @Override
    public String toString() {
        return "GardenFormResult{" +
                "id=" + id +
                ", name='" + this.name + '\'' +
                ", count='" + this.count + '\'' +
                ", description ='" + this.description + '\'' +
                ", plantedDate ='" + this.plantedDate + '\'' +
                '}';
    }

}
