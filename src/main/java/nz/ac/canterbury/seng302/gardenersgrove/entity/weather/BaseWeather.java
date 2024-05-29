package nz.ac.canterbury.seng302.gardenersgrove.entity.weather;

import jakarta.persistence.MappedSuperclass;

/**
 * Shared base-class for common weather attributes
 */
@MappedSuperclass
public abstract class BaseWeather {
    private String city;
    private int humidity;
    private String conditions;
    private String icon;
    private float windSpeed;
    private int uv;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

}
