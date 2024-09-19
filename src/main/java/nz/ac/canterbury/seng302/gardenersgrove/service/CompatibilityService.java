package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CompatibilityService {

    /**
     * The Earth's radius in km
     */
    private static final int EARTH_RADIUS = 6371;
    /**
     * The number of seconds in a year
     */
    private static final double SECONDS_IN_YEAR = 365.25 * 24 * 60 * 60;

    private final GardenService gardenService;
    private final PlantService plantService;
    private final Clock clock;

    public CompatibilityService(GardenService gardenService, PlantService plantService, Clock clock) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.clock = clock;
    }

    /**
     * Calculates the geographic distance between the average garden locations
     *
     * @param user1 First garden user
     * @param user2 Second garden user
     * @return distance or null if it cannot be calculated
     */
    private Double calculateGeographicDistance(GardenUser user1, GardenUser user2) {
        List<Garden> user1Gardens = gardenService.getPublicGardensByOwnerId(user1);
        List<Garden> user2Gardens = gardenService.getPublicGardensByOwnerId(user2);

        if (user1Gardens.isEmpty() || user1Gardens.stream()
                .allMatch(garden -> garden.getLat() == null && garden.getLon() == null)) {
            return null;
        }
        if (user2Gardens.isEmpty() || user2Gardens.stream()
                .allMatch(garden -> garden.getLat() == null && garden.getLon() == null)) {
            return null;
        }

        Double user1LatAverage = user1Gardens.stream()
                .map(BaseGarden::getLat)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        Double user1LongAverage = user1Gardens.stream()
                .map(BaseGarden::getLon)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        Double user2LatAverage = user2Gardens.stream()
                .map(BaseGarden::getLat)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        Double user2LongAverage = user2Gardens.stream()
                .map(BaseGarden::getLon)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        return calculateDistance(user1LatAverage, user1LongAverage, user2LatAverage, user2LongAverage);
    }

    /**
     * Calculates the distance between two points on the Earth's surface
     *
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return distance in km
     */
    private Double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // https://community.fabric.microsoft.com/t5/Desktop/Distance-Calculation-in-Power-BI/m-p/207848/highlight/true#M91602
        return Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * EARTH_RADIUS;
    }

    /**
     * Calculates the proximity quotient
     *
     * @param user1 First garden user
     * @param user2 Second garden user
     * @return proximity quotient or null if it cannot be calculated
     */
    public Double calculateProximityQuotient(GardenUser user1, GardenUser user2) {
        Double distance = calculateGeographicDistance(user1, user2);

        if (distance == null) {
            return null;
        }

        return 100 * Math.exp(-0.001 * distance);
    }

    /**
     * Calculates the number of common plant names between garden users
     *
     * @param user1PlantNameSet Set of plant names for the first garden user
     * @param user2PlantNameSet Set of plant names for the second garden user
     * @return number of common plant names
     */
    private int calculateCommonPlantNum(Set<String> user1PlantNameSet, Set<String> user2PlantNameSet) {
        Set<String> commonNames = new HashSet<>(user1PlantNameSet);
        commonNames.retainAll(user2PlantNameSet);

        return commonNames.size();
    }

    /**
     * Calculate the number of total plants ignoring duplicate names
     *
     * @param user1PlantNameSet Set of plant names for the first garden user
     * @param user2PlantNameSet Set of plant names for the second garden user
     * @return number of unique total plants
     */
    private int calculateUniquePlantNum(Set<String> user1PlantNameSet, Set<String> user2PlantNameSet) {
        Set<String> uniqueNames = new HashSet<>(user1PlantNameSet);
        uniqueNames.addAll(user2PlantNameSet);

        return uniqueNames.size();
    }

    /**
     * Calculates the Jaccard similarity as a percentage
     *
     * @param user1 First garden user
     * @param user2 Second garden user
     * @return similarity percentage
     */
    public double calculatePlantSimilarity(GardenUser user1, GardenUser user2) {
        List<Plant> user1PlantList = plantService.getAllPlantsForUser(user1);
        List<Plant> user2PlantList = plantService.getAllPlantsForUser(user2);

        Set<String> user1PlantNameSet = user1PlantList.stream().map(BasePlant::getName).collect(Collectors.toSet());
        Set<String> user2PlantNameSet = user2PlantList.stream().map(BasePlant::getName).collect(Collectors.toSet());

        int totalPlants = calculateUniquePlantNum(user1PlantNameSet, user2PlantNameSet);
        int commonPlants = calculateCommonPlantNum(user1PlantNameSet, user2PlantNameSet);
        if (totalPlants == 0) {
            return 0;
        }
        return 100. * commonPlants / totalPlants;
    }

    /**
     * Calculates the age of a user from their date of birth
     *
     * @param user Garden user
     * @return age or null
     */
    private Double calculateAge(GardenUser user) {
        Instant now = clock.instant();

        if (user.getDateOfBirth() == null) {
            return null;
        }
        Instant dob = user.getDateOfBirth().atStartOfDay().atZone(clock.getZone()).toInstant();
        return Duration.between(dob, now).getSeconds() / SECONDS_IN_YEAR;
    }

    /**
     * Calculates the age quotient
     *
     * @param user1 First garden user
     * @param user2 Second garden user
     * @return age quotient or null
     */
    public Double calculateAgeQuotient(GardenUser user1, GardenUser user2) {
        Double user1Age = calculateAge(user1);
        Double user2Age = calculateAge(user2);

        if (user1Age == null || user2Age == null) {
            return null;
        }

        return 100 * Math.exp(-0.05 * Math.abs(user1Age - user2Age));
    }

    /**
     * Calculates the compatibility quotient
     *
     * @param user1 First garden user
     * @param user2 Second garden user
     * @return compatibility quotient
     */
    public Double friendshipCompatibilityQuotient(GardenUser user1, GardenUser user2) {
        Double proximityQuotient = calculateProximityQuotient(user1, user2);
        double plantSimilarity = calculatePlantSimilarity(user1, user2);
        Double ageQuotient = calculateAgeQuotient(user1, user2);

        if (proximityQuotient == null) {
            proximityQuotient = 0.;
        }

        if (ageQuotient == null) {
            ageQuotient = 0.;
        }

        return 0.4 * proximityQuotient + 0.4 * plantSimilarity + 0.2 * ageQuotient;
    }

}
