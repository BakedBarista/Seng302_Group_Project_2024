package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
    private GardenUserService userService;

    @Autowired
    private PlantService plantService;

    private GardenUser user;

    private Boolean hasSetup = false;

    @BeforeEach
    void setup() {
        Plant plantA = new Plant("testPlantA", "1", "yellow", LocalDate.now());
        Plant plantB = new Plant("testPlantB", "2", "green", LocalDate.now());
        Plant plantC = new Plant("testPlantC", "1", "big", LocalDate.now());
        if (!hasSetup) {
            user = new GardenUser("john", "doe", "johndoe@someemail.com", "password", LocalDate.now());
            userService.addUser(user);
            plantService.save(plantA);
            plantService.save(plantB);
            plantService.save(plantC);

            hasSetup = true;
        }

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
        Assertions.assertFalse(updatedUser.getFavouritePlants().contains(plantToRemove));
    }

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
//        Assertions.assertEquals(1, updatedUser.getFavouritePlants().size());
        // assert removal of A was matched and no longer in the list
        Assertions.assertTrue(wasMatchA);
        Assertions.assertFalse(updatedUser.getFavouritePlants().contains(plantA));
        // assert removal of C was matched and no longer in the list
        Assertions.assertTrue(wasMatchC);
        Assertions.assertFalse(updatedUser.getFavouritePlants().contains(plantC));
        // assert B is still in the list
        Assertions.assertTrue(updatedUser.getFavouritePlants().contains(plantB));
    }
}