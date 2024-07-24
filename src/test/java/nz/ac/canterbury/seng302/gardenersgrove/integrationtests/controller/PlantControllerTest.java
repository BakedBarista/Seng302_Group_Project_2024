package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;

@SpringBootTest
public class PlantControllerTest {

    @Autowired
    private GardenUserRepository userRepository;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private PlantController plantController;

    private Garden testGarden;

    private Plant testPlant;

    @BeforeEach
    void setup() {
        testGarden = new Garden("test", "1", "test", "test", "test",
                "test", "1234", 10D, 10D, "test", 10D);
        GardenUser owner = new GardenUser("john", "doe", "johndoe@gmail.com", "password", LocalDate.of(1997, 1, 1));
        userRepository.save(owner);

        testGarden.setOwner(owner);
        testGarden.setId(1L);
        gardenRepository.save(testGarden);

        testPlant = new Plant("test", "1", "test", LocalDate.of(1997, 1, 1));
        testPlant.setId(1L);
        plantRepository.save(testPlant);
    }

    @Test
    void givenIHaveAPlant_whenIUploadAValidImageWithUploadPlantImage_thenTheImageIsSaved() {
        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/png";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);

        plantController.uploadPlantImage(file, testPlant.getId(), "");

        Plant savedPlant = plantRepository.findById(testPlant.getId()).get();
        Assertions.assertArrayEquals(imageBytes, savedPlant.getPlantImage());
    }

    @Test
    void givenIHaveAPlant_whenIUploadAnInvalidImageWithUploadPlantImage_thenTheImageIsNotSaved() {
        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/gif";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);

        plantController.uploadPlantImage(file, testPlant.getId(), "");

        Plant savedPlant = plantRepository.findById(testPlant.getId()).get();
        Assertions.assertNull(savedPlant.getPlantImage());
    }

    @Test
    void givenIHaveAPlant_whenIUploadAValidImageWithSubmitAddPlantForm_thenTheImageIsSaved() {
        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/png";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);
        PlantDTO testPlantDTO = new PlantDTO("test", "1", "test", "2003-01-01");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Model model = Mockito.mock(Model.class);

        plantController.submitAddPlantForm(testGarden.getId(), testPlantDTO, bindingResult, file, "", model);

        Plant savedPlant = testGarden.getPlants().get(0);
        Assertions.assertArrayEquals(imageBytes, savedPlant.getPlantImage());
    }

    @Test
    void givenIHaveAPlant_whenIUploadAnInvalidImageWithSubmitAddPlantForm_thenTheImageIsNotSaved() {

    }

    @Test
    void givenIHaveAPlant_whenIUploadAValidImageWithSubmitEditPlantForm_thenTheImageIsSaved() {

    }

    @Test
    void givenIHaveAPlant_whenIUploadAnInvalidImageWithSubmitEditPlantForm_thenTheImageIsNotSaved() {

    }
}
