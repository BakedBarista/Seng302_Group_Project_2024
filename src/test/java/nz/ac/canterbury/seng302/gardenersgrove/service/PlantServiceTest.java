package nz.ac.canterbury.seng302.gardenersgrove.service;

import static org.mockito.Mockito.*;

// Assume these imports are correct for your test environment
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlantServiceTest {

    @Mock
    private GardenRepository gardenRepository;

    @Mock
    private PlantRepository plantRepository;

    private PlantService service; // Replace YourServiceClass with the actual class name that contains addPlant

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PlantService(plantRepository, gardenRepository);
    }

    @Test
    public void addPlant_PlantAddedToGarden() {
        Long gardenId = 1L;
        Garden mockGarden = new Garden(); // set properties as needed
        Plant mockPlant = new Plant(); // set properties as needed

        // Configure mocks
        when(gardenRepository.findById(gardenId)).thenReturn(Optional.of(mockGarden));
        when(plantRepository.save(any(Plant.class))).thenAnswer(i -> i.getArgument(0));

        // Call the method under test
        Plant savedPlant = service.addPlant(mockPlant, gardenId);

        // Assertions
        assertNotNull(savedPlant.getGarden(), "The plant's garden should not be null");
        assertEquals(mockGarden, savedPlant.getGarden(), "The plant's garden should be the one we found by ID");

        System.out.println(savedPlant.getGarden());

        // Verify interactions
        verify(gardenRepository).findById(gardenId);
        verify(plantRepository).save(mockPlant);
    }
}

