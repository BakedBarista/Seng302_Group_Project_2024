
package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;

@ExtendWith(MockitoExtension.class)
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
        byte[] gardenImage = null;
        String gardenImageContent = null;
        Garden garden = new Garden(gardenName, streetNumber,streetName,suburb,city,country,postCode,lon,lat, gardenDescription, gardenSize, gardenImage, gardenImageContent);
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
                new Garden("Garden1", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "Big", null, null, null),
                new Garden("Garden2", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041", 1.0,2.0,"Small", null, null, null)
        );
        when(gardenRepository.findAll()).thenReturn(mockGardens);

        List<Garden> returnedGardens = gardenService.getAllGardens();

        assertEquals(2, returnedGardens.size());
        assertEquals(mockGardens, returnedGardens);
    }

    @Test
    public void getGardenById_ReturnsGarden() {
        Garden garden = new Garden("Garden", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "Big", null, null, null);

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
                new Garden("Garden 1", "1","Test Road","Test Suburb","Test City","Test Country","1000",0.55,0.55, "small", null, null, null),
                new Garden("Garden 2", "2","Test Road","Test Suburb","Test City","Test Country","1000",0.55,0.55, "small", null, null, null)
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


    @Test
    void setGardenImageWithValidId_imageSaved() {
        long id = 1L;

        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);
        Garden garden = new Garden();
        garden.setId(id);
        Mockito.when(gardenRepository.findById(id)).thenReturn(Optional.of(garden));

        gardenService.setGardenImage(id, file);

        verify(gardenRepository, times(1)).save(garden);
        assertEquals(contentType, garden.getGardenImageContentType());
        assertEquals(imageBytes, garden.getGardenImage());
    }

    @Test
    void setPlantImageWithNonExistentId_imageNotSaved() {
        long id = 1L;
        byte[] image = {};
        String contentType = "image/svg";
        String name = "plant.png";
        String originalFilename = "plant.png";
        MultipartFile file = new MockMultipartFile(name,originalFilename,contentType,image);

        Mockito.when(gardenRepository.findById(id)).thenReturn(Optional.empty());

        gardenService.setGardenImage(id, file);
        verify(gardenRepository, never()).save(any());
    }




}