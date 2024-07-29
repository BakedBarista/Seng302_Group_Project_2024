package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

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

    private int test = 0;

    @BeforeEach
    void setup() {
        test++;
        testGarden = new Garden("test", "1", "test", "test", "test",
                "test", "1234", 10D, 10D, "test", 10D);
        GardenUser owner = userRepository.findAll().get(0);
        userRepository.save(owner);

        testGarden.setOwner(owner);
        gardenRepository.save(testGarden);

        testPlant = new Plant("test", "1", "test", LocalDate.of(1997, 1, 1));
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
        String originalFilename = "test.gif";
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

        List<Plant> savedPlants = plantRepository.findAll();
        Plant savedPlant = savedPlants.get(savedPlants.size() - 1);
        Assertions.assertArrayEquals(imageBytes, savedPlant.getPlantImage());
    }

    @Test
    void givenIHaveAPlant_whenIUploadAnInvalidImageWithSubmitAddPlantForm_thenTheImageIsNotSaved() {
        String filename = "test";
        String originalFilename = "test.gif";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/gif";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);
        PlantDTO testPlantDTO = new PlantDTO("test", "1", "test", "2003-01-01");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Model model = Mockito.mock(Model.class);

        plantController.submitAddPlantForm(testGarden.getId(), testPlantDTO, bindingResult, file, "", model);

        List<Plant> savedPlants = plantRepository.findAll();
        Plant savedPlant = savedPlants.get(savedPlants.size() - 1);
        Assertions.assertNull(savedPlant.getPlantImage());
    }

    @Test
    void givenIHaveAPlant_whenIUploadAValidImageWithSubmitEditPlantForm_thenTheImageIsSaved() {
        String filename = "test";
        String originalFilename = "test.png";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/png";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);
        PlantDTO testPlantDTO = new PlantDTO("test", "1", "test", "2003-01-01");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Model model = Mockito.mock(Model.class);

        plantController.submitEditPlantForm(testGarden.getId(), testPlant.getId(), file, "", testPlantDTO, bindingResult, model);

        List<Plant> savedPlants = plantRepository.findAll();
        Plant savedPlant = savedPlants.get(savedPlants.size() - 1);
        Assertions.assertArrayEquals(imageBytes, savedPlant.getPlantImage());
    }

    @Test
    void givenIHaveAPlant_whenIUploadAnInvalidImageWithSubmitEditPlantForm_thenTheImageIsNotSaved() {
        String filename = "test";
        String originalFilename = "test.gif";
        byte[] imageBytes = "test".getBytes();
        String contentType = "image/gif";
        MockMultipartFile file = new MockMultipartFile(filename, originalFilename, contentType, imageBytes);
        PlantDTO testPlantDTO = new PlantDTO("test", "1", "test", "2003-01-01");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Model model = Mockito.mock(Model.class);

        plantController.submitEditPlantForm(testGarden.getId(), testPlant.getId(), file, "", testPlantDTO, bindingResult, model);

        List<Plant> savedPlants = plantRepository.findAll();
        Plant savedPlant = savedPlants.get(savedPlants.size() - 1);
        Assertions.assertNull(savedPlant.getPlantImage());
    }
}
