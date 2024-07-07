package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidEuropeanDecimal;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
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
    private double size;

    public Garden() {
    }

    /**
     * Creates a new FormResult object
     * @param garden garden to copy data from
     */
    public Garden(GardenDTO garden) {
        this.setName(garden.getName());
        this.setStreetNumber(garden.getStreetNumber());
        this.setStreetName(garden.getStreetName());
        this.setSuburb(garden.getSuburb());
        this.setCity(garden.getCity());
        this.setCountry(garden.getCountry());
        this.setPostCode(garden.getPostCode());
        this.setLat(garden.getLat());
        this.setLon(garden.getLon());
        this.setDescription(garden.getDescription());
        this.size = Double.parseDouble(garden.getSize());
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setSize(String size) {
        this.size = Double.parseDouble(size);
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
