package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.time.Clock;
import java.time.Instant;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantHistoryRepository;

/**
 * Service to access plant history items
 */
public class PlantHistoryService {
    private PlantHistoryRepository plantHistoryRepository;
    private Clock clock;

    public PlantHistoryService(PlantHistoryRepository plantHistoryRepository, Clock clock) {
        this.plantHistoryRepository = plantHistoryRepository;
        this.clock = clock;
    }

    public void addHistoryItem(Plant plant, String contentType, byte[] image, String description) {
        Instant timestamp = clock.instant();
        PlantHistoryItem historyItem = new PlantHistoryItem(plant, timestamp);
        historyItem.setImage(contentType, image);
        historyItem.setDescription(description);

        plant.getHistory().add(historyItem);
        plantHistoryRepository.save(historyItem);
    }
}
