package nz.ac.canterbury.seng302.gardenersgrove.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Day {
    @JsonDeserialize
    @JsonProperty("maxtemp_c")
    private double maxTemp;
    @JsonDeserialize
    @JsonProperty("mintemp_c")
    private double minTemp;

    @JsonDeserialize
    @JsonProperty("totalprecip_mm")
    private double precipitation;

    @JsonDeserialize
    @JsonProperty("avghumidity")
    private int humidity;

    @JsonDeserialize
    @JsonProperty("uv")
    private int uv;

    @JsonDeserialize
    @JsonProperty("wind_kph")
    private float windSpeed;

    @JsonDeserialize
    @JsonProperty("condition")
    private Condition condition;


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

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
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
