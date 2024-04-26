package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@DataJpaTest
@Import(GardenUserService.class)
public class GardenServiceIntegrationTests {

    @Autowired
    private GardenUserRepository gardenUserRepository;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private PlantRepository plantRepository;

    private GardenUserService gardenUserService;

    private GardenService gardenService;

    private PlantService plantService;

    @BeforeEach
    public void setUp() {
        gardenUserService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        plantService = new PlantService(plantRepository, gardenRepository);
    }

    @Test
    public void testWhenISearchGardenName_AndThereIsAGardenWithGardenName_ReturnListWithGarden() {
        GardenUser gardenUser = new GardenUser("John", "Doe", "john.doe@gmail.com", "password", "01/01/2000");
        gardenUserService.addUser(gardenUser);

        Garden garden = new Garden("Garden Name", "Garden Location", "100", "Garden Description");
        garden.setOwner(gardenUser);

        Plant plant = new Plant();

        gardenService.addGarden(garden);
//        plantService.addPlant(plant, 0L);

        String search = "Garden Name";

        List<Garden> gardens = gardenService.findAllThatContainQuery(search);
        for (Garden iterateGarden : gardens) {
            System.out.println(iterateGarden);
        }

        Assertions.assertTrue(gardens.contains(garden));
    }
}
