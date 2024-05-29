package nz.ac.canterbury.seng302.gardenersgrove.model.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * ForecastDay component of the weather JSON response
 */
public class ForecastDay {
    @JsonDeserialize
    @JsonProperty("date")
    private String date;

    @JsonDeserialize
    @JsonProperty("day")
    private Day day;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }
}
