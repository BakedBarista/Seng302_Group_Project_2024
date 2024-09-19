package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
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
    private ZoneId timeZone;
    private Instant now;

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

        timeZone = ZoneId.of("Pacific/Auckland");
        now = LocalDate.of(2024, 1, 1).atStartOfDay(timeZone).toInstant();
    }

    private Garden gardenWithCoords(Double lat, Double lon) {
        return new Garden("garden1", "", "", "", "", "", "", lat, lon, "", null, null, null);
    }

    private Plant plantWithName(String name) {
        return new Plant(name, null, null, null);
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

    @ParameterizedTest
    @CsvSource({ ",,0,0", "0,0,,", ",,,", "0,,,", ",0,,", ",,0,", ",,,0" })
    void givenEitherUserHasNoGardensWithCoords_whenCalculateProximityQuotient_thenReturnNull(Double lat1, Double lon1,
            Double lat2, Double lon2) {
        List<Garden> user1Gardens = List.of(gardenWithCoords(lat1, lon1));
        List<Garden> user2Gardens = List.of(gardenWithCoords(lat2, lon2));
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertNull(result);
    }

    @ParameterizedTest
    @CsvSource({
            // Jack Erskine, Jack Erskine, 100
            "-43.522562, 172.581187, -43.522562, 172.581187, 100",
            // Jack Erskine, Town Hall, 75
            "-43.522562, 172.581187, -43.526812, 172.635688, 75",
            // Jack Erskine, Beehive, 0
            "-43.522562, 172.581187, -41.278437, 174.776687, 0"
    })
    void givenUsersInLocations_whenCalculateProximityQuotient_thenReturnPercentage(Double lat1, Double lon1,
            Double lat2, Double lon2, Double expected) {
        List<Garden> user1Gardens = List.of(gardenWithCoords(lat1, lon1));
        List<Garden> user2Gardens = List.of(gardenWithCoords(lat2, lon2));
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(user1Gardens);
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(user2Gardens);

        Double result = compatibilityService.calculateProximityQuotient(user1, user2);

        assertEquals(expected, result, 5.);
    }

    @Test
    void givenBothUsersHaveNoPlants_whenCalculatePlantSimilarity_thenReturn0() {
        List<Plant> user1Plants = List.of();
        List<Plant> user2Plants = List.of();
        when(plantService.getAllPlantsForUser(user1)).thenReturn(user1Plants);
        when(plantService.getAllPlantsForUser(user2)).thenReturn(user2Plants);

        double result = compatibilityService.calculatePlantSimilarity(user1, user2);

        assertEquals(0., result, 0.1);
    }

    @Test
    void givenNoPlantsInCommon_whenCalculatePlantSimilarity_thenReturn0() {
        List<Plant> user1Plants = List.of(plantWithName("Apple Tree"), plantWithName("Beetroot"));
        List<Plant> user2Plants = List.of(plantWithName("Cabbage"));
        when(plantService.getAllPlantsForUser(user1)).thenReturn(user1Plants);
        when(plantService.getAllPlantsForUser(user2)).thenReturn(user2Plants);

        double result = compatibilityService.calculatePlantSimilarity(user1, user2);

        assertEquals(0., result, 0.1);
    }

    @Test
    void givenSomePlantsInCommon_whenCalculatePlantSimilarity_thenReturnJaccardSimilarity() {
        List<Plant> user1Plants = List.of(plantWithName("Apple Tree"), plantWithName("Beetroot"));
        List<Plant> user2Plants = List.of(plantWithName("Beetroot"), plantWithName("Cabbage"));
        when(plantService.getAllPlantsForUser(user1)).thenReturn(user1Plants);
        when(plantService.getAllPlantsForUser(user2)).thenReturn(user2Plants);

        double result = compatibilityService.calculatePlantSimilarity(user1, user2);

        assertEquals(33.3, result, 0.1);
    }

    @ParameterizedTest
    @CsvSource({ ",2000-01-01", "2000-01-01,", "," })
    void givenEitherUserHasNoBirthday_whenCalculateAgeQuotient_thenReturnNull(LocalDate birthday1,
            LocalDate birthday2) {
        user1.setDateOfBirth(birthday1);
        user2.setDateOfBirth(birthday2);
        when(clock.getZone()).thenReturn(timeZone);
        when(clock.instant()).thenReturn(now);

        Double result = compatibilityService.calculateAgeQuotient(user1, user2);

        assertNull(result);
    }

    @ParameterizedTest
    @CsvSource({
            "2000-01-01,2000-01-01,100",
            "2001-01-01,2000-01-01,95",
            "2000-01-01,2004-01-01,80",
            "2000-01-01,2007-01-01,70",
            "2000-01-01,1970-01-01,20",
    })
    void givenUsersHaveBirthdays_whenCalculateAgeQuotient_thenReturnPercentage(LocalDate birthday1, LocalDate birthday2,
            Double expected) {
        user1.setDateOfBirth(birthday1);
        user2.setDateOfBirth(birthday2);
        when(clock.getZone()).thenReturn(timeZone);
        when(clock.instant()).thenReturn(now);

        Double result = compatibilityService.calculateAgeQuotient(user1, user2);

        assertEquals(expected, result, 5.);
    }

    @Test
    void givenBlankUsers_whenCalculateFriendshipCompatibility_thenReturn0() {
        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(List.of());
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(List.of());
        when(plantService.getAllPlantsForUser(user1)).thenReturn(List.of());
        when(plantService.getAllPlantsForUser(user2)).thenReturn(List.of());

        double result = compatibilityService.friendshipCompatibilityQuotient(user1, user2);

        assertEquals(0., result, 0.1);
    }

    @Test
    void givenReasonableUsers_whenCalculateFriendshipCompatibility_thenReturnReasonableValue() {
        user1.setDateOfBirth(LocalDate.of(2000, 1, 1));
        user2.setDateOfBirth(LocalDate.of(1970, 1, 1));

        when(gardenService.getPublicGardensByOwnerId(user1)).thenReturn(List.of(
                gardenWithCoords(-43.522562, 172.581187)));
        when(gardenService.getPublicGardensByOwnerId(user2)).thenReturn(List.of(
                gardenWithCoords(-43.526812, 172.635688)));

        when(plantService.getAllPlantsForUser(user1)).thenReturn(List.of(plantWithName("Tomato"), plantWithName("Cucumber")));
        when(plantService.getAllPlantsForUser(user2)).thenReturn(List.of(plantWithName("Tomato"), plantWithName("Cabbage")));

        when(clock.getZone()).thenReturn(timeZone);
        when(clock.instant()).thenReturn(now);

        double result = compatibilityService.friendshipCompatibilityQuotient(user1, user2);

        assertEquals(50., result, 10.);
    }
}
