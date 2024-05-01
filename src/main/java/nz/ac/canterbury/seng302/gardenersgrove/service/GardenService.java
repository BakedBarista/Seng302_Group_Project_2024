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
     * Get all gardens whose name or plants' name contain
     * a given query string.
     * Caps sensitivity is dealt with via SQL statement in GardenRepository.
     * @param query string to be matched against
     * @return a list of gardens whose name or plants' name substring the query
     */
    public List<Garden> findAllThatContainQuery(String query) {
        return gardenRepository.findAllThatContainQuery(query);
    }

    /**
     * Get a page for pagination of user gardens that meet the given query string
     * (e.g. garden name or plant in garden name is a substring of the query)
     * @param query string to be matched against
     * @param pageable pagination information
     * @return page to display
     */
    public Page<Garden> findPageThatContainsQuery(String query, Pageable pageable) {
        return gardenRepository.findPageThatContainsQuery(query, pageable);
    }

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

    /**
     * Gets the page for all public gardens
     * @param pageable pagination information
     * @return page
     */
    public Page<Garden> getPageForPublicGardens(Pageable pageable) {
        return gardenRepository.findByIsPublicTrueOrderByIdDesc(pageable);
    }

    /**
     * Gets a list of all public gardens
     * @return list of all public gardens
     */
    public List<Garden> getPublicGardens() {
        return gardenRepository.findByIsPublicTrueOrderByIdDesc();
    }

    /**
     *
     * @param ownerId owner id used to retrieve data
     * @return a list of gardens owned by the owner
     */
    public List<Garden> getGardensByOwnerId(Long ownerId) {
        return gardenRepository.findByOwnerId(ownerId);
    }

//    public List<Garden> getPublicGardensByOwnerId(GardenUser ownerId) {
//        return gardenRepository.findUserPublicGarden(ownerId);
//    }
//
//    public List<Garden> getPrivateGardensByOwnerId(GardenUser ownerId) {
//        return gardenRepository.findUserPrivateGarden(ownerId);
//    }

}

