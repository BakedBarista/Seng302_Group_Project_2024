package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.DeleteFavouritePlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;

@SuppressWarnings("SpringJavaInjectionsPointsAutowiringInspection")
@SpringBootTest
public class DeleteFavouritePlantControllerTest {

    @Autowired
    private DeleteFavouritePlantController controller;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private GardenUserService userService;

    @Autowired
    private PlantService plantService;

    private GardenUser user;

    private Authentication authentication;
    @BeforeEach
    void setup() {
        authentication = mock(Authentication.class);

        if (userService.getUserByEmail("johndoe@someemail.com") == null) {
            user = new GardenUser("john", "doe", "johndoe@someemail.com", "password", LocalDate.now());
            userService.addUser(user);
        } else {
            user = userService.getUserByEmail("johndoe@someemail.com");
            Mockito.when(authentication.getPrincipal()).thenReturn(user.getId());
            user.getFavouritePlants().forEach(plant -> controller.deleteFavouritePlant(Map.of("plantId", plant.getId()), authentication));
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

        Mockito.when(authentication.getPrincipal()).thenReturn(user.getId());
    }

    @Test
    void givenIAmAuthorizedAndHavePlantWithIdFavourited_whenIDeletePlantWithId_thenPlantWithIdIsRemoved() {
        Plant plantToRemove = user.getFavouritePlants().stream().toList().get(0);
        Map<String, Long> responseBody = new HashMap<>();
        responseBody.put("plantId", plantToRemove.getId());

        System.out.println(user.getFavouritePlants());
        System.out.println(user.getFavouritePlants().size());
        System.out.println(responseBody);

        Boolean wasMatch = controller.deleteFavouritePlant(responseBody, authentication);

        GardenUser updatedUser = userService.getUserById(user.getId());
        System.out.println(updatedUser.getFavouritePlants());

        // assert that only one plant was removed
        Assertions.assertEquals(2, updatedUser.getFavouritePlants().size());
        // assert there was a match (e.g. found the plant in the set)
        Assertions.assertTrue(wasMatch);
        // assert that the plant has been removed from the set
        Assertions.assertTrue(updatedUser.getFavouritePlants()
                .stream().filter(plant -> plant.getId().equals(plantToRemove.getId())).toList().isEmpty());    }

    @Test
    void givenIAmAuthorizedAndHavePlantsWithIdsFavourited_whenIDeletePlantsWithIds_thenPlantsWithIdsAreRemoved() {
        List<Plant> plants = user.getFavouritePlants().stream().toList();
        Plant plantA = plants.get(0);
        Plant plantB = plants.get(1);
        Plant plantC = plants.get(2);

        Map<String, Long> responseBodyA = new HashMap<>();
        responseBodyA.put("plantId", plantA.getId());
        Boolean wasMatchA = controller.deleteFavouritePlant(responseBodyA, authentication);

        Map<String, Long> responseBodyC = new HashMap<>();
        responseBodyC.put("plantId", plantC.getId());
        Boolean wasMatchC = controller.deleteFavouritePlant(responseBodyC, authentication);

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