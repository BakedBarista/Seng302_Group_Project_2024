package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * Entity class for garden with name, location and size
 */
@Entity
public class Garden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please enter a name for your garden.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Please enter a location for your garden.")
    @Column(nullable = false)
    private String location;

    @NotBlank(message = "Please enter a size for your garden.")
    @Column(nullable = false)
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
        this.size = size;
    }

    @Override
    public String toString() {
        return "GardenFormResult{" +
                "id=" + id +
                ", gardenName='" + name + '\'' +
                ", gardenLocation='" + location + '\'' +
                ", gardenSize='" + size + '\'' +
                '}';
    }
}
