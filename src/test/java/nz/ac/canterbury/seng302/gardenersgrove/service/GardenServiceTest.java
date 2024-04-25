
package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(GardenService.class)
public class GardenServiceTest {
    private GardenRepository gardenRepository;
    private GardenService gardenService;

    @BeforeEach
    public void setUp() {
        gardenRepository = mock(GardenRepository.class);
        gardenService = new GardenService(gardenRepository);
    }

    @Test
    public void all_Fields_Valid_Garden_Successfully_Saved() {
        String gardenName = "Test Garden";
        String gardenLocation = "Test Location";
        String gardenSize = "100";
        String gardenDescription = "Test Description";

        when(gardenRepository.save(Mockito.any(Garden.class))).thenReturn(new Garden(gardenName, gardenLocation, gardenSize, gardenDescription));

        Garden garden = gardenService.addGarden(new Garden(gardenName, gardenLocation, gardenSize, gardenDescription));

        assertEquals(gardenName, garden.getName());
        assertEquals(gardenLocation, garden.getLocation());
        assertEquals(gardenSize, garden.getSize());

    }

    @Test
    public void getAllGardens_ReturnsAllGardens() {
        List<Garden> mockGardens = Arrays.asList(
                new Garden("Garden 1", "Location 1", "100", "Small"),
                new Garden("Garden 2", "Location 2", "200", "Big")
        );
        when(gardenRepository.findAll()).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getAllGardens();

        assertEquals(2, returnedGardens.size());
        assertEquals(mockGardens, returnedGardens);
    }

    @Test
    public void getGardenById_ReturnsGarden() {
        Garden garden = new Garden("Garden 1", "Location 1", "100", "Small");

        when(gardenRepository.findById(1L)).thenReturn(java.util.Optional.of(garden));

        Garden returnedGarden = gardenService.getGardenById(1L).get();

        assertEquals(garden, returnedGarden);
    }

    @Test
    public void testGetPublicGardens() {
        GardenService gardenServiceMock = mock(GardenService.class);
        Pageable pageable = PageRequest.of(0, 10);
        List<Garden> gardens = Arrays.asList(new Garden(), new Garden());
        Page<Garden> expectedPage = new PageImpl<>(gardens, pageable, gardens.size());
        when(gardenServiceMock.getPublicGardens(pageable)).thenReturn(expectedPage);

        // Assuming the controller calls the method like this
        Page<Garden> result = gardenServiceMock.getPublicGardens(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size()); // Check if the content size is as expected
        verify(gardenServiceMock).getPublicGardens(pageable);
    }

    }
