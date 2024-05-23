package nz.ac.canterbury.seng302.gardenersgrove.service.weather;

import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.repository.weather.GardenWeatherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GardenWeatherService {
    private final GardenWeatherRepository gardenWeatherRepository;

    public GardenWeatherService (GardenWeatherRepository gardenWeatherRepository) {
        this.gardenWeatherRepository = gardenWeatherRepository;
    }

    /**
     * Gets all the currently stored garden weather data from the database.
     * @return a list of all the weather data.
     */
    public List<GardenWeather> getAllWeatherData() {
        return gardenWeatherRepository.findAll();
    }

    /**
     * Gets the garden weather for a specific id from the database.
     * @param id the id of the garden weather
     * @return an optional that contains the garden weather at that ID.
     */
    public Optional<GardenWeather> getWeatherDataById(long id) {
        return gardenWeatherRepository.findById(id);
    }

    /**
     * Saves garden weather to the database
     * @param gardenWeather the data to save
     * @return the GardenWeather data that is saved.
     */
    public GardenWeather addWeather(GardenWeather gardenWeather) {
        return gardenWeatherRepository.save(gardenWeather);
    }
}
