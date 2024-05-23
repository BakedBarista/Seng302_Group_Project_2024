package nz.ac.canterbury.seng302.gardenersgrove.entity.weather;

public interface WeatherData {
    void setCity(String city);
    String getCity();

    void setDate(String date);
    String getDate();

    void setMaxTemp(double maxTemp);
    double getMaxTemp();

    void setMinTemp(double minTemp);
    double getMinTemp();

    void setHumidity(int humidity);
    int getHumidity();

    void setConditions(String conditions);
    String getConditions();

    void setIcon(String icon);
    String getIcon();

    void setWindSpeed(float windSpeed);
    float getWindSpeed();

    void setPrecipitation(double precipitation);
    double getPrecipitation();

    void setUv(int uv);
    int getUv();
}
