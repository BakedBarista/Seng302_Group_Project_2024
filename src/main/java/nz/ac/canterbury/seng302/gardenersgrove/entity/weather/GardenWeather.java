package nz.ac.canterbury.seng302.gardenersgrove.entity.weather;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;

import java.util.List;

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
    private List<PreviousWeather> previousWeather;

    @ElementCollection
    @CollectionTable(name = "forecast_weather", joinColumns = @JoinColumn(name = "garden_weather_id"))
    private List<ForecastWeather> forecastWeather;

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

    public List<PreviousWeather> getPreviousWeather() {
        return previousWeather;
    }

    public void setPreviousWeather(List<PreviousWeather> previousWeather) {
        this.previousWeather = previousWeather;
    }

    public List<ForecastWeather> getForecastWeather() {
        return forecastWeather;
    }

    public void setForecastWeather(List<ForecastWeather> forecastWeather) {
        this.forecastWeather = forecastWeather;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
