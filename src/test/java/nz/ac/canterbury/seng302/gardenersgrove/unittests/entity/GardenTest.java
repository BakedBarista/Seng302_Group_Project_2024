package nz.ac.canterbury.seng302.gardenersgrove.unittests.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;

class GardenTest {
    @ParameterizedTest
    @CsvSource({
        "2,Main St,Suburb,City,Country,'2 Main St, Suburb, City, Country'",
        ",Main St,Suburb,City,Country,'Main St, Suburb, City, Country'",
        ",,Suburb,City,Country,'Suburb, City, Country'",
        ",,,City,Country,'City, Country'",
        ",,,,Country,'Country'",
        ",,,,,''",
    })
    void givenGardenAtAddress_whenGetAddress_thenReturnAddress(String streetNumber, String streetName, String suburb, String city, String country, String expectedAddress) {
        Garden garden = new Garden("Test Garden", streetNumber, streetName, suburb, city, country, null, null, null, null, null, null, null);
        
        String address = garden.getAddress();

        assertEquals(expectedAddress, address);
    }

    @Test
    void givenNullFieldsInGarden_whenGetAddress_thenReturnEmptyString() {
        Garden garden = new Garden("Test Garden", null, null, null, null, null, null, null, null, null, null, null, null);
        
        String address = garden.getAddress();

        assertEquals("", address);
    }
}
