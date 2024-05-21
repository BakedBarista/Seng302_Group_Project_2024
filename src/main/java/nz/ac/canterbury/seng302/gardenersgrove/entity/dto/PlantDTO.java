package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidDate;

import java.time.LocalDate;

import static nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidationConstants.GARDEN_REGEX;



/**
 * DTO for plants
 */

@Entity
public class PlantDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes")
    @Pattern(regexp = GARDEN_REGEX, message = "Plant name cannot by empty and must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes")
    @Column(nullable = false)
    private String name;

    @Pattern(regexp = "^[0-9]*$", message = "Plant count must be a positive number")
    @Column(nullable = false)
    private String count;

    @Size(min = 0, max = 512, message = "Plant description must be less than 512 characters")
    @Column(nullable = false, length = 512)
    private String description;

    @Column()
    @ValidDate()
    private String plantedDate;

    @ManyToOne
    @JoinColumn
    private Garden garden;

    @Column(nullable = true)
    private String plantImageContentType;

    @Column(nullable = true, columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] plantImage;

    public PlantDTO(String name, String count, String description, String plantedDate) {
        this.name = name;
        this.count = count;
        this.description = description;
        this.plantedDate = plantedDate;
    }

    public PlantDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
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

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", name='" + this.name + '\'' +
                ", count='" + this.count + '\'' +
                ", description ='" + this.description + '\'' +
                ", plantedDate ='" + this.plantedDate + '\'' +
                '}';
    }
}
