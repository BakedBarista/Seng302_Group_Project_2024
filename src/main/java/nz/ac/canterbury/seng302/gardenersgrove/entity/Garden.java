package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidEuropeanDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    @Pattern(regexp = "^$|^[\\p{L}0-9 .,'-]+$", message = "Garden name must only include letters, numbers, spaces, dots, commas, hyphens, or apostrophes")
    @Column(nullable = false)
    private String name;

    @Size(max = 512, message = "Description must be 512 characters or less and contain some text")
    @Pattern(regexp = "^.*[a-zA-Z].*|$", message = "Description must be 512 characters or less and contain some text")
    private String description;

    @Pattern(regexp = "^(|([0-9]+[a-zA-Z]?(\\s?\\-?\\s?[0-9]+[a-zA-Z]?)?))$", message = "Please enter a valid street number")
    @Column
    private String streetNumber;

    @Pattern(regexp = "^(|([a-zA-Z0-9 ,.'-]+))$", message = "Please enter a valid street name")
    @Column
    private String streetName;

    @Pattern(regexp = "^(|([a-zA-Z0-9 ,.'-]+))$", message = "Please enter a valid suburb")
    @Column
    private String suburb;

    @NotBlank(message = "City and Country are required")
    @Pattern(regexp = "^$|^[\\p{L}0-9 .,'-]+$", message = "Please enter a valid City name")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "City and Country are required")
    @Pattern(regexp = "^$|^[\\p{L}0-9 .,'-]+$", message = "Please enter a valid country name")
    @Column(nullable = false)
    private String country;

    @Pattern(regexp = "^(|([0-9]+))$", message = "Please enter a valid post code")
    @Column
    private String postCode;
    @Column
    private Double lon;
    @Column
    private Double lat;

    @ValidEuropeanDecimal(message = "Garden size must be a positive number")
    @Column(nullable = true)
    private String size;

    @Column(nullable = false)
    private Boolean isPublic = false;

    @OneToMany(mappedBy = "garden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Plant> plants;
    private String forecastLastUpdated = null;
    private String timezoneId = null;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private GardenUser owner;

    @Lob
    private String weatherForecast;

    @Lob
    private String weatherPrevious;

    @Column(nullable = false)
    private boolean displayWeatherAlert = true;

    @Column
    private boolean wateringRecommendation;

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

    /**
     * Gets the weather forecast from the Database and deserialises the JSON to a List of Maps
     * @return the weather forecast in a list of maps
     */
    public List<Map<String, Object>> getWeatherForecast() {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (weatherForecast == null) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(weatherForecast, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON", e);
            return Collections.emptyList();
        }
    }

    /**
     * Sets the weather forecast in the database and serialises the forecast data into a JSON string
     * @param weatherForecast the weather data a List of Maps
     */
    public void setWeatherForecast(List<Map<String, Object>> weatherForecast) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            this.weatherForecast = objectMapper.writeValueAsString(weatherForecast);

        } catch (JsonProcessingException e) {
            logger.error("Issue converting weather forecast back into a String", e);
            this.weatherForecast = "";
        }
    }

    /**
     * Gets the previous weather from the Database and deserialises the JSON to a List of Maps
     * @return the weather forecast in a list of maps
     */
    public List<Map<String, Object>> getWeatherPrevious() {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (weatherForecast == null) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(weatherForecast, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON", e);
            return Collections.emptyList();
        }
    }

    /**
     * Sets the previous weather in the database and serialises the forecast data into a JSON string
     * @param weatherPrevious the weather data a List of Maps
     */
    public void setWeatherPrevious(List<Map<String, Object>> weatherPrevious) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            this.weatherPrevious = objectMapper.writeValueAsString(weatherPrevious);

        } catch (JsonProcessingException e) {
            logger.error("Issue converting weather forecast back into a String", e);
            this.weatherPrevious = "";
        }
    }

    public String getForecastLastUpdated() {
        return forecastLastUpdated;
    }

    public void setForecastLastUpdated(String forecastLastUpdated) {
        this.forecastLastUpdated = forecastLastUpdated;
    }

    public String getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = timezoneId;
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

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }
    public List<Plant> getPlants() {
        return plants;
    }
}
