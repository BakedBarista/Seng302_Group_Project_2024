
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
        Double gardenSize = 100D;
        String gardenDescription = "Test Description";

        Garden garden = new Garden(gardenName, streetNumber,streetName,suburb,city,country,postCode,lon,lat, gardenDescription, gardenSize);
        Mockito.when(gardenRepository.save(Mockito.any(Garden.class))).thenReturn(garden);

        Garden gardenReturned = gardenService.addGarden(garden);

        Assertions.assertEquals(gardenName, gardenReturned.getName());
        Assertions.assertEquals(streetNumber, gardenReturned.getStreetNumber());
        Assertions.assertEquals(streetName,gardenReturned.getStreetName());
        Assertions.assertEquals(suburb,gardenReturned.getSuburb());
        Assertions.assertEquals(city,gardenReturned.getCity());
        Assertions.assertEquals(country,gardenReturned.getCountry());
        Assertions.assertEquals(postCode,gardenReturned.getPostCode());
        Assertions.assertEquals(gardenDescription,gardenReturned.getDescription());
        Assertions.assertEquals(gardenSize, gardenReturned.getSize());

    }

    @Test
    public void getAllGardens_ReturnsAllGardens() {
        List<Garden> mockGardens = Arrays.asList(
                new Garden("Garden1", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "Big", null),
                new Garden("Garden2", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041", 1.0,2.0,"Small", null)
        );
        when(gardenRepository.findAll()).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getAllGardens();

        assertEquals(2, returnedGardens.size());
        assertEquals(mockGardens, returnedGardens);
    }

    @Test
    public void getGardenById_ReturnsGarden() {
        Garden garden = new Garden("Garden", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "Big", null);

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
                new Garden("Garden 1", "1","Test Road","Test Suburb","Test City","Test Country","1000",0.55,0.55, "small", null),
                new Garden("Garden 2", "2","Test Road","Test Suburb","Test City","Test Country","1000",0.55,0.55, "small", null)
        );
        Mockito.when(gardenRepository.findByOwnerId(1L)).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getGardensByOwnerId(1L);

        assertEquals(2, returnedGardens.size());
        assertEquals(mockGardens, returnedGardens);
    }

    @Test
    void testFindGardensBySearchAndTags_WithEmptyTags() {

        String search = "flowers";
        List<String> tags = Collections.emptyList();
        when(gardenRepository.findPageThatContainsQuery(search, pageable)).thenReturn(expectedPage);

        Page<Garden> result = gardenService.findGardensBySearchAndTags(search, tags, pageable);

        assertEquals( expectedPage, result);
        verify(gardenRepository).findPageThatContainsQuery(search, pageable);
        verifyNoMoreInteractions(gardenRepository);
    }

    @Test
    void testFindGardensBySearchAndTags_WithNullTags() {

        String search = "flowers";
        when(gardenRepository.findPageThatContainsQuery(search, pageable)).thenReturn(expectedPage);

        Page<Garden> result = gardenService.findGardensBySearchAndTags(search, null, pageable);

        assertEquals( expectedPage, result);
        verify(gardenRepository).findPageThatContainsQuery(search, pageable);
        verifyNoMoreInteractions(gardenRepository);
    }






}