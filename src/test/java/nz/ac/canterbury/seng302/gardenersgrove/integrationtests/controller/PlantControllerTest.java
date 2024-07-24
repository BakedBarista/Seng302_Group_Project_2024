package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

@SpringBootTest
public class PlantControllerTest {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private PlantController plantController;

    private Plant testPlant;

    @BeforeEach
    void setup() {
        testPlant = new Plant("test", "1", "test", LocalDate.of(1997, 1, 1));
        testPlant.setId(1L);
        plantRepository.save(testPlant);
    }

    @Test
    void givenIHaveAPlant_whenIUploadAValidImage_thenTheImageIsSaved() {
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
    void givenIHaveAPlant_whenIUploadAnInvalidImage_thenTheImageIsNotSaved() {
        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/gif";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);

        plantController.uploadPlantImage(file, testPlant.getId(), "");

        Plant savedPlant = plantRepository.findById(testPlant.getId()).get();
        Assertions.assertNull(savedPlant.getPlantImage());
    }
}
