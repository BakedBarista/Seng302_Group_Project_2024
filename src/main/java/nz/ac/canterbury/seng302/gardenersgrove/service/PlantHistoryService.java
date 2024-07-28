package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHistoryItemDTO;
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

    public void addHistoryItem(Plant plant, String contentType, byte[] image, String description) {
        LocalDate timestamp = clock.instant().atZone(clock.getZone()).toLocalDate();
        PlantHistoryItem historyItem = new PlantHistoryItem(plant, timestamp);
        historyItem.setImage(contentType, image);
        historyItem.setDescription(description);

        plant.getHistory().add(historyItem);
        plantHistoryRepository.save(historyItem);
    }

    public List<PlantHistoryItemDTO> getPlantHistory(Plant plant) {

        List<PlantHistoryItem> historyItems = plantHistoryRepository.findByPlantId(plant.getId());
        return historyItems.stream()
                .map(item -> new PlantHistoryItemDTO(item.getDescription(), item.getTimestamp()))
                .collect(Collectors.toList());
    }


}
