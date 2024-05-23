package nz.ac.canterbury.seng302.gardenersgrove.entity.weather;

import jakarta.persistence.Embeddable;

@Embeddable
public class ForecastWeather implements WeatherData {
    private String city;
    private String date;
    private double maxTemp;
    private double minTemp;
    private int humidity;
    private String conditions;
    private String icon;
    private float windSpeed;
    private double precipitation;
    private int uv;

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public double getMaxTemp() {
        return maxTemp;
    }

    @Override
    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    @Override
    public double getMinTemp() {
        return minTemp;
    }

    @Override
    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    @Override
    public int getHumidity() {
        return humidity;
    }

    @Override
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    @Override
    public String getConditions() {
        return conditions;
    }

    @Override
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public float getWindSpeed() {
        return windSpeed;
    }

    @Override
    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    @Override
    public double getPrecipitation() {
        return precipitation;
    }

    @Override
    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    @Override
    public int getUv() {
        return uv;
    }

    @Override
    public void setUv(int uv) {
        this.uv = uv;
    }
}
