package nz.ac.canterbury.seng302.gardenersgrove.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * Type class for the location in the Weather API responses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    @JsonDeserialize
    @JsonProperty("name")
    private String locationName;

    @JsonDeserialize
    @JsonProperty("tz_id")
    private String timezoneId;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = timezoneId;
    }
}
