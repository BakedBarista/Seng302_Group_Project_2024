package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(PlantService.class)
class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;
    @Mock
    private GardenRepository gardenRepository;
    @InjectMocks
    private PlantService plantService;

    @Test
    void AddPlant_ValidPlantWithGardenId_ReturnsPlantWithCorrectGardenId() {
        Plant testPlant = new Plant("Rose", "5", "Flower", "01/01/2024");
        double lat = 1;
        double lon = 1;
        Garden testGarden = new Garden("Garden", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041",lat, lon, "100", "Big");
        Long gardenId = 1L;
        testGarden.setId(gardenId);

        Mockito.when(gardenRepository.findById(Mockito.any())).thenReturn(Optional.of(testGarden));

        Mockito.when(plantRepository.save(Mockito.any(Plant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Plant result = plantService.addPlant(testPlant, gardenId);
        Assertions.assertEquals(result.getGarden().getId(), gardenId);
    }

    @Test
    void AddPlant_InvalidGardenId_ThrowsException() {
        Plant testPlant = new Plant("Rose", "5", "Flower", "01/01/2024");
        Long gardenId = 1L;

        Mockito.when(gardenRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> plantService.addPlant(testPlant, gardenId));
        Mockito.verify(plantRepository, Mockito.never()).save(Mockito.any(Plant.class));
    }

    @Test
    void GetAllPlants_MoreThanOnePlantInDB_ReturnsListOfPlants() {
        Plant testPlant1 = new Plant("Rose", "5", "Flower", "01/01/2024");
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", "01/01/2024");

        Mockito.when(plantRepository.findAll()).thenReturn(List.of(testPlant1, testPlant2));
        Assertions.assertEquals(2, plantService.getAllPlants().size());
        Assertions.assertEquals(testPlant1, plantService.getAllPlants().get(0));
        Assertions.assertEquals(testPlant2, plantService.getAllPlants().get(1));
    }

    @Test
    void GetAllPlants_NoPlantsInDB_ReturnsEmptyList() {
        Mockito.when(plantRepository.findAll()).thenReturn(List.of());
        Assertions.assertEquals(0, plantService.getAllPlants().size());
    }

    @Test
    void GetPlantsByGardenId_TwoPlantsOneGarden_ReturnBothPlants() {
        Plant testPlant1 = new Plant("Rose", "5", "Flower", "01/01/2024");
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", "01/01/2024");
        Long gardenId = 1L;

        Mockito.when(plantRepository.findByGardenId(gardenId)).thenReturn(List.of(testPlant1, testPlant2));
        Assertions.assertEquals(2, plantService.getPlantsByGardenId(gardenId).size());
        Assertions.assertEquals(testPlant1, plantService.getPlantsByGardenId(gardenId).get(0));
        Assertions.assertEquals(testPlant2, plantService.getPlantsByGardenId(gardenId).get(1));
    }

    @Test
    void GetPlantsByGardenId_NoPlantsInGarden_ReturnsEmptyList() {
        Long gardenId = 1L;
        Mockito.when(plantRepository.findByGardenId(gardenId)).thenReturn(List.of());
        Assertions.assertEquals(0, plantService.getPlantsByGardenId(gardenId).size());
    }

    @Test
    void GetPlantsByGardenId_InvalidGardenId_ReturnsEmptyList() {
        Long gardenId = 1L;
        Mockito.when(plantRepository.findByGardenId(gardenId)).thenReturn(List.of());
        Assertions.assertEquals(0, plantService.getPlantsByGardenId(gardenId).size());
    }

    @Test
    void GetPlantsByGardenId_ThreePlantsTwoInGarden1OneInGardenTwo_ReturnOnlyGarden1Plants() {
        double lat = 1;
        double lon = 1;
        Plant testPlant1 = new Plant("Rose", "5", "Flower", "01/01/2024");
        Plant testPlant2 = new Plant("Daisy", "3", "Flower", "01/01/2024");
        Plant testPlant3 = new Plant("Tulip", "2", "Flower", "01/01/2024");
        Garden testGarden1 = new Garden("Garden1", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041", lat, lon,"100", "Big");
        Garden testGarden2 = new Garden("Garden2", "1","Ilam Road","Ilam","Christchurch","New Zealand","8041", lat, lon,"100", "Big");
        Long gardenId1 = 1L;
        Long gardenId2 = 2L;

        testPlant1.setGarden(testGarden1);
        testPlant2.setGarden(testGarden1);
        testPlant3.setGarden(testGarden2);

        Mockito.when(plantRepository.findByGardenId(gardenId1)).thenReturn(List.of(testPlant1, testPlant2));
        Mockito.when(plantRepository.findByGardenId(gardenId2)).thenReturn(List.of(testPlant3));
        Assertions.assertEquals(2, plantService.getPlantsByGardenId(gardenId1).size());
        Assertions.assertEquals(testPlant1, plantService.getPlantsByGardenId(gardenId1).get(0));
        Assertions.assertEquals(testPlant2, plantService.getPlantsByGardenId(gardenId1).get(1));
    }

//    @Test
//    void GetPlantById_ValidPlantId_ReturnsPlant() {
//        Plant testPlant = new Plant("Rose", 5, "Flower", "01/01/2024");
//        Long plantId = 1L;
//        testPlant.setId(plantId);
//
//        Mockito.when(plantRepository.findById(Mockito.any())).thenReturn(Optional.of(testPlant));
//
//        Optional<Plant> returnedPlant = plantService.getPlantById(plantId);
//
//        Assertions.assertEquals(testPlant, returnedPlant);
//    }
}
