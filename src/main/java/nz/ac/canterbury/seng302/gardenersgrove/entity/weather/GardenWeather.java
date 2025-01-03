package nz.ac.canterbury.seng302.gardenersgrove.entity.weather;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import org.hibernate.annotations.Cascade;

import java.util.List;

/**
 * An entity that creates a database table for the garden weather
 */
@Entity
public class GardenWeather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garden_id")
    private Garden garden;

    @Column
    private Double lat;

    @Column
    private Double lng;

    @ElementCollection
    @CollectionTable(name = "previous_weather", joinColumns = @JoinColumn(name = "garden_weather_id"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<WeatherData> previousWeather;

    @ElementCollection
    @CollectionTable(name = "forecast_weather", joinColumns = @JoinColumn(name = "garden_weather_id"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<WeatherData> forecastWeather;

    @Column
    private String lastUpdated;

    public Long getId() {
        return id;
    }

    public Garden getGarden() {
        return garden;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public List<WeatherData> getPreviousWeather() {
        return previousWeather;
    }

    public void setPreviousWeather(List<WeatherData> previousWeather) {
        this.previousWeather = previousWeather;
    }

    public List<WeatherData> getForecastWeather() {
        return forecastWeather;
    }

    public void setForecastWeather(List<WeatherData> forecastWeather) {
        this.forecastWeather = forecastWeather;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
