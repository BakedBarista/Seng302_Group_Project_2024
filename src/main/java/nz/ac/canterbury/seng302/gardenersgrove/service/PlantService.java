package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * PlantService implementation of the plant repository
 */
@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private GardenRepository gardenRepository;


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
     * Add's a plant to the database.
     * @param plant the plant data to save in the database.
     * @return the saved plant object.
     */
    public Plant addPlant(Plant plant, Long gardenId) {
        Garden garden = gardenRepository.findById(gardenId).orElseThrow(() -> new RuntimeException("Garden not found"));
        System.out.println("Plant name before save: " + plant.getName());
        plant.setGarden(garden);
        return plantRepository.save(plant);
    }



    /**
     * Get's one plant by it's unique ID.
     * @param id the unique ID of the plant in the database.
     * @return the plant object
     */
    public Optional<Plant> getPlantById(long id) {return plantRepository.findById(id);}
}
