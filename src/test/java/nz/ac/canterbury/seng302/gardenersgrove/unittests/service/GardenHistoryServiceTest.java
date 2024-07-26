package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenHistoryItemDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenHistoryItemDTO.Action;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenHistoryService;

class GardenHistoryServiceTest {
    private GardenHistoryService gardenHistoryService;

    @BeforeEach
    void setUp() {
        gardenHistoryService = new GardenHistoryService();
    }

    @Test
    void givenGardenPlantsNull_whenGetGardenHistory_thenEmptyList() {
        Garden garden = new Garden();
        garden.setPlants(null);

        List<GardenHistoryItemDTO> gardenHistory = gardenHistoryService.findPlantHistoryByGarden(garden);

        assertTrue(gardenHistory.isEmpty());
    }

    @Test
    void givenEmptyGarden_whenGetGardenHistory_thenEmptyList() {
        Garden garden = new Garden();
        garden.setPlants(List.of());

        List<GardenHistoryItemDTO> gardenHistory = gardenHistoryService.findPlantHistoryByGarden(garden);

        assertTrue(gardenHistory.isEmpty());
    }

    @Test
    void givenGardenWithPlants_whenGetGardenHistory_thenReturnsPlantedItems() {
        Plant plant1 = new Plant();
        plant1.setPlantedDate(LocalDate.of(2024, 7, 2));
        Plant plant2 = new Plant();
        plant2.setPlantedDate(null);
        Plant plant3 = new Plant();
        plant3.setPlantedDate(LocalDate.of(2024, 7, 3));
        Plant plant4 = new Plant();
        plant4.setPlantedDate(LocalDate.of(2024, 7, 1));
        Garden garden = new Garden();
        garden.setPlants(List.of(plant1, plant2, plant3, plant4));

        List<GardenHistoryItemDTO> gardenHistory = gardenHistoryService.findPlantHistoryByGarden(garden);

        assertEquals(3, gardenHistory.size() );
        assertEquals(plant3, gardenHistory.get(0).getPlant());
        assertEquals(Action.PLANTED, gardenHistory.get(0).getAction());
        assertEquals(LocalDate.of(2024, 7, 3), gardenHistory.get(0).getDate());
        assertEquals(plant1, gardenHistory.get(1).getPlant());
        assertEquals(Action.PLANTED, gardenHistory.get(1).getAction());
        assertEquals(LocalDate.of(2024, 7, 2), gardenHistory.get(1).getDate());
        assertEquals(plant4, gardenHistory.get(2).getPlant());
        assertEquals(Action.PLANTED, gardenHistory.get(2).getAction());
        assertEquals(LocalDate.of(2024, 7, 1), gardenHistory.get(2).getDate());
    }
}
