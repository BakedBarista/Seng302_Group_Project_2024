package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.CompatibilityService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;

@DataJpaTest
@Import({ CompatibilityService.class, GardenService.class, PlantService.class })
class CompatibilityServiceIntegrationTests {
    @Autowired
    private CompatibilityService compatibilityService;

    @Autowired
    private GardenUserRepository userRepo;
    @Autowired
    private GardenRepository gardenRepo;
    @Autowired
    private PlantRepository plantRepo;
    @MockBean
    private Clock clock;

    private GardenUser user1;
    private GardenUser user2;
    private ZoneId timeZone;
    private Instant now;

    @BeforeEach
    void setUp() {
        user1 = new GardenUser("User", "1", "user1@compat.friends", "password", null);
        userRepo.save(user1);
        user2 = new GardenUser("User", "2", "user2@compat.friends", "password", null);
        userRepo.save(user2);

        timeZone = ZoneId.of("Pacific/Auckland");
        now = LocalDate.of(2024, 1, 1).atStartOfDay(timeZone).toInstant();
    }

    @AfterEach
    void tearDown() {
        userRepo.delete(user1);
        userRepo.delete(user2);
    }

    private Garden gardenWithCoords(Double lat, Double lon) {
        return new Garden("garden1", "", "", "", "Christchurch", "NZ", "", lat, lon, "", null, null, null);
    }

    private Plant plantWithName(String name) {
        return new Plant(name, "", "", null);
    }

    @Test
    void givenBlankUsers_whenCalculateFriendshipCompatibility_thenReturn0() {
        double result = compatibilityService.friendshipCompatibilityQuotient(user1, user2);

        assertEquals(0., result, 0.1);
    }

    @Test
    void givenReasonableUsers_whenCalculateFriendshipCompatibility_thenReturnReasonableValue() {
        user1.setDateOfBirth(LocalDate.of(2000, 1, 1));
        userRepo.save(user1);
        user2.setDateOfBirth(LocalDate.of(1970, 1, 1));
        userRepo.save(user2);

        Garden garden1 = gardenWithCoords(-43.522562, 172.581187);
        garden1.setOwner(user1);
        garden1.setPublic(true);
        gardenRepo.save(garden1);
        Plant plant1 = plantWithName("Tomato");
        plant1.setGarden(garden1);
        plantRepo.save(plant1);
        Plant plant2 = plantWithName("Cucumber");
        plant2.setGarden(garden1);
        plantRepo.save(plant2);

        Garden garden2 = gardenWithCoords(-43.526812, 172.635688);
        garden2.setOwner(user2);
        garden2.setPublic(true);
        gardenRepo.save(garden2);
        Plant plant3 = plantWithName("Tomato");
        plant3.setGarden(garden2);
        plantRepo.save(plant3);
        Plant plant4 = plantWithName("Cabbage");
        plant4.setGarden(garden2);
        plantRepo.save(plant4);

        when(clock.getZone()).thenReturn(timeZone);
        when(clock.instant()).thenReturn(now);

        double result = compatibilityService.friendshipCompatibilityQuotient(user1, user2);

        assertEquals(50., result, 10.);
    }
}
