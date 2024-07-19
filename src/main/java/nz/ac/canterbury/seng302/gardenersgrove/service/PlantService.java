package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * PlantService implementation of the plant repository
 */
@Service
public class PlantService {
    Logger logger = LoggerFactory.getLogger(PlantService.class);

    private final static Set<String> ACCEPTED_FILE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/svg");
    private final static int MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final PlantRepository plantRepository;
    private final GardenRepository gardenRepository;

    /**
     * Constructor of PlantService, takes an instance of plantRepository
     * @param plantRepository an instance of PlantRepository
     */
    public PlantService(PlantRepository plantRepository, GardenRepository gardenRepository) {
        this.gardenRepository= gardenRepository;
        this.plantRepository = plantRepository;}

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
     * Add's a plant to the database.
     * @param plant the plant data to save in the database.
     * @return the saved plant object.
     */
    public Plant addPlant(Plant plant, Long gardenId) {
        Garden garden = gardenRepository.findById(gardenId).orElseThrow(() -> new RuntimeException("Garden not found"));
        plant.setGarden(garden);
        return plantRepository.save(plant);
    }

    /**
     * Get's one plant by it's unique ID.
     * @param id the unique ID of the plant in the database.
     * @return the plant object
     */

    public Optional<Plant> getPlantById(long id) {return plantRepository.findById(id);}

    /**
     * Sets the image of the plant with given ID.
     * @param id ID of the plant which image is to be set
     * @param plantImage multipart file for the plant image
     */
    public void setPlantImage(long id, MultipartFile plantImage) {
        if (validateImage(plantImage)) {
            var plant = plantRepository.findById(id);
            if (plant.isEmpty()) {
                return;
            }

            try {
                plant.get().setPlantImage(plantImage.getContentType(), plantImage.getBytes());
            } catch (IOException e) {
                logger.error("Exception ", e);
            }
            plantRepository.save(plant.get());
        } else {
            logger.error("Plant image is too large or not of correct type");
        }
    }

    /**
     * Validate an image on the server side to be > 10MB
     * and a valid file type (png, svg, jpg, jpeg
     * @param plantImage image to be validated
     * @return true if it is valid
     */
    public boolean validateImage(MultipartFile plantImage) {
        return ACCEPTED_FILE_TYPES.contains(plantImage.getContentType())
                && (plantImage.getSize() <= MAX_FILE_SIZE);
    }
}
