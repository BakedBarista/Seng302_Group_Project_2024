package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

@DataJpaTest
@Import(PlantService.class)
public class PlantServiceTest {

    @Autowired
    private PlantService plantService;

    @Autowired
    private PlantRepository plantRepository;

    private Plant testPlant;

    @BeforeEach
    void setup() {
        testPlant = new Plant("test", "1", "test", LocalDate.of(1997, 1, 1));
        testPlant.setId(1L);
        plantRepository.save(testPlant);
    }

    @Test
    void givenIHaveAPlant_whenISaveAValidImageForThatPlant_thenThatPlantHasAnImage() {
        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/png";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);

        plantService.setPlantImage(testPlant.getId(), file);

        Plant savedPlant = plantRepository.findById(testPlant.getId()).get();
        Assertions.assertEquals(imageBytes, savedPlant.getPlantImage());
    }

    @Test
    void givenIHaveAPlant_whenISaveAnInvalidImageForThatPlant_thenThatPlantHasAnImage() {
        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/gif";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);

        plantService.setPlantImage(testPlant.getId(), file);

        Plant savedPlant = plantRepository.findById(testPlant.getId()).get();
        Assertions.assertNull(savedPlant.getPlantImage());
    }
}
