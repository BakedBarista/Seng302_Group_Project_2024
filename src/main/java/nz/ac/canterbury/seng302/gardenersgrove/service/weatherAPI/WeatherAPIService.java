package nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class WeatherAPIService {
    private static Logger logger = LoggerFactory.getLogger(WeatherAPIService.class);
    private final RestTemplate restTemplate;
    @Value("${WEATHER_API}")
    private String API_KEY;
    private final String WEATHER_API_URL = "https://api.weatherapi.com/v1/forecast.json?key=";
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    public WeatherAPIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the forecasted weather for the next 3 days (including today as one of the days)
     * The included data is the City Name, Max Temp, Avg Humidity, Conditions, Wind Speed (kph), Precipitation (mm),
     * and UV
     * @param lat latitude value of location
     * @param lng longitude value of location
     * @return a list of the weather forecast values for each day in a map.
     */
    // TODO: Implement caching of the weather response
    public List<HashMap<String, Object>> getForecastWeather(double lat, double lng) {
        ArrayList<HashMap<String, Object>> forecastWeather = new ArrayList<>();
        String locationQuery = "&q=" + lat + "," + lng + "&days=3";
        String url = WEATHER_API_URL + API_KEY + locationQuery;

        logger.info("Requesting the future forecast for Lat: {} Lng: {}", lat, lng);

        try {
            logger.debug("Requesting weather for API on URL: {}", url);
            ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
            HttpStatusCode statusCode = result.getStatusCode();
            logger.info("API responded with status code: {}", statusCode);

            if (statusCode == HttpStatus.OK && result.getBody() != null) {
                logger.info("Weather data was successfully fetched.");
                logger.debug("API Result: {}", result.getBody());
                logger.info("Parsing JSON weather result...");

                try {
                    String jsonResponse = result.getBody();
                    WeatherAPIResponse weatherAPIResponse = null;
                    weatherAPIResponse = objectMapper.readValue(jsonResponse, WeatherAPIResponse.class);
                    logger.info("{}", weatherAPIResponse);

                    for (WeatherAPIResponse.Forecast.ForecastDay forecastDay: weatherAPIResponse.getForecast().getForecastDays()) {
                        HashMap<String, Object> weatherValues = new HashMap<>();

                        weatherValues.put("city", weatherAPIResponse.getLocation().getLocationName());
                        weatherValues.put("maxTemp", forecastDay.getDay().getMaxTemp());
                        weatherValues.put("avgHumidity", forecastDay.getDay().getHumidity());
                        weatherValues.put("conditions", forecastDay.getDay().getCondition().getConditions());
                        weatherValues.put("iconUrl", forecastDay.getDay().getCondition().getIconUrl());
                        weatherValues.put("windSpeed", forecastDay.getDay().getWindSpeed());
                        weatherValues.put("precipitation", forecastDay.getDay().getPrecipitation());
                        weatherValues.put("uv", forecastDay.getDay().getUv());

                        // Transform date into format we want e.g.: 2024-04-29 to Monday 29 Apr
                        LocalDate date = LocalDate.parse(forecastDay.getDate());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMM");
                        weatherValues.put("date", date.format(formatter));
                        logger.debug("Weather values for {}: {}", date, weatherValues);
                        forecastWeather.add(weatherValues);
                    }
                } catch (JsonProcessingException e) {
                    logger.error("Error parsing API response", e);
                } catch (NullPointerException e) {
                    logger.error("Something went wrong accessing one of the results", e);
                }
            } else {
                logger.error("Weather data was not returned successfully");
            }
            logger.debug("Weather JSON parsed as: {}", forecastWeather);
        } catch (HttpClientErrorException e) {
            logger.error("Something went wrong accessing the weather API data. Check the API key", e);
        }
        logger.info("Weather data returned as: {}", forecastWeather);
        return forecastWeather;
    }
}