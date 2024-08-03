package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
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

        var history = gardenHistoryService.getGardenHistory(garden);

        assertTrue(history.isEmpty());
    }

    @Test
    void givenEmptyGarden_whenGetGardenHistory_thenEmptyList() {
        Garden garden = new Garden();
        garden.setPlants(List.of());

        var history = gardenHistoryService.getGardenHistory(garden);

        assertTrue(history.isEmpty());
    }

    @Test
    void givenGardenWithPlants_whenGetGardenHistory_thenReturnsPlantedItems() {
        var day1 = LocalDate.of(2024, 7, 1);
        var day2 = LocalDate.of(2024, 7, 2);
        var day3 = LocalDate.of(2024, 7, 3);

        Plant plant1 = new Plant();
        plant1.setPlantedDate(day2);
        Plant plant2 = new Plant();
        plant2.setPlantedDate(null);
        Plant plant3 = new Plant();
        plant3.setPlantedDate(day3);
        Plant plant4 = new Plant();
        plant4.setPlantedDate(day1);
        plant4.setHarvestedDate(day3);
        Plant plant5 = new Plant();
        plant5.setPlantedDate(day1);
        Garden garden = new Garden();
        garden.setPlants(List.of(plant1, plant2, plant3, plant4, plant5));

        var gardenHistory = gardenHistoryService.getGardenHistory(garden);

        assertEquals(3, gardenHistory.size());
        assertEquals(Comparator.reverseOrder(), gardenHistory.comparator());

        var day1History = gardenHistory.get(day1);
        assertEquals(2, day1History.size());
        assertEquals(plant4, day1History.get(0).getPlant());
        assertEquals(Action.PLANTED, day1History.get(0).getAction());
        assertEquals(day1, day1History.get(0).getDate());
        assertEquals(plant5, day1History.get(1).getPlant());
        assertEquals(Action.PLANTED, day1History.get(1).getAction());
        assertEquals(day1, day1History.get(1).getDate());

        var day2History = gardenHistory.get(day2);
        assertEquals(1, day2History.size());
        assertEquals(plant1, day2History.get(0).getPlant());
        assertEquals(Action.PLANTED, day2History.get(0).getAction());
        assertEquals(day2, day2History.get(0).getDate());

        var day3History = gardenHistory.get(day3);
        assertEquals(2, day3History.size());
        assertEquals(plant3, day3History.get(0).getPlant());
        assertEquals(Action.PLANTED, day3History.get(0).getAction());
        assertEquals(day3, day3History.get(0).getDate());
        assertEquals(plant4, day3History.get(1).getPlant());
        assertEquals(Action.HARVESTED, day3History.get(1).getAction());
        assertEquals(day3, day3History.get(1).getDate());
    }
}
