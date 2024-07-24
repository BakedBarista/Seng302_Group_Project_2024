package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantHistoryRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * PlantService implementation of the plant repository
 */
@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final PlantHistoryRepository plantHistoryRepository;
    private final GardenRepository gardenRepository;
    private final Clock clock;


    /**
     * Constructor of PlantService, takes an instance of plantRepository
     * @param plantRepository an instance of PlantRepository
     */
    public PlantService(PlantRepository plantRepository, PlantHistoryRepository plantHistoryRepository,
            GardenRepository gardenRepository, Clock clock) {
        this.gardenRepository = gardenRepository;
        this.plantHistoryRepository = plantHistoryRepository;
        this.plantRepository = plantRepository;
        this.clock = clock;
    }

    /**
     * Gets all of the plants from the database.
     * @return a List of plants in the database.
     */
    public List<Plant> getAllPlants() { return plantRepository.findAll();}


    /**
     * Get a list of plants for given gardenId
     * @param gardenId the id of the garden
     * @return list of plants for given gardenId
     */
    public List<Plant> getPlantsByGardenId(long gardenId) {
        return plantRepository.findByGardenId(gardenId);
    }


    /**
     * Adds a plant to the database.
     * @param plant the plant data to save in the database.
     * @param gardenId the garden ID to associate the plant with.
     * @return the saved plant object.
     */
    public Plant createPlant(PlantDTO plantDTO, Long gardenId) {
        plantDTO.normalize();
        Plant plant = new Plant(plantDTO);

        Garden garden = gardenRepository.findById(gardenId).orElseThrow(() -> new RuntimeException("Garden not found"));
        plant.setGarden(garden);
        return plantRepository.save(plant);
    }


    /**
     * Updates a plant in the database.
     * @param plant the plant object to update.
     * @param plantDTO the plant data to update the plant with.
     */
    public void updatePlant(Plant plant, PlantDTO plantDTO) {
        plantDTO.normalize();
        plant.setName(plantDTO.getName());
        plant.setCount(plantDTO.getCount());
        plant.setDescription(plantDTO.getDescription());
        plant.setPlantedDate(plantDTO.getParsedPlantedDate());

        plantRepository.save(plant);
    }


    /**
     * Get's one plant by it's unique ID.
     * @param id the unique ID of the plant in the database.
     * @return the plant object
     */
    public Optional<Plant> getPlantById(long id) {
        return plantRepository.findById(id);
    }


    /**
     * Sets the image of the plant with given ID.
     * @param id ID of the plant which image is to be set
     * @param contentType contentType The content type of the plant image
     * @param plantImage The byte array representing the plant image
     */
    public void setPlantImage(long id, String contentType, byte[] plantImage) {
        var plant = plantRepository.findById(id);
        if (plant.isEmpty()) {
            return;
        }
        plant.get().setPlantImage(contentType, plantImage);

        Instant timestamp = clock.instant();
        PlantHistoryItem historyItem = new PlantHistoryItem(plant.get(), timestamp);
        historyItem.setImage(contentType, plantImage);

        plantRepository.save(plant.get());
        plantHistoryRepository.save(historyItem);
    }
}
