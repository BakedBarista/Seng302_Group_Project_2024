package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherAPIService {
    private static Logger logger = LoggerFactory.getLogger(WeatherAPIService.class);
    private final RestTemplate restTemplate;
//    @Value("$WEATHER_API")
    private final String apiKey = "c5491038c30146868ae23158242203";
    private final String apiUrlCurrentWeather = "https://api.weatherapi.com/v1/current.json?key=" + apiKey;
    private final String apiUrlForecastWeather = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey;
    private final Gson gson = new Gson();

    @Autowired
    public WeatherAPIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // TODO: Rate limiting

    /**
     * Fetches the current weather forecast for a location from the weatherapi.com API.
     * @param lat latitude value of location
     * @param lng longitude value of location
     * @return the current weather forecast in a JSON format
     */
    public Map<String, String> getCurrentWeather(double lat, double lng) {
        HashMap<String, String> currentWeather = new HashMap<>();
        String locationQuery = "&q=" + lat + "," + lng;
        String url = apiUrlCurrentWeather + locationQuery;
        logger.info(String.format("Requesting the current forecast for Lat: %f Lng: %f", lat, lng));

        // Get API response for location
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        HttpStatusCode statusCode = result.getStatusCode();

        if (statusCode == HttpStatus.OK && result.getBody() != null) {
            logger.info("Weather data was successfully fetched.");
            logger.debug("API Result: {}", result.getBody());
            logger.debug("Parsing JSON weather result...");
            JsonElement jsonResponse = JsonParser.parseString(result.getBody());
            JsonObject jsonObject = jsonResponse.getAsJsonObject();

            JsonObject location = jsonObject.getAsJsonObject("location");
            JsonObject current = jsonObject.getAsJsonObject("current");

            String city = location.get("name").getAsString();
            String conditions = current.getAsJsonObject("condition").get("text").getAsString();
            String temperature = current.get("temp_c").getAsString();
            String humidity = current.get("humidity").getAsString();

            currentWeather.put("city", city);
            currentWeather.put("conditions",conditions);
            currentWeather.put("temperature", temperature);
            currentWeather.put("humidity", humidity);
        } else {
            logger.error("Weather data was not returned successfully");
        }
        logger.debug("Weather JSON parsed as: {}", currentWeather);
        return currentWeather;
    }
}