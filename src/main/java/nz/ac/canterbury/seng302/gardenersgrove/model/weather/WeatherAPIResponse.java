package nz.ac.canterbury.seng302.gardenersgrove.model.weather;

public interface WeatherAPIResponse {
    Location getLocation();
    void setLocation(Location location);
    Forecast getForecast();
    void setForecast(Forecast forecast);
}
