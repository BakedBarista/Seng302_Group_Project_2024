package nz.ac.canterbury.seng302.gardenersgrove.entity.weather;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class CurrentWeather extends BaseWeather {
    private double temp;
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }
}
