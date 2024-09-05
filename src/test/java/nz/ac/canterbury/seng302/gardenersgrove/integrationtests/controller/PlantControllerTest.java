package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHistoryItemDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantHistoryRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantHistoryService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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


    @BeforeEach
    void setup() {
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
        Assertions.assertEquals("error/accessDenied",result);
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
        Assertions.assertEquals("redirect:/gardens/" + gardenId + "/plants/" + plantId, result);
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


    @Test
    void givenInvalidUser_whenAccessUnauthorizedPlant_thenAccessDenied() {
        Model model = Mockito.mock(Model.class);
        GardenUser notOwner = new GardenUser();
        when(userService.getCurrentUser()).thenReturn(notOwner);
        String result = plantController.getPlantTimeline(testGarden.getId(),testPlant.getId(),model);
        Assertions.assertEquals("error/accessDenied",result);
    }

    @Test
    void givenPlantExists_whenAccessingPlantHistoryPage_thenRedirectToPlantDetailsPage() {
        Model model = Mockito.mock(Model.class);
        long gardenId = testGarden.getId();
        long plantId = testPlant.getId();

        String result = plantController.getPlantTimeline(gardenId, plantId, model);

        Assertions.assertEquals("plants/plantDetails", result);

    }

    @Test
    void whenPlantHistoryImageExists_returnPlantHistoryImage() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        String imagePath = "static/img/plant.png";
        try (InputStream inputStream = nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller.PlantControllerTest.class.getClassLoader().getResourceAsStream(imagePath)) {
            if (inputStream == null) {
                throw new IOException("Image not found: " + imagePath);
            }
            byte[] image = inputStream.readAllBytes();
            String contentType = "image/png";
            Plant plant = new Plant();

            LocalDate date = LocalDate.of(1970, 1, 1);
            PlantHistoryItem plantHistoryItem = new PlantHistoryItem(plant, date);
            plantHistoryItem.setImage(contentType, image);
            plant.setPlantImage(contentType,image);

            when(plantHistoryService.getPlantHistoryById(1L)).thenReturn(Optional.of(plantHistoryItem));

            ResponseEntity<byte[]> response = plantController.historyImage(1L, 1L, mockRequest);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(contentType, response.getHeaders().getContentType().toString());
            assertEquals(image, response.getBody());

        } catch (IOException e) {
            System.out.println("Error with resource file " + e.getMessage());
        }
    }


    @Test
    void whenHistoryDoesNotExist_returnDefaultImage() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        String imagePath = "static/img/plant.png";

        try (InputStream inputStream = nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller.PlantControllerTest.class.getClassLoader().getResourceAsStream(imagePath)) {
            if (inputStream == null) {
                throw new IOException("Image not found: " + imagePath);
            }
            byte[] image = inputStream.readAllBytes();
            String contentType = "image/png";
            Plant plant = new Plant();

            LocalDate date = LocalDate.of(1970, 1, 1);
            PlantHistoryItem plantHistoryItem = new PlantHistoryItem(plant, date);
            plantHistoryItem.setImage(contentType, image);
            plant.setPlantImage(contentType,image);

            when(plantHistoryService.getPlantHistoryById(1L)).thenReturn(Optional.of(plantHistoryItem));

            ResponseEntity<byte[]> response = plantController.historyImage(1L, plantHistoryItem.getId(), mockRequest);
            assertEquals(HttpStatus.FOUND, response.getStatusCode());
            assertEquals("/img/default-plant.svg", response.getHeaders().getFirst(HttpHeaders.LOCATION));
        } catch (IOException e) {
            System.out.println("Error with resource file " + e.getMessage());
        }


    }

}
