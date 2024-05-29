package nz.ac.canterbury.seng302.gardenersgrove.service.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.WeatherData;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.*;
import nz.ac.canterbury.seng302.gardenersgrove.model.weather.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Supplier;

@Service
public class WeatherAPIService {
    @Value("${WEATHER_KEY:}")
    private String API_KEY;

    private static final String API_NO_RESPONSE = "No Response";
    private static final Logger logger = LoggerFactory.getLogger(WeatherAPIService.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // The rainy conditions possible with WeatherAPI.com
    private static final Set<String> RAIN_CONDITIONS = Set.of(
            "rain", "drizzle", "heavy rain", "light rain", "showers",
            "thunderstorms", "sleet", "snow", "light snow", "heavy snow", "snow showers", "patchy rain nearby"
    );
    private final RestTemplate restTemplate;
    private final GardenService gardenService;
    private final GardenWeatherService gardenWeatherService;

    /**
     * Constructor for the garden weather API service
     * @param restTemplate the resttemplate object
     * @param gardenService the garden service object
     * @param gardenWeatherService the garden weather service object
     */
    @Autowired
    public WeatherAPIService(RestTemplate restTemplate, GardenService gardenService, GardenWeatherService gardenWeatherService) {
        this.restTemplate = restTemplate;
        this.gardenService = gardenService;
        this.gardenWeatherService = gardenWeatherService;
    }

    /**
     * Gets the DB stored weather data for a garden. This weather data being the previous weather and the forecast weather, NOT
     * the current weather.
     * @param gardenId the garden to fetch the weather for
     * @param lat the latitude of the garden
     * @param lng the longitude of the garden
     * @return the weather data for that garden
     */
    @Transactional
    public GardenWeather getWeatherData(long gardenId,  double lat, double lng) {

        Optional<Garden> optGarden = gardenService.getGardenById(gardenId);
        if (optGarden.isEmpty()) {
            logger.error("No garden can be found at that ID");
            return null;
        }
        boolean fetchFromApi = false;
        Garden garden = optGarden.get();

        GardenWeather gardenWeather = garden.getGardenWeather();

        if (gardenWeather == null) {
            logger.info("No weather saved for garden, fetching from API.");
            fetchFromApi = true;
        } else if (!LocalDate.parse(gardenWeather.getLastUpdated(), DateTimeFormatter.ISO_DATE).isEqual(LocalDate.now())) {
            logger.info("The weather saved for garden has expired, fetching again from API.");
            fetchFromApi = true;
        }

        if (!fetchFromApi) {
            logger.info("Serving weather data from the database");
            return gardenWeather;
        } else {
            // Get past 2 days weather
            ArrayList<String> dates = new ArrayList<>();
            LocalDate currentDate = LocalDate.now();
            dates.add(currentDate.minusDays(2).toString());
            dates.add(currentDate.minusDays(1).toString());
            List<WeatherAPIResponse> historyResponses = getPreviousWeatherFromAPI(lat, lng, dates);

            // Get forecast weather
            WeatherAPIResponse forecastResponse = getForecastWeatherFromAPI(lat, lng);

            if (forecastResponse.getForecast() == null || historyResponses.isEmpty()) {
                logger.error("No data was returned from the weather API");
                return null;
            }

            return saveWeather(lat, lng, garden, forecastResponse, historyResponses);
        }
    }

    /**
     * Gives a recommendation on whether to water plants, based on if it is either currently raining, or if it
     * has rained the previous 2 consecutive days.
     * @param gardenWeather the weather data for a garden
     * @param currentResponse the current weather data for a garden.
     * @return a true or false on if the plants should be watered.
     */

    public boolean getWateringRecommendation(GardenWeather gardenWeather, WeatherAPICurrentResponse currentResponse) {
        logger.info("Generating watering recommendation");

        // If the current weather is rain, don't water the plants
        logger.info("Current condition is: {}", currentResponse.getCurrent().getCondition().getConditions().toLowerCase());
        if (RAIN_CONDITIONS.contains(currentResponse.getCurrent().getCondition().getConditions().toLowerCase())) {
            logger.info("Currently raining, don't water plants");
            return false;
        }

        // If it has rained the last 2 days don't water the plants
        int rainyDayCount = 0;
        List<WeatherData> historyResponses = gardenWeather.getPreviousWeather();

        for (WeatherData weather: historyResponses) {
            if (RAIN_CONDITIONS.contains(weather.getConditions().toLowerCase())) {
                rainyDayCount++;
            }
        }
        logger.info("It has rained the past {} days, only water if 0", rainyDayCount);
        return rainyDayCount == 0;
    }

    /**
     * Gets the current weather conditions for a specified location from the weather API.
     * @param lat the latitude of the location
     * @param lng the longitude of the location
     * @return a {@link WeatherAPICurrentResponse} representing the parsed JSON values from the API.
     */
    @Cacheable(value = "weatherCache", key = "#lat + ',' + #lng")
    public WeatherAPICurrentResponse getCurrentWeatherFromAPI(double lat, double lng) {
        WeatherAPICurrentResponse currentWeather = new WeatherAPICurrentResponse();
        String locationQuery = "&q=" + lat + "," + lng;
        String apiCurrentUrl = "https://api.weatherapi.com/v1/current.json?key=";
        String url = apiCurrentUrl + API_KEY + locationQuery;

        try {
            logger.info("Requesting the current weather conditions for Lat: {} Lng: {}", lat, lng);
            String response = requestAPI(url);

            if (Objects.equals(response, API_NO_RESPONSE)) {
                logger.error("No value was returned by the weather API, returning empty response object.");
                return currentWeather;
            }
            return objectMapper.readValue(response, WeatherAPICurrentResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON: ", e);
        }
        return currentWeather;
    }

    /**
     * Gets the previous weather forecast from the past requested days.
     * @param lat the latitude of the location
     * @param lng the longitude of the location
     * @param dates the previous dates requested as a list of dates in format YYYY-MM-DD e.g. "2024-05-23"
     * @return A list of {@link WeatherAPIResponse} containing the conditions from the previous requested dates.
     */
    private List<WeatherAPIResponse> getPreviousWeatherFromAPI(double lat, double lng, List<String> dates) {
        ArrayList<WeatherAPIResponse> previousWeather = new ArrayList<>();
        logger.info("Requesting previous weather data for dates: {}", dates);

        for (String date: dates) {
            try {
                String locationQuery = "&q=" + lat + "," + lng + "&dt=" + date;
                String apiHistoryUrl = "https://api.weatherapi.com/v1/history.json?key=";
                String url = apiHistoryUrl + API_KEY + locationQuery;

                logger.info("Requesting the previous forecast for Lat: {} Lng: {} on Day: {}", lat, lng, date);
                String response = requestAPI(url);

                if (Objects.equals(response, API_NO_RESPONSE)) {
                    logger.error("No value was returned by the weather API, returning empty response object.");
                } else {
                    previousWeather.add(objectMapper.readValue(response, WeatherAPIResponse.class));
                }
            } catch (JsonProcessingException e) {
                logger.error("Error processing JSON: ", e);
            }
        }
        return previousWeather;
    }

    /**
     * Gets the forecast weather conditions for a specified location from the weather API. The forecast includes, later
     * today, the next day , and the day after.
     * @param lat the latitude of the location
     * @param lng the longitude of the location
     * @return a {@link WeatherAPIResponse} representing the parsed JSON values from the API.
     */
    private WeatherAPIResponse getForecastWeatherFromAPI(double lat, double lng) {
        WeatherAPIResponse forecastWeather = new WeatherAPIResponse();
        String locationQuery = "&q=" + lat + "," + lng + "&days=3";
        String apiForecastUrl = "https://api.weatherapi.com/v1/forecast.json?key=";
        String url = apiForecastUrl + API_KEY + locationQuery;

        try {
            logger.info("Requesting the forecast weather conditions for Lat: {} Lng: {}", lat, lng);
            String response = requestAPI(url);

            if (Objects.equals(response, API_NO_RESPONSE)) {
                logger.error("No value was returned by the weather API, returning empty response object.");
                return forecastWeather;
            }
            return objectMapper.readValue(response, WeatherAPIResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON: ", e);
        }
        return forecastWeather;
    }

    /**
     * Requests the actual weather API with the given parameters.
     * @param url the url to request.
     * @return a String containing the request body, which will be a JSON representation of the requested weather data.
     * If there is an error then "No Response" will be returned.
     */
    public String requestAPI(String url) {
        try {
            logger.info("Requesting the weather API");
            ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);

            logger.debug("API response body: {}", result.getBody());
            return result.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 400) {
                logger.error("Bad request sent to weather API, something is wrong with the lat and lng provided.");
            } else if (e.getStatusCode().value() == 403) {
                logger.error("Authentication issue with weather API, check API key.");
            } else {
                logger.error("An unknown error occurred with the weather API.", e);
            }
        }
        return API_NO_RESPONSE;
    }

    /**
     * Takes the weather data from the API and saves it to the database.
     * @param lat the latitude of the weather data
     * @param lng the longitude of the weather data
     * @param garden the Garden this weather is associated with
     * @param forecastResponse the forecast weather API response
     * @param previousResponse the previous weather API response
     */
    public GardenWeather saveWeather(double lat, double lng, Garden garden, WeatherAPIResponse forecastResponse, List<WeatherAPIResponse> previousResponse) {
        GardenWeather gardenWeather = new GardenWeather();

        gardenWeather.setLat(lat);
        gardenWeather.setLng(lng);
        gardenWeather.setLastUpdated(LocalDate.now().toString());

        logger.info("Adding the forecasted weather to the weather data.");
        List<WeatherData> forecastedWeather = new ArrayList<>();
        for (ForecastDay forecastDay: forecastResponse.getForecast().getForecastDays()) {
            forecastedWeather.add(extractDailyWeatherData(forecastResponse, WeatherData::new, forecastDay));
        }
        gardenWeather.setForecastWeather(forecastedWeather);

        logger.info("Adding the previous weather to the weather data.");
        List<WeatherData> previousWeather =  new ArrayList<>();
        for (WeatherAPIResponse historyDay: previousResponse) {
            previousWeather.add(extractDailyWeatherData(historyDay, WeatherData::new, historyDay.getForecast().getForecastDays().get(0)));
        }
        gardenWeather.setPreviousWeather(previousWeather);

        logger.info("Saving the weather data to the database.");
        gardenWeather.setGarden(garden);
        return gardenWeatherService.addWeather(gardenWeather);
    }

    /**
     * Goes through the forecast / previously forecast day in the weather data and extracts the data for that day
     * exists in there. Creating a new weather data object for each one. Generic type used to allow both Forecast and
     * Previous days to be used.
     * <p>
     * ChatGPT helped me come up with the Supplier thing to make the generic responses work :D - Luke
     * @param weatherAPIResponse a generic weather API response
     * @param supplier the specific type of the data wanted {@link WeatherData}.
     * @return a list of generic weather data
     */
    private <T extends WeatherData> T extractDailyWeatherData(WeatherAPIResponse weatherAPIResponse, Supplier<T> supplier, ForecastDay forecastDay) {
        T weather = supplier.get();
        Day day = forecastDay.getDay();
        weather.setCity(weatherAPIResponse.getLocation().getLocationName());

        LocalDate date = null;
        try {
            date = LocalDate.parse(forecastDay.getDate());
        } catch (DateTimeParseException ignored) {
        }
        weather.setDate(date);
        weather.setMaxTemp(day.getMaxTemp());
        weather.setMinTemp(day.getMinTemp());
        weather.setHumidity(day.getHumidity());
        weather.setConditions(day.getCondition().getConditions());

        String[] urlParts = forecastDay.getDay().getCondition().getIconUrl().split("/");
        String icon = urlParts[urlParts.length - 1];
        weather.setIcon(icon);

        weather.setWindSpeed(day.getWindSpeed());
        weather.setPrecipitation(day.getPrecipitation());
        weather.setUv(day.getUv());

        return weather;
    }

    /**
     * method for extracting necessary data for displaying current weather on
     * current weather page by creating a new CurrentWeather instance
     *
     * @param weatherAPIResponse API response that needs to be converted to CurrentWeather instance
     * @return CurrentWeather instance that is populated with data from API response
     */
    public CurrentWeather extractCurrentWeatherData(WeatherAPICurrentResponse weatherAPIResponse) {
        CurrentWeather weather = new CurrentWeather();
        weather.setCity(weatherAPIResponse.getLocation().getLocationName());

        Current current = weatherAPIResponse.getCurrent();

        weather.setDate(LocalDate.now());
        weather.setTemp(current.getCurrentTemp());
        weather.setHumidity(current.getHumidity());
        weather.setConditions(current.getCondition().getConditions());
        weather.setWindSpeed(current.getWindSpeed());

        String[] urlParts = current.getCondition().getIconUrl().split("/");
        String icon = urlParts[urlParts.length - 1];
        weather.setIcon(icon);

        return weather;
    }
}
