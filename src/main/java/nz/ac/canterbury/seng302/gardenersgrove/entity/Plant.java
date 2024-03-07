package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


/**
 * Entity class for Plants
 */

@Entity
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please enter a name")
    @Pattern(regexp = "^[a-zA-Z0-9 \\-.,']*$", message = "Name must only contain letters and numbers")
    @Column(nullable = false)
    private String name;

//    @NotNull(message = "Count cannot be null")
    @Min(value = 1, message = "Count must be greater than 0")
    @Column(nullable = false)
    private Integer count;

//    @NotBlank(message = "Please enter a description")
    @Size(min = 1, max = 511, message = "Description must be less than 512 characters")
    @Column(nullable = false)
    private String description;

//    @NotBlank(message = "Please enter a date")
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$", message = "Date must be in DD/MM/YYYY format")
    @Column(nullable = false)
    private String plantedDate;

    @ManyToOne
    @JoinColumn
    private Garden garden;

    @Column(nullable = true)
    private String plantImagePath;

    public Plant(String name, int count, String description, String plantedDate) {
        this.name = name;
        this.count = count;
        this.description = description;
        this.plantedDate = plantedDate;
    }

    public Plant() {}

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

    public String getPlantImagePath() {return plantImagePath;}

    public void setPlantImagePath(String plantImagePath) { this.plantImagePath = plantImagePath;}

    @Override
    public String toString() {
        return "PlantFormResult{" +
                "id=" + id +
                ", name='" + this.name + '\'' +
                ", count='" + this.count + '\'' +
                ", description ='" + this.description + '\'' +
                ", plantedDate ='" + this.plantedDate + '\'' +
                '}';
    }

}
