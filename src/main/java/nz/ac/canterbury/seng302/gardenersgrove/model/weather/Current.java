package nz.ac.canterbury.seng302.gardenersgrove.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Current {
    @JsonDeserialize
    @JsonProperty("location")
    private Location location;

    @JsonDeserialize
    @JsonProperty("temp_c")
    private double currentTemp;

    @JsonDeserialize
    @JsonProperty("humidity")
    private int humidity;

    @JsonDeserialize
    @JsonProperty("precip_mm")
    private double precipitation;

    @JsonDeserialize
    @JsonProperty("uv")
    private int uv;

    @JsonDeserialize
    @JsonProperty("wind_kph")
    private float windSpeed;

    @JsonDeserialize
    @JsonProperty("condition")
    private Condition condition;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(double currentTemp) {
        this.currentTemp = currentTemp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
