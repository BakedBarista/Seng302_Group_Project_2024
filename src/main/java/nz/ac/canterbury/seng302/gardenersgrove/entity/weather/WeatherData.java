package nz.ac.canterbury.seng302.gardenersgrove.entity.weather;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

/**
 * Creates an embeddable object for the weather data
 */
@Embeddable
public class WeatherData extends BaseWeather {
    private LocalDate date;
    private double maxTemp;
    private double minTemp;
    private double precipitation;
    private String type;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}