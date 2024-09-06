package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class DeleteFavouritePlantControllerTest {

    @Autowired
    private GardenService gardenService;

    @Autowired
    private GardenUserService userService;

    @Autowired
    private PlantService plantService;

    private GardenUser user;

    @BeforeEach
    void setup() {
        if (userService.getUserByEmail("johndoe@someemail.com") == null) {
            user = new GardenUser("john", "doe", "johndoe@someemail.com", "password", LocalDate.now());
            userService.addUser(user);
        } else {
            user = userService.getUserByEmail("johndoe@someemail.com");
        }
        Garden garden = new Garden("name", "1", "streetName",
                "suburb", "city", "country", "1000", 0D, 0D,
                "description", 2D, "gardenImage".getBytes(), "gardenImageContentType");
        garden.setOwner(user);
        gardenService.addGarden(garden);

        Plant plantA = new Plant("testPlantA", "1", "yellow", LocalDate.now());
        plantA.setGarden(garden);
        Plant plantB = new Plant("testPlantB", "2", "green", LocalDate.now());
        plantB.setGarden(garden);
        Plant plantC = new Plant("testPlantC", "1", "big", LocalDate.now());
        plantC.setGarden(garden);
        plantService.save(plantA);
        plantService.save(plantB);
        plantService.save(plantC);

        user.setFavouritePlants(Set.of(plantA, plantB, plantC));
        userService.addUser(user);
    }

    @Test
    void givenIAmAuthorizedAndHavePlantWithIdFavourited_whenIDeletePlantWithId_thenPlantWithIdIsRemoved() {
        GardenUser currentUser = userService.getUserById(user.getId());
        Plant plantToRemove = currentUser.getFavouritePlants().stream().toList().get(0);

        Boolean wasMatch = userService.removeFavouritePlant(currentUser.getId(), plantToRemove.getId());

        GardenUser updatedUser = userService.getUserById(user.getId());

        // assert that only one plant was removed
        Assertions.assertEquals(2, updatedUser.getFavouritePlants().size());
        // assert there was a match (e.g. found the plant in the set)
        Assertions.assertTrue(wasMatch);
        // assert that the plant has been removed from the set
        Assertions.assertTrue(updatedUser.getFavouritePlants()
                .stream().filter(plant -> plant.getId().equals(plantToRemove.getId())).toList().isEmpty());    }

    @Test
    void givenIAmAuthorizedAndHavePlantsWithIdsFavourited_whenIDeletePlantsWithIds_thenPlantsWithIdsAreRemoved() {
        GardenUser currentUser = userService.getUserById(user.getId());
        List<Plant> plants = currentUser.getFavouritePlants().stream().toList();
        Plant plantA = plants.get(0);
        Plant plantB = plants.get(1);
        Plant plantC = plants.get(2);

        Boolean wasMatchA = userService.removeFavouritePlant(currentUser.getId(), plantA.getId());
        Boolean wasMatchC = userService.removeFavouritePlant(currentUser.getId(), plantC.getId());

        GardenUser updatedUser = userService.getUserById(user.getId());

        // assert there is only 1 plant left
        Assertions.assertEquals(1, updatedUser.getFavouritePlants().size());
        // assert removal of A was matched and no longer in the list
        Assertions.assertTrue(wasMatchA);
        Assertions.assertTrue(updatedUser.getFavouritePlants()
                .stream().filter(plant -> plant.getId().equals(plantA.getId())).toList().isEmpty());
        // assert removal of C was matched and no longer in the list
        Assertions.assertTrue(wasMatchC);
        Assertions.assertTrue(updatedUser.getFavouritePlants()
                .stream().filter(plant -> plant.getId().equals(plantC.getId())).toList().isEmpty());
        // assert B is still in the list
        Assertions.assertFalse(updatedUser.getFavouritePlants()
                .stream().filter(plant -> plant.getId().equals(plantB.getId())).toList().isEmpty());

        System.out.println(plantService.getAllPlants().stream().filter(plant -> plant.getId().equals(plantA.getId())));
    }
}