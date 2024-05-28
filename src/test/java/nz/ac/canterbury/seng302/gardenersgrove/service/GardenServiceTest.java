
package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions.U24BrowsingGardenByTagFeature.pageable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(GardenService.class)
public class GardenServiceTest {
    private GardenRepository gardenRepository;
    private GardenService gardenService;

    @Mock
    private Pageable pageable;

    @Mock
    private Page<Garden> expectedPage;

    @BeforeEach
    public void setUp() {
        gardenRepository = mock(GardenRepository.class);
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
        when(gardenRepository.findAll()).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getAllGardens();

        assertEquals(2, returnedGardens.size());
        assertEquals(mockGardens, returnedGardens);
    }

    @Test
    public void getGardenById_ReturnsGarden() {
        Garden garden = new Garden("Garden", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "100", "Big");

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
        when(gardenServiceMock.getPageForPublicGardens(pageable)).thenReturn(expectedPage);

        Page<Garden> result = gardenServiceMock.getPageForPublicGardens(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(gardenServiceMock).getPageForPublicGardens(pageable);
    }

    @Test
    public void getGardensByOwnerId_ReturnsGardens() {
        List<Garden> mockGardens = Arrays.asList(
                new Garden("Garden 1", "1","Test Road","Test Suburb","Test City","Test Country","1000",0.55,0.55, "100", "Small"),
                new Garden("Garden 2", "2","Test Road","Test Suburb","Test City","Test Country","1000",0.55,0.55, "100", "Small")
        );
        Mockito.when(gardenRepository.findByOwnerId(1L)).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getGardensByOwnerId(1L);

        assertEquals(2, returnedGardens.size());
        assertEquals(mockGardens, returnedGardens);
    }

    @Test
    public void testFindGardensBySearchAndTags_WithEmptyTags() {

        String search = "flowers";
        List<String> tags = Collections.emptyList();
        when(gardenRepository.findPageThatContainsQuery(search, pageable)).thenReturn(expectedPage);

        Page<Garden> result = gardenService.findGardensBySearchAndTags(search, tags, pageable);

        assertEquals( expectedPage, result);
        verify(gardenRepository).findPageThatContainsQuery(search, pageable);
        verifyNoMoreInteractions(gardenRepository);
    }

    @Test
    public void testFindGardensBySearchAndTags_WithNullTags() {

        String search = "flowers";
        when(gardenRepository.findPageThatContainsQuery(search, pageable)).thenReturn(expectedPage);

        Page<Garden> result = gardenService.findGardensBySearchAndTags(search, null, pageable);

        assertEquals( expectedPage, result);
        verify(gardenRepository).findPageThatContainsQuery(search, pageable);
        verifyNoMoreInteractions(gardenRepository);
    }






}