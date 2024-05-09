package nz.ac.canterbury.seng302.gardenersgrove.unittests.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

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
}
