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

import java.util.Optional;

@DataJpaTest
@Import(FormService.class)
class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;
    @Mock
    private GardenRepository gardenRepository;
    @InjectMocks
    private PlantService plantService;

    @Test
    void AddPlant_ValidPlantWithGardenId_ReturnsPlantWithCorrectGardenId() {
        Long gardenId = 1L;

        Plant testPlant = new Plant("Rose", 5, "Flower", "01/01/2024");
        Garden testGarden = new Garden("Test Garden", "Test Location", "5");
        Mockito.when(gardenRepository.findById(Mockito.any())).thenReturn(Optional.of(testGarden));

//        Mockito.when(plantRepository.save(Mockito.any())).thenReturn(new Plant(name, count, description, plantedDate));

        Plant plant = plantService.addPlant(testPlant, gardenId);

        System.out.println(plant.getGarden());
        System.out.println(gardenId);
        Assertions.assertEquals(plant.getGarden(), gardenId);
    }
}
