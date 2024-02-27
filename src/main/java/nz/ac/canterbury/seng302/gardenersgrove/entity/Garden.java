package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;


/**
 * Entity class for garden with name, location and size
 */
@Entity
public class Garden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String size;

    protected Garden() {}

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
