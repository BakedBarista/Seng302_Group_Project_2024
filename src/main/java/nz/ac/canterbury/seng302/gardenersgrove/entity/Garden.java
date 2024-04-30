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


    @NotBlank(message = "Location cannot by empty")
    @Pattern(regexp = "^$|^[A-Za-z0-9 .,'-]+$", message = "Location name must only include letters, numbers, spaces, dots, commas, hyphens or apostrophes")
    @Column(nullable = false)
    private String location;

    @Size(max = 512, message = "Description must be 512 characters or less and contain some text")
    @Pattern(regexp = "^.*[a-zA-Z].*|$", message = "Description must be 512 characters or less and contain some text")
    private String description;

    @ValidEuropeanDecimal(message = "Garden size must be a positive number")
    @Column()
    private String size;

    @Column(nullable = false)
    private Boolean isPublic = false;

    private String forecastLastUpdated = null;
    private String timezoneId = null;

    @Lob
    private String weatherForecast;

//    @ElementCollection
//    private List<WeatherForecast> weatherForecast;

//    @Convert(converter = HashMapListConverter.class)
//    private List<Map<String, Object>> weatherForecast;
    public Garden() {
    }

    /**
     * Creates a new FormResult object
     * @param gardenName name of garden
     * @param gardenLocation location of garden
     * @param gardenSize size of garden
     */
    public Garden(String gardenName, String gardenLocation, String gardenSize, String description) {
        this.name = gardenName;
        this.location = gardenLocation;
        this.size = gardenSize;
        this.description = description;
    }
    public Boolean getIsPublic() {
        return isPublic;
    }
    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
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
