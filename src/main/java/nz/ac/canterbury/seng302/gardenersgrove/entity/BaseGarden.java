package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.*;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationMessages.*;

/**
 * Acts as a skeleton for Garden and GardenDTO, which share a lot of the same fields and methods
 */
@MappedSuperclass
public abstract class BaseGarden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = EMPTY_GARDEN_NAME_MESSAGE)
    @Length(max = 100, message = MAX_GARDEN_NAME_MESSAGE)
    @Pattern(regexp = GARDEN_REGEX, message = INVALID_GARDEN_NAME_MESSAGE)
    @Column(nullable = false)
    private String name;

    @Size(max = 512, message = INVALID_GARDEN_DESCRIPTION)
    @Pattern(regexp = DESCRIPTION_REGEX , message = INVALID_GARDEN_DESCRIPTION)
    private String description;

    @Pattern(regexp = "^([0-9]+[a-zA-Z]?(\\s?\\-?\\s?[0-9]+[a-zA-Z]?)?)?$", message = GARDEN_STREET_NUMBER_MESSAGE)
    @Column
    private String streetNumber;

    @Pattern(regexp = GARDEN_REGEX, message = GARDEN_STREET_NAME_MESSAGE)
    @Column
    private String streetName;

    @Pattern(regexp = GARDEN_REGEX, message = GARDEN_SUBURB_MESSAGE)
    @Column
    private String suburb;

    @NotBlank(message = GARDEN_CITY_REQUIRED_MESSAGE)
    @Pattern(regexp = GARDEN_REGEX, message = GARDEN_CITY_MESSAGE)
    @Column(nullable = false)
    private String city;

    @NotBlank(message = GARDEN_COUNTRY_REQUIRED_MESSAGE)
    @Pattern(regexp = GARDEN_REGEX, message = GARDEN_COUNTRY_MESSAGE)
    @Column(nullable = false)
    private String country;

    @Pattern(regexp = "^[0-9]*$", message = GARDEN_POST_CODE_MESSAGE)
    @Column
    private String postCode;
    @Column
    private Double lon;
    @Column
    private Double lat;

    @Column(nullable = false)
    private Boolean isPublic = false;

    @OneToMany(mappedBy = "garden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Plant> plants;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private GardenUser owner;

    @ManyToMany
    @JoinTable(name="garden_tags", joinColumns = @JoinColumn(name="garden_id"), inverseJoinColumns = @JoinColumn(name="tag_id"))
    private Set<Tag> tags = new HashSet<>();

    public BaseGarden(String name, String streetNumber, String streetName, String suburb, String city,
                      String country, String postCode, Double lat, Double lon, String description) {
        this.name = name;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.suburb = suburb;
        this.city = city;
        this.country = country;
        this.postCode = postCode;
        this.lat = lat;
        this.lon = lon;
        this.description = description;
    }

    /**
     * copy the main (shared) data for a base garden (either garden or gardenDTO)
     * @param garden
     */
    public BaseGarden(BaseGarden garden) {
        if (garden != null) {
            this.id = garden.getId();
            this.name = garden.getName();
            this.streetNumber = garden.getStreetNumber();
            this.streetName = garden.getStreetName();
            this.suburb = garden.getSuburb();
            this.city = garden.getCity();
            this.country = garden.getCountry();
            this.postCode = garden.getPostCode();
            this.lat = garden.getLat();
            this.lon = garden.getLon();
            this.description = garden.getDescription();
        }
    }

    public BaseGarden() {}

    // Getters and setters

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    public void setOwner(GardenUser owner) {
        this.owner = owner;
    }
    public GardenUser getOwner() {
        return owner;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
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
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * Gets a string combining the street number, street name, suburb, city and
     * country
     *
     * @return the address of the garden
     */
    public String getAddress() {
        StringBuilder sb = new StringBuilder();
        if (streetNumber != null && !streetNumber.isEmpty()) {
            sb.append(streetNumber).append(" ");
        }
        if (streetName != null && !streetName.isEmpty()) {
            sb.append(streetName).append(", ");
        }
        if (suburb != null && !suburb.isEmpty()) {
            sb.append(suburb).append(", ");
        }
        if (city != null && !city.isEmpty()) {
            sb.append(city).append(", ");
        }
        if (country != null && !country.isEmpty()) {
            sb.append(country);
        }
        return sb.toString();
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

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Returns a string with the tags of the garden
     *
     * @return a comma separated list of tags
     */
    public String getTagsString() {
        return String.join(",", tags.stream().sorted().map(Tag::getName).toList());
    }

    public String getLocation() {
        return streetNumber + " " + streetName + " " + suburb + " " + city + " " + postCode + " " + country;
    }

    @Override
    public String toString() {
        return "Garden{" +
                "name='" + name + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", streetName='" + streetName + '\'' +
                ", suburb='" + suburb + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postCode='" + postCode + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", description='" + description + '\'' +
                '}';
    }
}