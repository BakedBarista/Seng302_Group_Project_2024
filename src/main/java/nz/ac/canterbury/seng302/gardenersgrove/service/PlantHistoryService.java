package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service to access plant history items
*/
@Service
public class PlantHistoryService {
    private PlantHistoryRepository plantHistoryRepository;
    private Clock clock;

    public PlantHistoryService(PlantHistoryRepository plantHistoryRepository, Clock clock) {
        this.plantHistoryRepository = plantHistoryRepository;
        this.clock = clock;
    }

    /**
     * Add a history item to a plant with the given image and description, and the current time
     *
     * @param plant the plant to add the history item to
     * @param contentType the content type of the image
     * @param image the image data
     * @param description the description of the history item
     */
    public void addHistoryItem(Plant plant, String contentType, byte[] image, String description) {
        LocalDate timestamp = clock.instant().atZone(clock.getZone()).toLocalDate();
        PlantHistoryItem historyItem = new PlantHistoryItem(plant, timestamp);
        historyItem.setImage(contentType, image);
        historyItem.setDescription(description);

        plant.getHistory().add(historyItem);
        plantHistoryRepository.save(historyItem);
    }

    /**
     * Retrieves a plant history item
     *
     * @param plant the plant the history item belongs to
     * @return the history item
     */
    public List<PlantHistoryItem> getPlantHistory(Plant plant) {
        return plantHistoryRepository.findByPlantId(plant.getId());
    }

    /**
     * Checks if a plant history item exists on a date
     *
     * @param plant Plant the history belongs to
     * @param timestamp The date where a history update is added
     * @return True if plant history exists on that date otherwise false
     */
    public boolean historyExists(Plant plant, LocalDate timestamp) {
        List<PlantHistoryItem> plantHistoryItems = plantHistoryRepository.findByPlantIdAndTimestamp(plant.getId(), timestamp);
        return !plantHistoryItems.isEmpty();
    }

    /**
     * Gets plant history item by id
     *
     * @param id the id of the plant history
     * @return Instance of plant history item
     */
    public Optional<PlantHistoryItem> getPlantHistoryById(Long id) {
        return plantHistoryRepository.findById(id);
    }
}
