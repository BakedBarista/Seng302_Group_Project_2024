package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidEuropeanDecimal;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidationConstants.GARDEN_REGEX;


/**
 * Entity class for garden with name, location and size
 */
@Entity
public class Garden {
    @Transient
    private static Logger logger = LoggerFactory.getLogger(Garden.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Garden name cannot be empty")
    @Pattern(regexp = GARDEN_REGEX, message = "Garden name must only include letters, numbers, spaces, dots, commas, hyphens, or apostrophes")
    @Column(nullable = false)
    private String name;

    @Size(max = 512, message = "Description must be 512 characters or less and contain some text")
    @Pattern(regexp = "^(.*\\p{L}.*)?$" , message = "Description must be 512 characters or less and contain some text")
    private String description;

    @Pattern(regexp = "^([0-9]+[a-zA-Z]?(\\s?\\-?\\s?[0-9]+[a-zA-Z]?)?)?$", message = "Please enter a valid street number")
    @Column
    private String streetNumber;

    @Pattern(regexp = GARDEN_REGEX, message = "Please enter a valid street name")
    @Column
    private String streetName;

    @Pattern(regexp = GARDEN_REGEX, message = "Please enter a valid suburb")
    @Column
    private String suburb;

    @NotBlank(message = "City is required")
    @Pattern(regexp = GARDEN_REGEX, message = "Please enter a valid City name")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Country is required")
    @Pattern(regexp = GARDEN_REGEX, message = "Please enter a valid country name")
    @Column(nullable = false)
    private String country;

    @Pattern(regexp = "^[0-9]*$", message = "Please enter a valid post code")
    @Column
    private String postCode;
    @Column
    private Double lon;
    @Column
    private Double lat;

    @ValidEuropeanDecimal(message = "Garden size must be a valid positive number (only allows numbers and a single period or comma)")
    @Column(nullable = true)
    private String size;

    @Column(nullable = false)
    private Boolean isPublic = false;

    @OneToMany(mappedBy = "garden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Plant> plants;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private GardenUser owner;

    @Column(nullable = false)
    private boolean displayWeatherAlert = true;

    @Column
    private LocalDate alertHidden;

    @Column
    private boolean wateringRecommendation;

    @OneToOne(mappedBy = "garden", cascade = CascadeType.ALL, orphanRemoval = true)
    private GardenWeather gardenWeather;

    @ManyToMany
    @JoinTable(name="garden_tags", joinColumns = @JoinColumn(name="garden_id"), inverseJoinColumns = @JoinColumn(name="tag_id"))
    private Set<Tag> tags = new HashSet<>();

    public Garden() {
    }

    /**
     * Creates a new FormResult object
     * @param gardenName name of garden
     * @param streetNumber street number of garden
     * @param streetName street name of garden
     * @param suburb suburb
     * @param city city
     * @param country country
     * @param postCode postcode
     * @param gardenSize size of garden
     */
    public Garden(String gardenName, String streetNumber, String streetName, String suburb, String city, String country, String postCode, Double lat, Double lon, String gardenSize, String description) {
        this.name = gardenName;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.suburb = suburb;
        this.city = city;
        this.country = country;
        this.postCode = postCode;
        this.lat = lat;
        this.lon = lon;
        this.size = gardenSize;
        this.description = description;
    }
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


    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setSize(String size) {
        this.size = size.replace(',', '.');
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

    public boolean getDisplayWeatherAlert() {
        return displayWeatherAlert;
    }

    public void setDisplayWeatherAlert(boolean displayedToday) {
        this.displayWeatherAlert = displayedToday;
    }

    public boolean getWateringRecommendation() {
        return wateringRecommendation;
    }

    public void setWateringRecommendation(boolean weatherRecommendation) {
        this.wateringRecommendation = weatherRecommendation;
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

    public GardenWeather getGardenWeather() {
        return gardenWeather;
    }

    public void setGardenWeather(GardenWeather gardenWeather) {
        this.gardenWeather = gardenWeather;
    }

    public LocalDate getAlertHidden() {
        return alertHidden;
    }

    public void setAlertHidden(LocalDate alertHidden) {
        this.alertHidden = alertHidden;
    }
}
