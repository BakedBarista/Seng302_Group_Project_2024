package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenHistoryItemDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Service for retrieving the history of garden plants
 */
@Service
public class GardenHistoryService {

    /**
     * Get the history of plants in a garden
     * @param garden Garden to retrieve the plant history of
     * @return History of plants in a garden
     */
    public SortedMap<LocalDate, List<GardenHistoryItemDTO>> getGardenHistory(Garden garden) {
        List<Plant> plants = garden.getPlants();
        if (plants == null) {
            return new TreeMap<>();
        }

        List<GardenHistoryItemDTO> items = new ArrayList<>();
        for (Plant plant : plants) {
            if (plant.getPlantedDate() != null) {
                items.add(new GardenHistoryItemDTO(plant, plant.getPlantedDate(), GardenHistoryItemDTO.Action.PLANTED));
            }
            //TODO implement harvest action
        }

        SortedMap<LocalDate, List<GardenHistoryItemDTO>> history = new TreeMap<>(Comparator.reverseOrder());
        for (GardenHistoryItemDTO item : items) {
            LocalDate date = item.getDate();
            history.computeIfAbsent(date, () -> new ArrayList<>())
            history.get(date).add(item);
        }
        return history;
    }
}
