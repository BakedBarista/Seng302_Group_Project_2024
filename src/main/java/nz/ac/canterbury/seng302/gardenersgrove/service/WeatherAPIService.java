package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class WeatherAPIService {
    private static Logger logger = LoggerFactory.getLogger(WeatherAPIService.class);
    private final RestTemplate restTemplate;
    @Value("${WEATHER_API}")
    private String apiKey;
    private final String apiUrlForecastWeather = "https://api.weatherapi.com/v1/forecast.json?key=";

    @Autowired
    public WeatherAPIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the forecasted weather for the next 3 days (including today as one of the days)
     * @param lat latitude value of location
     * @param lng longitude value of location
     * @return a list of the weather forecast values for each day in an Map form:
     *      {
     *          'city': string,
     *          'minTemp': double,
     *          'maxTemp': double,
     *          'humidity': string,
     *          'conditions': string,
     *      }
     */
    // TODO: Implement caching of the weather response
    public List<HashMap<String, Object>> getForecastWeather(double lat, double lng) {
        ArrayList<HashMap<String, Object>> forecastWeather = new ArrayList<>();
        String locationQuery = "&q=" + lat + "," + lng + "&days=3";
        String url = apiUrlForecastWeather + apiKey + locationQuery;
        logger.info("Requesting the future forecast for Lat: {} Lng: {}", lat, lng);

        try {
            logger.debug("Requesting weather for API on URL: {}", url);
            ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
            HttpStatusCode statusCode = result.getStatusCode();

            if (statusCode == HttpStatus.OK && result.getBody() != null) {
                logger.info("Weather data was successfully fetched.");
                logger.debug("API Result: {}", result.getBody());
                logger.debug("Parsing JSON weather result...");

                // Process the JSON response using GSON
                JsonObject jsonResponse = JsonParser.parseString(result.getBody()).getAsJsonObject();
                JsonObject location = jsonResponse.getAsJsonObject("location");
                JsonArray forecastDays = jsonResponse.getAsJsonObject("forecast").getAsJsonArray("forecastday");

                for (JsonElement day: forecastDays) {
                    HashMap<String, Object> weatherValues = new HashMap<>();
                    JsonObject daysWeather = day.getAsJsonObject().getAsJsonObject("day");

                    weatherValues.put("city", location.get("name").getAsString());
                    weatherValues.put("maxTemp", daysWeather.get("maxtemp_c").getAsDouble());
                    weatherValues.put("minTemp", daysWeather.get("mintemp_c").getAsDouble());
                    weatherValues.put("avgHumidity", daysWeather.get("avghumidity").getAsInt());
                    weatherValues.put("conditions", daysWeather.getAsJsonObject("condition").get("text").getAsString());
                    forecastWeather.add(weatherValues);
                }
            } else {
                logger.error("Weather data was not returned successfully");
            }
            logger.debug("Weather JSON parsed as: {}", forecastWeather);
        } catch (HttpClientErrorException e) {
            logger.error("Something went wrong accessing the weather API data. Check the API key.");
        }
        return forecastWeather;
    }
}