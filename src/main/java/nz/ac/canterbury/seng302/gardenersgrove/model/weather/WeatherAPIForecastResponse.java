package nz.ac.canterbury.seng302.gardenersgrove.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A Type class for the Weather API Response that is used by Jackson to deserialise the JSON api data to a known format.
 * Also prevents any unknown values from being parsed.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherAPIForecastResponse implements WeatherAPIResponse{
    @JsonDeserialize
    @JsonProperty("location")
    private Location location;

    @JsonDeserialize
    @JsonProperty("forecast")
    private Forecast forecast;

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Forecast getForecast() {
        return forecast;
    }

    @Override
    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

}
