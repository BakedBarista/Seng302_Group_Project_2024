package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for GardenFormResults
 */
@Service
public class GardenService {
    private final GardenRepository gardenRepository;
    private static final Set<String> ACCEPTED_FILE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/svg");
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;

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
    public Garden addGarden(Garden garden) {
        if (garden.getSize() != null && garden.getSize() <= 0) {
            throw new IllegalArgumentException("Garden size must be greater than 0");
        }
        return gardenRepository.save(garden);
    }

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

    /**
     * Get all public gardens by owner id
     * @param user owner id used to retrieve data
     * @return a list of public gardens owned by the owner
     */
    public List<Garden> getPublicGardensByOwnerId(GardenUser user) {
        return gardenRepository.findUserPublicGarden(user);
    }

    /**
     * Get all private gardens by owner id
     * @param user owner id used to retrieve data
     * @return a list of private gardens owned by the owner
     */

    public List<Garden> getPrivateGardensByOwnerId(GardenUser user) {
        return gardenRepository.findUserPrivateGarden(user);
    }

    /**
     *  Get a page for pagination of user gardens that meet the given query string
     * @param search string to be matched against
     * @param tags list of tags to be matched against
     * @param pageable pagination information
     * @return page to display
     */
    public Page<Garden> findGardensBySearchAndTags(String search, List<String> tags, Pageable pageable) {
        if (tags == null || tags.isEmpty()) {
            return gardenRepository.findPageThatContainsQuery(search, pageable);
        } else {
            return gardenRepository.findGardensBySearchAndTags(search, tags, pageable);
        }
    }

    /**
     * Sets the image of the garden with given ID.
     * @param id ID of the garden which image is to be set
     * @param gardenImage multipart file for the garden image
     */
    public void setGardenImage(long id, MultipartFile gardenImage) {
        if (validateImage(gardenImage)) {
            var garden = gardenRepository.findById(id);
            if (garden.isEmpty()) {
                return;
            }

            try {
                garden.get().setGardenImage(gardenImage.getContentType(), gardenImage.getBytes());
                gardenRepository.save(garden.get());
            } catch (Exception e) {
            }
        }
    }

    /**
     * Validate an image on the server side to be > 10MB
     * and a valid file type (png, svg, jpg, jpeg
     * @param gardenImage image to be validated
     * @return true if it is valid
     */
    public boolean validateImage(MultipartFile gardenImage) {
        return ACCEPTED_FILE_TYPES.contains(gardenImage.getContentType())
                && (gardenImage.getSize() <= MAX_FILE_SIZE);
    }
}

