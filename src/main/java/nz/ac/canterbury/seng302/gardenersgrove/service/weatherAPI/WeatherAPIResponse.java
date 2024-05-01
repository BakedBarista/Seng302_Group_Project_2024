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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public static class Location {
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

    public static class Forecast {
        @JsonDeserialize
        @JsonProperty("forecastday")
        private ArrayList<ForecastDay> forecastDays;

        public ArrayList<ForecastDay> getForecastDays() {
            return forecastDays;
        }

        public void setForecastDays(ArrayList<ForecastDay> forecastDays) {
            this.forecastDays = forecastDays;
        }

        public static class ForecastDay {
            @JsonDeserialize
            @JsonProperty("date")
            private String date;

            @JsonDeserialize
            @JsonProperty("day")
            private Day day;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public Day getDay() {
                return day;
            }

            public void setDay(Day day) {
                this.day = day;
            }

            public static class Day {
                @JsonDeserialize
                @JsonProperty("maxtemp_c")
                private double maxTemp;

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

                public static class Condition {
                    @JsonDeserialize
                    @JsonProperty("text")
                    private String conditions;

                    @JsonDeserialize
                    @JsonProperty("icon")
                    private String iconUrl;

                    public String getConditions() {
                        return conditions;
                    }

                    public void setConditions(String conditions) {
                        this.conditions = conditions;
                    }

                    public String getIconUrl() {
                        return iconUrl;
                    }

                    public void setIconUrl(String iconUrl) {
                        this.iconUrl = iconUrl;
                    }
                }
            }

        }
    }
}
