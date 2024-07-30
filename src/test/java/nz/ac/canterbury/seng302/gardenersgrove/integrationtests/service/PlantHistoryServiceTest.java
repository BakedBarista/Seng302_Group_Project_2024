package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHistoryItemDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantHistoryRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantHistoryService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ GardenUserService.class, GardenService.class, PlantService.class, PlantHistoryService.class })
class PlantHistoryServiceTest {

    @Autowired
    private GardenUserService gardenUserService;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private PlantService plantService;

    @Autowired
    private PlantHistoryService historyService;

    @Mock
    private PlantHistoryRepository plantHistoryRepository;

    @MockBean
    private Clock clock;

    private GardenUser user;
    private Garden garden;
    private Plant plant;
    private Instant timestamp;

    @BeforeEach
    void setUp() {
        user = new GardenUser("John", "Doe", "jdo123@uclive.ac.nz", "passwd", null);
        gardenUserService.addUser(user);

        garden = new Garden("Garden Name", "1", "Ilam Rd", "Ilam", "Christchurch", "NZ", null, null, null,
                "Garden Description", null);
        garden.setOwner(user);
        gardenService.addGarden(garden);

        PlantDTO plantDTO = new PlantDTO("Rose", "1", "", null);
        plant = plantService.createPlant(plantDTO, garden.getId());

        timestamp = Instant.ofEpochSecond(0);
    }

    @Test
    void whenAddHistoryItem_thenHistoryItemCreated() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(timestamp);
        String contentType = "image/png";
        byte[] image = new byte[] { 1, 2, 3, 4, 5 };

        historyService.addHistoryItem(plant, contentType, image, "Description");

        assertEquals(1, plant.getHistory().size());
        PlantHistoryItem historyItem = plant.getHistory().iterator().next();
        assertEquals(plant, historyItem.getPlant());
        assertEquals(timestamp.atZone(clock.getZone()).toLocalDate(), historyItem.getTimestamp());
        assertEquals(contentType, historyItem.getImageContentType());
        assertEquals(image, historyItem.getImage());
        assertEquals("Description", historyItem.getDescription());
    }

    @Test
    void whenGetHistoryItem_thenItemsRetrieved() {
        PlantHistoryItem plantHistoryItem = new PlantHistoryItem(plant, LocalDate.of(1970, 1, 1));
        System.out.println(plantHistoryItem);

        List<PlantHistoryItem> historyItems = Collections.singletonList(plantHistoryItem);
        System.out.println(historyItems);

        when(plantHistoryRepository.findByPlantId(plant.getId())).thenReturn(historyItems);

        List<PlantHistoryItemDTO> result = historyService.getPlantHistory(plant);
        System.out.println(result);

        assertEquals(1, result.size());
        PlantHistoryItemDTO resultItem = result.get(0);
        assertEquals(timestamp, resultItem.getTimestamp());


    }

}
