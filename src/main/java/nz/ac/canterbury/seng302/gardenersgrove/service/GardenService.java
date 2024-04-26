package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for GardenFormResults
 */
@Service
public class GardenService {
    private final GardenRepository gardenRepository;

    public GardenService(GardenRepository gardenRepository) {this.gardenRepository = gardenRepository;}

    /**
     * Gets all the gardens currently in the database.
     * @return a List of the existing gardens.
     */
    public List<Garden> getAllGardens() { return gardenRepository.findAll();}

    /**
     * Adds a garden to the database.
     * @param garden object to save.
     * @return the saved garden object.
     */
    public Garden addGarden(Garden garden) { return gardenRepository.save(garden);}

    /**
     * Get garden details by id
     * @param id garden id used to retrieve data
     * @return the object of given id
     */
    public Optional<Garden> getGardenById(long id) {return gardenRepository.findById(id);}


    public Page<Garden> getPublicGardens(Pageable pageable) {
        return gardenRepository.findByIsPublicTrue(pageable);
    }


    public List<Garden> getGardensByOwnerId(Long ownerId) {
        return gardenRepository.findByOwnerId(ownerId);
    }

}

