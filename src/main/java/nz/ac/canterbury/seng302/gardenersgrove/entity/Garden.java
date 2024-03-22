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

    @NotBlank(message = "Garden name cannot be empty", groups = {ValidationGroups.FirstOrder.class})
    @Pattern(regexp = "^[\\p{L}0-9 .,'-]+$", message = "Garden name must only include letters, numbers, spaces, dots, commas, hyphens, or apostrophes", groups = {ValidationGroups.SecondOrder.class})
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.", groups = {ValidationGroups.SecondOrder.class})
    @Column(nullable = false)
    private String name;


    @NotBlank(message = "Location cannot by empty", groups = {ValidationGroups.FirstOrder.class})
    @Pattern(regexp = "^[A-Za-z0-9 .,'-]+$", message = "Location name must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes", groups = {ValidationGroups.SecondOrder.class})
    @Column(nullable = false)
    private String location;

    @Pattern(regexp = "^[A-Za-z0-9 /-]+$", message = "Please enter a valid street number", groups = {ValidationGroups.SecondOrder.class})
    @Column
    private String streetNumber;

    @Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "Please enter a valid street name", groups = {ValidationGroups.SecondOrder.class})
    @Column
    private String streetName;
    @Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "Please enter a valid Suburb", groups = {ValidationGroups.SecondOrder.class})
    @Column
    private String suburb;
    @NotBlank(message = "City and Country are required", groups = {ValidationGroups.FirstOrder.class})
    @Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "Please enter a valid City name", groups = {ValidationGroups.SecondOrder.class})
    @Column
    private String City;
    @NotBlank(message = "City and Country are required", groups = {ValidationGroups.FirstOrder.class})
    @Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "Please enter a valid country name", groups = {ValidationGroups.SecondOrder.class})
    @Column
    private String country;
    @Pattern(regexp = "^[0-9]+$", message = "Please enter a valid post code", groups = {ValidationGroups.SecondOrder.class})
    @Column
    private String postCode;
    @Column
    private Double lon;
    @Column
    private Double lat;

    @ValidEuropeanDecimal(message = "Garden size must be a positive number", groups = {ValidationGroups.FirstOrder.class})
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

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }


    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
