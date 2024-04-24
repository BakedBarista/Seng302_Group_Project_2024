
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
        String streetNumber = "1";
        String streetName = "Test Street";
        String suburb = "Test Suburb";
        String city = "Test City";
        String country = "Test Country";
        String postCode = "1234";
        Double lon = 1.0;
        Double lat = 2.0;
        String gardenSize = "100";
        String gardenDescription = "Test Description";

        Mockito.when(gardenRepository.save(Mockito.any(Garden.class))).thenReturn(new Garden(gardenName, streetNumber,streetName,suburb,city,country,postCode,lon,lat, gardenSize, gardenDescription));

        Garden garden = gardenService.addGarden(new Garden(gardenName, streetNumber,streetName,suburb,city,country,postCode,lon,lat, gardenSize, gardenDescription));

        Assertions.assertEquals(gardenName, garden.getName());
        Assertions.assertEquals(streetNumber, garden.getStreetNumber());
        Assertions.assertEquals(streetName,garden.getStreetName());
        Assertions.assertEquals(suburb,garden.getSuburb());
        Assertions.assertEquals(city,garden.getCity());
        Assertions.assertEquals(country,garden.getCountry());
        Assertions.assertEquals(postCode,garden.getPostCode());
        Assertions.assertEquals(gardenDescription,garden.getDescription());
        Assertions.assertEquals(gardenSize, garden.getSize());

    }

    @Test
    public void getAllGardens_ReturnsAllGardens() {
        List<Garden> mockGardens = Arrays.asList(
                new Garden("Garden1", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "100", "Big"),
                new Garden("Garden2", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041", 1.0,2.0,"100", "Small")
        );
        Mockito.when(gardenRepository.findAll()).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getAllGardens();

        Assertions.assertEquals(2, returnedGardens.size());
        Assertions.assertEquals(mockGardens, returnedGardens);
    }

    @Test
    public void getGardenById_ReturnsGarden() {
        Garden garden = new Garden("Garden", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "100", "Big");

        Mockito.when(gardenRepository.findById(1L)).thenReturn(java.util.Optional.of(garden));

        Garden returnedGarden = gardenService.getGardenById(1L).get();

        Assertions.assertEquals(garden, returnedGarden);
    }




}