package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class DeleteFavouritePlantControllerTest {

    @Autowired
    private GardenUserService userService;

    @Autowired
    private PlantService plantService;

    private GardenUser user;

    @BeforeEach
    void setup() {
        Plant plantA = new Plant("testPlantA", "1", "yellow", LocalDate.now());
        Plant plantB = new Plant("testPlantB", "2", "green", LocalDate.now());
        Plant plantC = new Plant("testPlantC", "1", "big", LocalDate.now());
        plantService.save(plantA);
        plantService.save(plantB);
        plantService.save(plantC);

        user = new GardenUser("john", "doe", "johndoe@someemail.com", "password", LocalDate.now());
        user.setFavouritePlants(List.of(plantA, plantB, plantC));

        userService.addUser(user);
    }

    @Test
    void givenIAmAuthorizedAndHavePlantWithIdFavourited_whenIDeletePlantWithId_thenPlantWithIdIsRemoved() {
        GardenUser currentUser = userService.getUserById(user.getId());
        userService.removeFavouritePlant(user.getId(), user.getFavouritePlants().get(0).getId());
    }
}