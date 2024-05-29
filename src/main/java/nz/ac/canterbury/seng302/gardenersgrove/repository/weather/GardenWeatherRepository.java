package nz.ac.canterbury.seng302.gardenersgrove.repository.weather;

import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface GardenWeatherRepository extends CrudRepository<GardenWeather, Long> {
    Optional<GardenWeather> findById(long id);
    List<GardenWeather> findAll();
}
