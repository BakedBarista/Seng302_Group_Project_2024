package nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherAPIService {
    @Value("${WEATHER_API}")
    private String API_KEY;

    private final String WEATHER_API_URL = "https://api.weatherapi.com/v1/forecast.json?key=";
    private static Logger logger = LoggerFactory.getLogger(WeatherAPIService.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final RestTemplate restTemplate;
    private final GardenService gardenService;

    @Autowired
    public WeatherAPIService(RestTemplate restTemplate, GardenService gardenService) {
        this.restTemplate = restTemplate;
        this.gardenService = gardenService;
    }

    /**
     * Gets a weather forecast for a garden by id.
     * There is a check of the database first to see if there is a valid forecast saved, if not it retrieves a new one
     * from the API and then updates the database.
     * @param gardenId the ID of the garden
     * @param lat the latitude of the gardens location
     * @param lng the longitude of the gardens location
     * @return a List of Maps of the weather forecast for the next 3 days
     */
    public List<Map<String, Object>> getForecastWeather(long gardenId, double lat, double lng) {
        logger.info("Fetching garden at ID: {} for Weather Forecast purposes.", gardenId);
        Optional<Garden> garden = gardenService.getGardenById(gardenId);

        if (garden.isEmpty()) {
            logger.error("Garden could not be found at ID: {}", gardenId);
            return Collections.emptyList();
        }

        // Retrieve forecast information
        List<Map<String, Object>> weatherForecast = garden.get().getWeatherForecast();
        String timezoneId = garden.get().getTimezoneId();
        boolean fetchFromApi = false;

        // If there is an existing forecast
        if (!weatherForecast.isEmpty()) {
            logger.info("Forecast found in the database, checking if it is current...");
            LocalDate currentDate = ZonedDateTime.now(ZoneId.of(timezoneId)).toLocalDate();
            LocalDate lastUpdatedDate = LocalDate.parse(garden.get().getForecastLastUpdated() + " " + currentDate.getYear(),
                   DateTimeFormatter.ofPattern("EEEE dd MMM yyyy"));
            logger.info("Saved Forecast: {}", weatherForecast);
            logger.info("Timezone: {}", timezoneId);
            logger.info("Last Updated: {} Current Date: {}", lastUpdatedDate, currentDate);

            // If the forecasts last update wasn't today
            if (!lastUpdatedDate.equals(currentDate)) {
                fetchFromApi = true;
                logger.info("Forecast is not current, need to retrieve a new forecast from API");
            } else {
                logger.info("Forecast is current, serving data from database.");
            }
        } else {
            fetchFromApi = true;
        }

        // New weather data is required from API
        if (fetchFromApi) {
            logger.info("Fetching a new weather forecast from API...");
            weatherForecast = getForecastWeatherFromAPI(lat, lng);

            if (weatherForecast.isEmpty()) {
                logger.error("No weather forecast returned from API.");
                return Collections.emptyList();
            }
            String newTimezoneId = (String) weatherForecast.get(0).get("timezoneId");
            String newLastUpdateDate = (String) weatherForecast.get(0).get("date");

            // Update the forecast in the database
            logger.info("Updating the forecast saved on garden ID: {}", gardenId);
            garden.get().setWeatherForecast(weatherForecast);
            garden.get().setTimezoneId(newTimezoneId);
            garden.get().setForecastLastUpdated(newLastUpdateDate);
            gardenService.addGarden(garden.get());
        }
        return weatherForecast;
    }

    /**
     * Fetches the forecasted weather for the next 3 days from WeatherAPI.com (including today as one of the days)
     * The included data is the City Name, Max Temp, Avg Humidity, Conditions, Wind Speed (kph), Precipitation (mm),
     * and UV
     * @param lat latitude value of location
     * @param lng longitude value of location
     * @return a list of the weather forecast values for each day in a map.
     */
    private List<Map<String, Object>> getForecastWeatherFromAPI(double lat, double lng) {
        ArrayList<Map<String, Object>> forecastWeather = new ArrayList<>();
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
                    logger.info("{}", weatherAPIResponse.toString());

                    for (WeatherAPIResponse.Forecast.ForecastDay forecastDay: weatherAPIResponse.getForecast().getForecastDays()) {
                        Map<String, Object> weatherValues = new HashMap<>();

                        weatherValues.put("city", weatherAPIResponse.getLocation().getLocationName());
                        weatherValues.put("timezoneId", weatherAPIResponse.getLocation().getTimezoneId());
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
                    return forecastWeather;
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
            logger.error("Something went wrong accessing the weather API data. Check the location & API key");
        }
        logger.info("Weather data returned as: {}", forecastWeather);
        return Collections.emptyList();
    }
}