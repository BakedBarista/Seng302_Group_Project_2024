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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        Garden testGarden = new Garden("Garden", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "Big", null);
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
        Garden testGarden1 = new Garden("Garden1", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "big", null);
        Garden testGarden2 = new Garden("Garden2", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "big", null);
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
    public void setPlantImageWithValidId_imageSaved() {
        long id = 1L;
        byte[] imageBytes = {};
        String contentType = "image/png";
        Plant plant = new Plant();
        plant.setId(id);
        when(plantRepository.findById(id)).thenReturn(Optional.of(plant));
        plantService.setPlantImage(id, contentType, imageBytes);
        verify(plantRepository, times(1)).save(plant);
        assertEquals(contentType, plant.getPlantImageContentType());
        assertEquals(imageBytes, plant.getPlantImage());
    }

    @Test
    public void setPlantImageWithNonExistentId_imageNotSaved() {
        long id = 1L;
        when(plantRepository.findById(id)).thenReturn(Optional.empty());
        plantService.setPlantImage(id, "image/png", new byte[]{});
        verify(plantRepository, never()).save(any());
    }

    @Test
    void whenCreatePlant_thenTracksChanges() {
        PlantDTO plantDTO = new PlantDTO("Rose", "5", "Flower", "1970-01-01");
        Garden garden = new Garden("Garden", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "Big", null);
        Long gardenId = 1L;
        garden.setId(gardenId);

        when(gardenRepository.findById(gardenId)).thenReturn(Optional.of(garden));
        when(plantRepository.save(any(Plant.class))).thenAnswer((invocation) -> invocation.getArgument(0));

        Plant plant = plantService.createPlant(plantDTO, gardenId);

        verify(plantHistoryRepository).save(assertArg(historyItem -> {
            assertEquals(plant, historyItem.getPlant());
            assertEquals("Rose", historyItem.getName());
            assertEquals("5", historyItem.getCount());
            assertEquals("Flower", historyItem.getDescription());
            assertNull(historyItem.getPlantImage());
        }));
    }

    @Test
    void whenUpdatePlant_andNoFieldsChanged_thenNoChangesTracked() {
        Plant plant = new Plant("Rose", "5", "Flower", LocalDate.of(1970, 1, 1));
        Long plantId = 1L;
        plant.setId(plantId);
        PlantDTO plantDTO = new PlantDTO("Rose", "5", "Flower", "1970-01-01");

        plantService.updatePlant(plant, plantDTO);

        verify(plantHistoryRepository, times(0)).save(any());
    }

    @Test
    void whenUpdatePlant_andSomeFieldsChanged_thenTracksChanges() {
        Plant plant = new Plant("Rose", "5", "Flower", LocalDate.of(1970, 1, 1));
        Long plantId = 1L;
        plant.setId(plantId);
        PlantDTO plantDTO = new PlantDTO("Daisy", "3", "Flower", "1970-01-01");

        plantService.updatePlant(plant, plantDTO);

        verify(plantHistoryRepository).save(assertArg(historyItem -> {
            assertEquals(plant, historyItem.getPlant());
            assertEquals("Daisy", historyItem.getName());
            assertEquals("3", historyItem.getCount());
            assertNull(historyItem.getDescription());
            assertNull(historyItem.getPlantedDate());
            assertNull(historyItem.getPlantImage());
        }));
    }

    @Test
    void whenUpdatePlant_andAllFieldsChanged_thenTracksChanges() {
        Plant plant = new Plant("Rose", "5", "Flower", LocalDate.of(1970, 1, 1));
        Long plantId = 1L;
        plant.setId(plantId);
        PlantDTO plantDTO = new PlantDTO("Daisy", "3", "Pretty Flower", "2024-07-24");

        plantService.updatePlant(plant, plantDTO);

        verify(plantHistoryRepository).save(assertArg(historyItem -> {
            assertEquals(plant, historyItem.getPlant());
            assertEquals("Daisy", historyItem.getName());
            assertEquals("3", historyItem.getCount());
            assertEquals("Pretty Flower", historyItem.getDescription());
            assertEquals(LocalDate.of(2024, 07, 24), historyItem.getPlantedDate());
            assertNull(historyItem.getPlantImage());
        }));
    }

    @Test
    void whenUpdatePlantImage_thenTracksChanges() {
        Plant plant = new Plant("Rose", "5", "Flower", LocalDate.of(1970, 1, 1));
        long plantId = 1;
        plant.setId(plantId);
        String contentType = "image/png";
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};

        when(plantRepository.findById(plantId)).thenReturn(Optional.of(plant));

        plantService.setPlantImage(plantId, contentType, imageData);

        verify(plantHistoryRepository).save(assertArg(historyItem -> {
            assertEquals(plant, historyItem.getPlant());
            assertNull(historyItem.getName());
            assertNull(historyItem.getCount());
            assertNull(historyItem.getDescription());
            assertNull(historyItem.getPlantedDate());
            assertEquals(contentType, historyItem.getPlantImageContentType());
            assertEquals(imageData, historyItem.getPlantImage());
        }));
    }
}
