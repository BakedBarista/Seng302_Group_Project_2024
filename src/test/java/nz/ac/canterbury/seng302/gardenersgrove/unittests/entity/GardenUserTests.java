package nz.ac.canterbury.seng302.gardenersgrove.unittests.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GardenUserTests {
    @Test
    void givenHasLName_whenGetFullName_thenReturnsFullName() {
        GardenUser user = new GardenUser();
        user.setFname("John");
        user.setLname("Doe");

        assertEquals("John Doe", user.getFullName());
    }

    @Test
    void givenHasNoLName_whenGetFullName_thenReturnsFName() {
        GardenUser user = new GardenUser();
        user.setFname("John");
        user.setLname(null);

        assertEquals("John", user.getFullName());
    }

    @Test
    void givenFavouritePlantIsEmpty_whenAddFavouritePlant_thenPlantIsAdded() {
        GardenUser user = new GardenUser();
        Plant plant = new Plant();
        Set<Plant> plants = new HashSet<>();
        user.setFavouritePlants(plants);
        user.addFavouritePlant(plant);
        assertEquals(plants, user.getFavouritePlants());

    }
}
