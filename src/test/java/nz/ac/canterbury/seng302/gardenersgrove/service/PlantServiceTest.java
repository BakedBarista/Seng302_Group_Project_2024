package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(PlantService.class)
class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;
    @Mock
    private GardenRepository gardenRepository;
    @Mock
    private GardenUserRepository gardenUserRepository;

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
        Plant testPlant = new Plant("Rose", "5", "Flower", "01/01/2024");
        Garden testGarden = new Garden("Garden", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "100", "Big");
        Long gardenId = 1L;
        testGarden.setId(gardenId);

        Mockito.when(gardenRepository.findById(Mockito.any())).thenReturn(Optional.of(testGarden));

        Mockito.when(plantRepository.save(Mockito.any(Plant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Plant result = plantService.addPlant(testPlant, gardenId);
        assertEquals(result.getGarden().getId(), gardenId);
    }

    @Test
    void AddPlant_InvalidGardenId_ThrowsException() {
        Plant testPlant = new Plant("Rose", "5", "Flower", "01/01/2024");
        Long gardenId = 1L;

        Mockito.when(gardenRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> plantService.addPlant(testPlant, gardenId));
        verify(plantRepository, Mockito.never()).save(Mockito.any(Plant.class));
    }

    @Test
    void GetAllPlants_MoreThanOnePlantInDB_ReturnsListOfPlants() {
        Plant testPlant1 = new Plant("Rose", "5", "Flower", "01/01/2024");
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", "01/01/2024");

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
        Plant testPlant1 = new Plant("Rose", "5", "Flower", "01/01/2024");
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", "01/01/2024");
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
        Plant testPlant1 = new Plant("Rose", "5", "Flower", "01/01/2024");
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", "01/01/2024");
        Plant testPlant3 = new Plant("Tulip", "2", "Flower", "01/01/2024");
        Garden testGarden1 = new Garden("Garden1", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "100", "Big");
        Garden testGarden2 = new Garden("Garden2", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",1.0,2.0, "100", "Big");
        Long gardenId1 = 1L;
        Long gardenId2 = 2L;

        testPlant1.setGarden(testGarden1);
        testPlant2.setGarden(testGarden1);
        testPlant3.setGarden(testGarden2);

        Mockito.when(plantRepository.findByGardenId(gardenId1)).thenReturn(List.of(testPlant1, testPlant2));
        Mockito.when(plantRepository.findByGardenId(gardenId2)).thenReturn(List.of(testPlant3));
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
}
