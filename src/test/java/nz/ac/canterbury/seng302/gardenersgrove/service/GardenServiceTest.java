
package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@Import(GardenService.class)
public class GardenServiceTest {
    private GardenRepository gardenRepository;
    private GardenService gardenService;

    @BeforeEach
    public void setUp() {
        gardenRepository = Mockito.mock(GardenRepository.class);
        gardenService = new GardenService(gardenRepository);
    }

    @Test
    public void all_Fields_Valid_Garden_Successfully_Saved() {
        String gardenName = "Test Garden";
        String gardenLocation = "Test Location";
        String gardenSize = "100";
        String gardenDescription = "Test Description";

        Mockito.when(gardenRepository.save(Mockito.any(Garden.class))).thenReturn(new Garden(gardenName, gardenLocation, gardenSize, gardenDescription));

        Garden garden = gardenService.addGarden(new Garden(gardenName, gardenLocation, gardenSize, gardenDescription));

        Assertions.assertEquals(gardenName, garden.getName());
        Assertions.assertEquals(gardenLocation, garden.getLocation());
        Assertions.assertEquals(gardenSize, garden.getSize());

    }

    @Test
    public void getAllGardens_ReturnsAllGardens() {
        List<Garden> mockGardens = Arrays.asList(
                new Garden("Garden 1", "Location 1", "100", "Small"),
                new Garden("Garden 2", "Location 2", "200", "Big")
        );
        Mockito.when(gardenRepository.findAll()).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getAllGardens();

        Assertions.assertEquals(2, returnedGardens.size());
        Assertions.assertEquals(mockGardens, returnedGardens);
    }

    @Test
    public void getGardenById_ReturnsGarden() {
        Garden garden = new Garden("Garden 1", "Location 1", "100", "Small");

        Mockito.when(gardenRepository.findById(1L)).thenReturn(java.util.Optional.of(garden));

        Garden returnedGarden = gardenService.getGardenById(1L).get();

        Assertions.assertEquals(garden, returnedGarden);
    }

    @Test
    public void getGardensByOwnerId_ReturnsGardens() {
        List<Garden> mockGardens = Arrays.asList(
                new Garden("Garden 1", "Location 1", "100", "Small"),
                new Garden("Garden 2", "Location 2", "200", "Big")
        );
        Mockito.when(gardenRepository.findByOwnerId(1L)).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getGardensByOwnerId(1L);

        Assertions.assertEquals(2, returnedGardens.size());
        Assertions.assertEquals(mockGardens, returnedGardens);
    }





}