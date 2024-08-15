package nz.ac.canterbury.seng302.gardenersgrove.unittests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantHistoryRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;
    @Mock
    private PlantHistoryRepository plantHistoryRepository;
    @Mock
    private GardenRepository gardenRepository;

    @Mock
    private GardenUserRepository gardenUserRepository;
    @Mock
    private Clock clock;

    @Mock
    private GardenUserService gardenUserService;

    @Mock
    private GardenUserRepository userRepository;

    @Mock
    private GardenService gardenService;

    @InjectMocks
    private PlantService plantService;

    @Test
    void AddPlant_ValidPlantWithGardenId_ReturnsPlantWithCorrectGardenId() {
        PlantDTO testPlant = new PlantDTO("Rose", "5", "Flower", "1970-01-01");
        Garden testGarden = new Garden("Garden", "1","Ilam Road","Ilam",
                "Christchurch","New Zealand","8041",1.0,2.0, "Big", null, null, null);
        Long gardenId = 1L;
        testGarden.setId(gardenId);

        Mockito.when(gardenRepository.findById(Mockito.any())).thenReturn(Optional.of(testGarden));

        Mockito.when(plantRepository.save(Mockito.any(Plant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Plant result = plantService.createPlant(testPlant, gardenId);
        assertEquals(result.getGarden().getId(), gardenId);
    }

    @Test
    void AddPlant_InvalidGardenId_ThrowsException() {
        PlantDTO testPlant = new PlantDTO("Rose", "5", "Flower", "1970-01-01");
        Long gardenId = 1L;

        Mockito.when(gardenRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> plantService.createPlant(testPlant, gardenId));
        verify(plantRepository, Mockito.never()).save(Mockito.any(Plant.class));
    }

    @Test
    void GetAllPlants_MoreThanOnePlantInDB_ReturnsListOfPlants() {
        Plant testPlant1 = new Plant("Rose", "5", "Flower", LocalDate.of(1970, 1, 1));
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", LocalDate.of(1970, 1, 1));

        Mockito.when(plantRepository.findAll()).thenReturn(List.of(testPlant1, testPlant2));
        assertEquals(2, plantService.getAllPlants().size());
        assertEquals(testPlant1, plantService.getAllPlants().get(0));
        assertEquals(testPlant2, plantService.getAllPlants().get(1));
    }

    @Test
    void GetAllPlants_NoPlantsInDB_ReturnsEmptyList() {
        Mockito.when(plantRepository.findAll()).thenReturn(List.of());
        assertEquals(0, plantService.getAllPlants().size());
    }

    @Test
    void GetPlantsByGardenId_TwoPlantsOneGarden_ReturnBothPlants() {
        Plant testPlant1 = new Plant("Rose", "5", "Flower", LocalDate.of(1970, 1, 1));
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", LocalDate.of(1970, 1, 1));
        Long gardenId = 1L;

        Mockito.when(plantRepository.findByGardenId(gardenId)).thenReturn(List.of(testPlant1, testPlant2));
        assertEquals(2, plantService.getPlantsByGardenId(gardenId).size());
        assertEquals(testPlant1, plantService.getPlantsByGardenId(gardenId).get(0));
        assertEquals(testPlant2, plantService.getPlantsByGardenId(gardenId).get(1));
    }

    @Test
    void GetPlantsByGardenId_NoPlantsInGarden_ReturnsEmptyList() {
        Long gardenId = 1L;
        Mockito.when(plantRepository.findByGardenId(gardenId)).thenReturn(List.of());
        assertEquals(0, plantService.getPlantsByGardenId(gardenId).size());
    }

    @Test
    void GetPlantsByGardenId_InvalidGardenId_ReturnsEmptyList() {
        Long gardenId = 1L;
        Mockito.when(plantRepository.findByGardenId(gardenId)).thenReturn(List.of());
        assertEquals(0, plantService.getPlantsByGardenId(gardenId).size());
    }

    @Test
    void GetPlantsByGardenId_ThreePlantsTwoInGarden1OneInGardenTwo_ReturnOnlyGarden1Plants() {
        Plant testPlant1 = new Plant("Rose", "5", "Flower", LocalDate.of(1970, 1, 1));
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", LocalDate.of(1970, 1, 1));
        Plant testPlant3 = new Plant("Tulip", "2", "Flower", LocalDate.of(1970, 1, 1));
        Garden testGarden1 = new Garden("Garden1", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "big", null, null, null);
        Garden testGarden2 = new Garden("Garden2", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "big", null, null, null);
        Long gardenId1 = 1L;
        Long gardenId2 = 2L;

        testPlant1.setGarden(testGarden1);
        testPlant2.setGarden(testGarden1);
        testPlant3.setGarden(testGarden2);

        Mockito.when(plantRepository.findByGardenId(gardenId1)).thenReturn(List.of(testPlant1, testPlant2));
        assertEquals(2, plantService.getPlantsByGardenId(gardenId1).size());
        assertEquals(testPlant1, plantService.getPlantsByGardenId(gardenId1).get(0));
        assertEquals(testPlant2, plantService.getPlantsByGardenId(gardenId1).get(1));
    }

    @Test
    void setPlantImageWithValidId_imageSaved() {
        long id = 1L;

        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = {};
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);
        Plant plant = new Plant();
        plant.setId(id);
        Mockito.when(plantRepository.findById(id)).thenReturn(Optional.of(plant));

        plantService.setPlantImage(id, file);

        verify(plantRepository, times(1)).save(plant);
        assertEquals(contentType, plant.getPlantImageContentType());
        assertEquals(imageBytes, plant.getPlantImage());
    }

    @Test
    void setPlantImageWithNonExistentId_imageNotSaved() {
        long id = 1L;
        byte[] image = {};
        String contentType = "image/svg";
        String name = "plant.png";
        String originalFilename = "plant.png";
        MultipartFile file = new MockMultipartFile(name,originalFilename,contentType,image);

        Mockito.when(plantRepository.findById(id)).thenReturn(Optional.empty());

        plantService.setPlantImage(id, file);
        verify(plantRepository, never()).save(any());
    }

    static Stream<Arguments> provideInvalidFiles() {
        return Stream.of(
                Arguments.of(new MockMultipartFile("file", "invalid.gif", "image/gif", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.html", "text/html", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.txt", "text/plain", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.png", "image/png", new byte[10 * 2024 * 1024]))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFiles")
    void givenImageIsInvalid_WhenValidateImage_ReturnTrue(MockMultipartFile file) {
        boolean result = plantService.validateImage(file);

        Assertions.assertFalse(result);
    }

    static Stream<Arguments> provideValidFiles() {
        return Stream.of(
                Arguments.of(new MockMultipartFile("file", "invalid.png", "image/png", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.svg", "image/svg", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.jpg", "image/jpg", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.jpeg", "image/jpeg", new byte[0])),
                Arguments.of(new MockMultipartFile("file", "invalid.png", "image/png", new byte[10 * 1024 * 1024 - 1]))
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidFiles")
    void givenImageIsValid_WhenValidateImage_ReturnTrue(MockMultipartFile file) {
        boolean result = plantService.validateImage(file);

        Assertions.assertTrue(result);
    }
}
