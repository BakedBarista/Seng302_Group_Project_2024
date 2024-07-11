package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;


/**
 * Entity class for garden with name, location and size
 */
@Entity
public class Garden extends BaseGarden {
    @Transient
    private static Logger logger = LoggerFactory.getLogger(Garden.class);

    @Column(nullable = false)
    private boolean displayWeatherAlert = true;

    @Column
    private LocalDate alertHidden;

    @Column
    private boolean wateringRecommendation;

    @OneToOne(mappedBy = "garden", cascade = CascadeType.ALL, orphanRemoval = true)
    private GardenWeather gardenWeather;

    @Column(nullable = true)
    private Double size;

    public Garden() {
    }

    /**
     * Create Garden object from given params
     * @param name
     * @param streetNumber
     * @param streetName
     * @param suburb
     * @param city
     * @param country
     * @param postCode
     * @param lat
     * @param lon
     * @param description
     * @param size
     */
    public Garden(String name, String streetNumber, String streetName, String suburb, String city, String country,
                  String postCode, Double lat, Double lon, String description, Double size) {
        super(name, streetNumber, streetName, suburb, city, country, postCode, lat, lon, description);
        this.size = size;
    }

    /**
     * Creates Garden object from a GardenDTO object
     * @param garden garden to copy data from
     */
    public Garden(BaseGarden garden, Double size) {
        super(garden);
        this.setSize(size);
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    /**
     * handles converting String size to Double size
     * @param size
     */
    public void setSize(String size) {
        if (size == null || size.trim().isEmpty()) {
            this.size = null;
        } else {
            try {
                this.size = Double.parseDouble(size);
            } catch (NumberFormatException e) {
                this.size = null;
            }
        }
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
