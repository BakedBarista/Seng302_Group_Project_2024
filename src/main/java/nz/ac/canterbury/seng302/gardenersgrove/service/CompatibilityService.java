package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CompatibilityService {

    private static final int EARTH_RADIUS = 6371;
    private GardenUserService gardenUserService;
    private GardenService gardenService;
    private PlantService plantService;
    private Clock clock;

    public CompatibilityService (GardenUserService gardenUserService, GardenService gardenService, PlantService plantService, Clock clock) {
        this.gardenUserService = gardenUserService;
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.clock = clock;
    }
    /**
     * Calculates the geographic distance between the average garden locations
     *
     * @param gardenUser1 First garden user
     * @param gardenUser2 Second garden user
     * @return distance or null if it cannot be calculated
     */
    private Double calculateGeographicDistance(GardenUser gardenUser1, GardenUser gardenUser2) {
        List<Garden> gardenUser1gardensList = gardenService.getPublicGardensByOwnerId(gardenUser1);
        List<Garden> gardenUser2gardensList = gardenService.getPublicGardensByOwnerId(gardenUser2);

        if (gardenUser1gardensList.isEmpty()
                || gardenUser1gardensList.stream()
                .allMatch(garden -> garden.getLat() == null && garden.getLon() == null)) {
            return null;
        }
        if (gardenUser2gardensList.isEmpty()
                || gardenUser2gardensList.stream()
                .allMatch(garden -> garden.getLat() == null && garden.getLon() == null)) {
            return null;
        }

        Double user1LatAverage = gardenUser1gardensList.stream()
                .map(BaseGarden::getLat)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        Double user1LongAverage = gardenUser1gardensList.stream()
                .map(BaseGarden::getLon)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        Double user2LatAverage = gardenUser2gardensList.stream()
                .map(BaseGarden::getLat)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        Double user2LongAverage = gardenUser2gardensList.stream()
                .map(BaseGarden::getLon)
                .filter(Objects::nonNull)
                .collect(Collectors.averagingDouble(Double::doubleValue));

        Double distance = Math.acos(Math.sin(user1LatAverage) * Math.sin(user2LatAverage)
                + Math.cos(user1LatAverage) * Math.cos(user2LatAverage) * Math.cos(user2LongAverage - user1LongAverage))
                * EARTH_RADIUS;

        return distance;
    }

    /**
     * Calculates the proximity quotient
     *
     * @param gardenUser1 First garden user
     * @param gardenUser2 Second garden user
     * @return proximity quotient or null if it cannot be calculated
     */
    private Double calculateProximityQuotient(GardenUser gardenUser1, GardenUser gardenUser2) {
        Double geographicDistance = calculateGeographicDistance(gardenUser1, gardenUser2);

        if (geographicDistance == null) {
            return null;
        }

        return 100 * Math.exp(0.5 - 0.01 * geographicDistance);
    }

    /**
     * Calculates the number of common plant names between garden users
     *
     * @param gardenUser1 First garden user
     * @param gardenUser2 Second garden user
     * @return number of common plant names
     */
    private int calculateCommonPlantNum(GardenUser gardenUser1, GardenUser gardenUser2) {

        List<Plant> user1PlantList = plantService.getAllPlantsForUser(gardenUser1);
        List<Plant> user2PlantList = plantService.getAllPlantsForUser(gardenUser2);

        Set<String> user1PlantNameSet = user1PlantList.stream().map(BasePlant::getName).collect(Collectors.toSet());
        Set<String> user2PlantNameSet = user2PlantList.stream().map(BasePlant::getName).collect(Collectors.toSet());

        Set<String> commonNames = new HashSet<>(user1PlantNameSet);
        commonNames.retainAll(user2PlantNameSet);

        return commonNames.size();

    }

    /**
     * Calculate the number of total plants ignoring duplicate names
     *
     * @param gardenUser1 First garden user
     * @param gardenUser2 Second garden user
     * @return number of unique total plants
     */
    private int calculateUniquePlantNum(GardenUser gardenUser1, GardenUser gardenUser2) {
        List<Plant> user1PlantList = plantService.getAllPlantsForUser(gardenUser1);
        List<Plant> user2PlantList = plantService.getAllPlantsForUser(gardenUser2);

        Set<String> user1PlantNameSet = user1PlantList.stream().map(BasePlant::getName).collect(Collectors.toSet());
        Set<String> user2PlantNameSet = user2PlantList.stream().map(BasePlant::getName).collect(Collectors.toSet());

        Set<String> uniqueNames = new HashSet<>(user1PlantNameSet);
        uniqueNames.addAll(user2PlantNameSet);

        return uniqueNames.size();
    }

    /**
     * Calculates the Jaccard similarity as a percentage
     *
     * @param gardenUser1 First garden user
     * @param gardenUser2 Second garden user
     * @return similarity percentage
     */
    private double calculatePlantSimilarity(GardenUser gardenUser1, GardenUser gardenUser2) {
        int totalPlants = calculateUniquePlantNum(gardenUser1, gardenUser2);
        int commonPlants = calculateCommonPlantNum(gardenUser1, gardenUser2);
        return 100. * commonPlants / totalPlants;
    }

    /**
     * Calculates the age of a user from their date of birth
     *
     * @param gardenUser Garden user
     * @return age or null
     */
    private Integer calculateAge(GardenUser gardenUser) {
        LocalDate now = clock.instant().atZone(clock.getZone()).toLocalDate();

        if (gardenUser.getDateOfBirth() == null) {
            return null;
        }
        return Period.between(gardenUser.getDateOfBirth(), now).getYears();
    }

    /**
     * Calculates the age quotient
     *
     * @param gardenUser1 First garden user
     * @param gardenUser2 Second garden user
     * @return age quotient or null
     */
    private Double calculateAgeQuotient(GardenUser gardenUser1, GardenUser gardenUser2) {
        Integer gardenUser1Age = calculateAge(gardenUser1);
        Integer gardenUser2Age = calculateAge(gardenUser2);

        if (gardenUser1Age == null || gardenUser2Age == null) {
            return null;
        }

        return 100 * Math.exp(0.5 - 0.05 * Math.abs(gardenUser1Age - gardenUser2Age));
    }

    /**
     * Calculates the compatibility quotient
     *
     * @param gardenUser1 First garden user
     * @param gardenUser2 Second garden user
     * @return compatibility quotient
     */
    public Double friendshipCompatibilityQuotient(GardenUser gardenUser1, GardenUser gardenUser2) {
        Double proximityQuotient = calculateProximityQuotient(gardenUser1, gardenUser2);
        double plantSimilarity = calculatePlantSimilarity(gardenUser1, gardenUser2);
        Double ageQuotient = calculateAgeQuotient(gardenUser1, gardenUser2);

        if (proximityQuotient == null) {
            proximityQuotient = 0.;
        }

        if (ageQuotient == null) {
            ageQuotient = 0.;
        }

        return 0.4 * proximityQuotient + 0.4 * plantSimilarity + 0.2 * ageQuotient;
    }



}
