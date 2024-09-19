package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.CompatibilityService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;

class CompatibilityServiceUnitTests {
    private GardenService gardenService;
    private PlantService plantService;
    private Clock clock;
    private CompatibilityService compatibilityService;

    private GardenUser user1;
    private GardenUser user2;

    @BeforeEach
    void setUp() {
        gardenService = mock(GardenService.class);
        plantService = mock(PlantService.class);
        clock = mock(Clock.class);
        compatibilityService = new CompatibilityService(gardenService, plantService, clock);

        user1 = new GardenUser();
        user1.setId(1L);
        user2 = new GardenUser();
        user2.setId(2L);
    }

    private Garden gardenWithCoords(Double lat, Double lon) {
        return new Garden("garden1", "", "", "", "", "", "", lat, lon, "", null, null, null);
    }

    @Test
    void givenUser1HasNoGardens_whenCalculateProximityQuotient_thenReturnNull() {
        List<Garden> user1Gardens = List.of();
        List<Garden> user2Gardens = List.of(gardenWithCoords(0., 0.));
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertNull(result);
    }

    @Test
    void givenUser2HasNoGardens_whenCalculateGeographicDistance_thenReturnNull() {
        List<Garden> user1Gardens = List.of(gardenWithCoords(0., 0.));
        List<Garden> user2Gardens = List.of();
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertNull(result);
    }

    @Test
    void givenUser1HasNoGardensWithCoords_whenCalculateProximityQuotient_thenReturnNull() {
        List<Garden> user1Gardens = List.of(gardenWithCoords(null, null));
        List<Garden> user2Gardens = List.of(gardenWithCoords(0., 0.));
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertNull(result);
    }

    @Test
    void givenUser2HasNoGardensWithCoords_whenCalculateProximityQuotient_thenReturnNull() {
        List<Garden> user1Gardens = List.of(gardenWithCoords(0., 0.));
        List<Garden> user2Gardens = List.of(gardenWithCoords(null, null));
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertNull(result);
    }

    @Test
    void givenUsersInExactSameLocation_whenCalculateProximityQuotient_thenReturn100() {
        // Jack Erskine
        List<Garden> user1Gardens = List.of(gardenWithCoords(-43.522562, 172.581187));
        // Jack Erskine
        List<Garden> user2Gardens = List.of(gardenWithCoords(-43.522562, 172.581187));
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertEquals(100., result, 0.1);
    }

    @Test
    void givenUsersInSameCity_whenCalculateProximityQuotient_thenReturnApprox75() {
        // Jack Erskine
        List<Garden> user1Gardens = List.of(gardenWithCoords(-43.522562, 172.581187));
        // Town Hall
        List<Garden> user2Gardens = List.of(gardenWithCoords(-43.526812, 172.635688));
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertEquals(75., result, 10.);
    }

    @Test
    void givenUsersInDifferentCities_whenCalculateProximityQuotient_thenReturnApprox0() {
        // Jack Erskine
        List<Garden> user1Gardens = List.of(gardenWithCoords(-43.522562, 172.581187));
        // Beehive
        List<Garden> user2Gardens = List.of(gardenWithCoords(-41.278437, 174.776687));
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertEquals(0., result, 10.);
    }
}
