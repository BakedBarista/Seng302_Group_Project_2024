package nz.ac.canterbury.seng302.gardenersgrove.unittests.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;

class PlantTest {
    @Test
    void givenCountHasDecimal_whenNormalized_thenDecimalIsRemoved() {
        Plant plant = new Plant();
        plant.setCount("1.0");

        plant.normalize();

        assertEquals("1", plant.getCount());
    }

    @Test
    void givenCountHasNoDecimal_whenNormalized_thenCountIsUnchanged() {
        Plant plant = new Plant();
        plant.setCount("1");

        plant.normalize();

        assertEquals("1", plant.getCount());
    }

    @Test
    void givenCountIsNull_whenNormalized_thenCountIsUnchanged() {
        Plant plant = new Plant();
        plant.setCount(null);

        plant.normalize();

        assertNull(plant.getCount());
    }
}
