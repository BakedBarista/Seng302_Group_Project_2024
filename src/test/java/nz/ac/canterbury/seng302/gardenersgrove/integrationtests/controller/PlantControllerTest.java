package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHistoryItemDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantHistoryService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @MockBean
    private GardenUserService userService;

    @MockBean
    private PlantHistoryService plantHistoryService;

    private Garden testGarden;

    private Plant testPlant;

    private int test = 0;

    @BeforeEach
    void setup() {
        test++;
        testGarden = new Garden("test", "1", "test", "test", "test",
                "test", "1234", 10D, 10D, "test", 10D);
        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", LocalDate.of(2000, 10, 10));
        user.setId(1L);
        userRepository.save(user);
        testGarden.setOwner(user);
        gardenRepository.save(testGarden);

        testPlant = new Plant("test", "1", "test", LocalDate.of(1997, 1, 1));
        plantRepository.save(testPlant);

        Mockito.when(userService.getCurrentUser()).thenReturn(user);

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

    @Test
    void givenValidUser_thenDisplayPlantHistoryForm() {
        Model model = Mockito.mock(Model.class);
        String result = plantController.addPlantHistoryForm(testGarden.getId(), testPlant.getId(), model);
        Assertions.assertEquals("plants/plantHistory",result);
        Mockito.verify(model).addAttribute("gardenId",testGarden.getId());
        Mockito.verify(model).addAttribute("plantId",testPlant.getId());
        Mockito.verify(model).addAttribute(Mockito.eq("plant"),Mockito.any(Plant.class));
    }

    @Test
    void givenInvalidUser_thenDisplayAccessDenied() {
        Model model = Mockito.mock(Model.class);
        GardenUser notOwner = new GardenUser();
        when(userService.getCurrentUser()).thenReturn(notOwner);
        String result = plantController.addPlantHistoryForm(testGarden.getId(),testPlant.getId(),model);
        Assertions.assertEquals("/error/accessDenied",result);
    }

    @Test
    void givenValidInput_whenSubmitPlantHistoryForm_thenRedirectToGardenPage() throws IOException {
        long gardenId = testGarden.getId();
        long plantId = testPlant.getId();
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
        PlantHistoryItemDTO plantHistoryDTO = new PlantHistoryItemDTO("Test Description");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Model model = Mockito.mock(Model.class);
        Mockito.doNothing().when(plantHistoryService).addHistoryItem(Mockito.any(Plant.class),Mockito.anyString(),Mockito.any(byte[].class),Mockito.anyString());
        String result = plantController.submitPlantHistoryForm(gardenId, plantId, file, plantHistoryDTO.getDescription(), plantHistoryDTO,bindingResult, model);
        Assertions.assertEquals("redirect:/gardens/" + gardenId, result);
    }

    @Test
    void givenInvalidBindingResult_whenSubmitPlantHistoryForm_thenReturnPlantHistoryPage() throws IOException {
        long gardenId = testGarden.getId();
        long plantId = testPlant.getId();
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
        PlantHistoryItemDTO plantHistoryDTO = new PlantHistoryItemDTO("Test Description");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);
        Model model = Mockito.mock(Model.class);
        String result = plantController.submitPlantHistoryForm(gardenId, plantId, file, plantHistoryDTO.getDescription(),plantHistoryDTO, bindingResult, model);
        Assertions.assertEquals("plants/plantHistory", result);
        Mockito.verify(model).addAttribute("description", plantHistoryDTO);
    }

}
