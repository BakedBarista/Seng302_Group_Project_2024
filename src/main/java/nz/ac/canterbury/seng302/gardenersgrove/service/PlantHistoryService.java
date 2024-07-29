package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantHistoryRepository;

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
}
