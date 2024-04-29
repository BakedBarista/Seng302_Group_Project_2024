package nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherAPIResponse {
    @JsonDeserialize
    @JsonProperty("location")
    Location location;

    @JsonDeserialize
    @JsonProperty("forecast")
    private Forecast forecast;

    public static class Location {
        @JsonDeserialize
        @JsonProperty("name")
        private String locationName;
    }

    public static class Forecast {
        @JsonDeserialize
        @JsonProperty("forecastday")
        private ArrayList<ForecastDay> forecastDays;
        public static class ForecastDay {
            @JsonDeserialize
            @JsonProperty("date")


        }
    }
}
