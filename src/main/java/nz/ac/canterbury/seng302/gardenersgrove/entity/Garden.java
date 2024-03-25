package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidEuropeanDecimal;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationGroups;



/**
 * Entity class for garden with name, location and size
 */
@Entity
public class Garden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Garden name cannot be empty")
    @Pattern(regexp = "^$|^[\\p{L}0-9 .,'-]+$", message = "Garden name must only include letters, numbers, spaces, dots, commas, hyphens, or apostrophes")
    @Column(nullable = false)
    private String name;


    @NotBlank(message = "Location cannot by empty")
    @Pattern(regexp = "^$|^[A-Za-z0-9 .,'-]+$", message = "Location name must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes")
    @Column(nullable = false)
    private String location;

    @ValidEuropeanDecimal(message = "Garden size must be a positive number")
    @Column(nullable = true)
    private String size;

    public Garden() {}

    /**
     * Creates a new FormResult object
     * @param gardenName name of garden
     * @param gardenLocation location of garden
     * @param gardenSize size of garden
     */
    public Garden(String gardenName, String gardenLocation, String gardenSize) {
        this.name = gardenName;
        this.location = gardenLocation;
        this.size = gardenSize;
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

    public String getLocation() {
        return location;
    }

    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSize(String size) {
        this.size = size.replace(',', '.');
    }

    @Override
    public String toString() {
        return "Garden{" +
                "id=" + id +
                ", gardenName='" + name + '\'' +
                ", gardenLocation='" + location + '\'' +
                ", gardenSize='" + size + '\'' +
                '}';
    }
}
