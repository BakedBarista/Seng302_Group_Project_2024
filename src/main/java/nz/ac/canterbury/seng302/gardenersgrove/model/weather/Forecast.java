package nz.ac.canterbury.seng302.gardenersgrove.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * Type class for the Forecast in the Weather Response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Forecast {
    @JsonDeserialize
    @JsonProperty("forecastday")
    private List<ForecastDay> forecastDays;

    public List<ForecastDay> getForecastDays() {
        return forecastDays;
    }

    public void setForecastDays(List<ForecastDay> forecastDays) {
        this.forecastDays = forecastDays;
    }
}
