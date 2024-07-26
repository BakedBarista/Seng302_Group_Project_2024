package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenHistoryItemDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    public List<GardenHistoryItemDTO> findPlantHistoryByGarden(Garden garden) {
        List<Plant> plants = garden.getPlants();

        List<GardenHistoryItemDTO> items = new ArrayList<>();
        if (plants == null) {
            return items;
        }

        for (Plant plant : plants) {
            if (plant.getPlantedDate() != null) {
                items.add(new GardenHistoryItemDTO(plant, plant.getPlantedDate(), GardenHistoryItemDTO.Action.PLANTED));
            }
            //TODO implement harvest action
        }
        items.sort(Comparator.reverseOrder());
        return items;
    }
}
