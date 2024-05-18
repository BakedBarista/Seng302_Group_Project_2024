package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlantControllerTest {
    @Mock
    private PlantService plantService;

    @Mock
    private MultipartFile file;
    
    @Mock
    private GardenService gardenService;

    @Mock
    private GardenUserService gardenUserService;

    private Model model;

    @InjectMocks
    private PlantController plantController;

    String dateValidStr = "";
    String dateInvalidStr = "dateInvalid";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        GardenUser mockUser = mock(GardenUser.class);
        when(mockUser.getId()).thenReturn(1L);
        when(gardenUserService.getCurrentUser()).thenReturn(mockUser);
        when(gardenService.getGardensByOwnerId(1L)).thenReturn(Collections.emptyList());

        model = mock(Model.class);
    }

    @Test
    void testAddPlantForm_ReturnsToAddPlant() {
        long gardenId = 0;
        String expectedReturnPage = "plants/addPlant";

        String returnPage = plantController.addPlantForm(gardenId, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testSubmitAddPlantForm_DataIsValid_ReturnToGardenDetailPage() throws Exception {
        Plant validPlant = new Plant("Plant", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
//        doNothing().when(plantService).setPlantImage(anyLong(), anyString(), any(byte[].class));
        String returnPage = plantController.submitAddPlantForm(gardenId, validPlant, bindingResult, file, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testSubmitAddPlantForm_DataIsInvalid_ReturnToAddPlantForm() throws Exception {
        Plant invalidPlant = new Plant("#invalid", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        String expectedReturnPage = "plants/addPlant";

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitAddPlantForm(gardenId, invalidPlant, bindingResult, file,  model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testEditPlantForm_ReturnsToEditPlant() {
        Plant plant = new Plant("#invalid", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "plants/editPlant";

        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(plant));

        String returnPage = plantController.editPlantForm(gardenId, plantId, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    public void givenOnSubmitEditPlantForm_whenDataIsValid_thenReturnToEditPlantForm() throws Exception {
        Plant validPlant = new Plant("Plant", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(validPlant));
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, validPlant, bindingResult, model);

        assertEquals(expectedReturnPage, returnPage);

        verify(plantService, times(1)).addPlant(validPlant, gardenId);
    }

    @Test
    public void gvenOnSubmitEditPlantForm_whenCountIsInvalid_thenReturnToEditPlantForm() throws Exception {
        Plant invalidPlant = new Plant("Lotus", "abc", "Yellow", "11/03/2024");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "plants/editPlant";

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, invalidPlant, bindingResult, model);

        assertEquals(expectedReturnPage, returnPage);
    }

    /**
     * plantedDate is only passed to the controller if the date is valid, it is otherwise considered empty
     * So, dateValidation.js flags if the date input is incorrect
     * Testing that the controller throws an error when dateValidation.js picks up an error, but everything else is valid
     */
    @Test
    void testSubmitEditPlantForm_PlantedDateInvalidFromJS_ReturnToEditPlantForm() {
        Plant invalidPlant = new Plant("validName", "1", "Yellow", "");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "plants/editPlant";

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateInvalidStr, invalidPlant, bindingResult, model);

        assertEquals(expectedReturnPage, returnPage);
    }

    /**
     * plantedDate is only passed to the controller if the date is valid, it is otherwise considered empty
     * So, dateValidation.js flags if the date input is incorrect
     * Testing that the controller throws an error when dateValidation.js picks up an error, but everything else is valid
     */
    @Test
    void testSubmitEditPlantForm_PlantedDateValidFromJS_ReturnToGardenDetailPage_PlantAddedToRepository() {
        Plant validPlant = new Plant("validName", "1", "Yellow", "");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(validPlant));
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, validPlant, bindingResult, model);

        verify(plantService, times(0)).addPlant(invalidPlant, gardenId);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    public void testSubmitEditPlantForm_NameIsInvalid_ReturnToEditPlantForm() throws Exception {
        Plant invalidPlant = new Plant("#invalid", "abc", "Yellow", "11/03/2024");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "plants/editPlant";

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, invalidPlant, bindingResult, model);

        verify(plantService, times(0)).addPlant(invalidPlant, gardenId);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testSubmitPlantForm_NoImageUploaded_PlantImageIsSetToNull() {
        long gardenId = 1L;
        byte[] image = {};
        String contentType = "image/png";
        String name = "plant.png";
        String originalFilename = "plant.png";

        MultipartFile file = new MockMultipartFile(name,originalFilename,contentType,image);
        Plant plant = new Plant();
        BindingResult bindingResult = mock(BindingResult.class);

        plant.setPlantedDate("10/10/2024");
        when(bindingResult.hasErrors()).thenReturn(false);
        plantController.submitAddPlantForm(gardenId, plant, bindingResult, file, model);

        assertNull(plant.getPlantImage());
        assertNull(plant.getPlantImageContentType());
    }

    @Test
    void whenPlantImageExists_returnPlantImage() {
        String imagePath = "static/img/plant.png";
        try (InputStream inputStream = PlantControllerTest.class.getClassLoader().getResourceAsStream(imagePath)) {
            if (inputStream == null) {
                throw new IOException("Image not found: " + imagePath);
            }
            byte[] image = inputStream.readAllBytes();
            String contentType = "image/png";
            Plant plant = new Plant();
            plant.setPlantImage(contentType,image);
            when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));

            ResponseEntity<byte[]> response = plantController.plantImage(1L);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(contentType, response.getHeaders().getContentType().toString());
            assertEquals(image, response.getBody());

        } catch (IOException e) {
            System.out.println("Error with resource file " + e.getMessage());
        }
    }

    @Test
    void whenPlantImageNotExist_returnDefaultImage() {
        Plant plant = new Plant();
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        ResponseEntity<byte[]> response = plantController.plantImage(1L);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/img/default-plant.svg", response.getHeaders().getFirst(HttpHeaders.LOCATION));
    }

    @Test
    void whenImageUploaded_thenRedirectToReferer() throws Exception {
        Plant plant = new Plant();
        String referer = "/gardens/1";
        byte[] image = {};
        String contentType = "image/png";
        String name = "plant.png";
        String originalFilename = "plant.png";
        MultipartFile file = new MockMultipartFile(name,originalFilename,contentType,image);
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        doThrow(new RuntimeException("Image processing error"))
                .when(plantService).setPlantImage(anyLong(), anyString(), any(byte[].class));

        String response = plantController.uploadPlantImage(file, 1L, referer);

        assertEquals("redirect:" + referer, response);
    }
    @Test
    void testSubmitAddPlantFormWithImage() throws Exception {
        Long gardenId = 1L;
        Plant plant = new Plant();
        plant.setId(1L);
        plant.setPlantedDate("2023-05-14");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.addPlant(any(Plant.class), eq(gardenId))).thenReturn(plant);
        when(file.isEmpty()).thenReturn(false);
        doThrow(new RuntimeException("Image processing error"))
                .when(plantService).setPlantImage(anyLong(), anyString(), any(byte[].class));

        String view = plantController.submitAddPlantForm(gardenId, plant, bindingResult, file, model);

        assertEquals("redirect:/gardens/" + gardenId, view);
    }

    @Test
    void testSubmitEditPlantFormWithImage() throws Exception {
        Long gardenId = 1L;
        Long plantId = 1L;
        Plant plant = new Plant("Plant", "10", "Yellow", "11/03/2024");
        BindingResult bindingResult = mock(BindingResult.class);
        Optional<Plant> existingPlant = Optional.of(new Plant());
        existingPlant.get().setId(plantId);
        existingPlant.get().setName("Tomato");
        existingPlant.get().setCount("3");
        existingPlant.get().setDescription("tomato plant");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(existingPlant);
        when(file.isEmpty()).thenReturn(false);
        doThrow(new RuntimeException("Image processing error"))
                .when(plantService).setPlantImage(anyLong(), anyString(), any(byte[].class));

        String view = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, plant, bindingResult, model);

        assertEquals("redirect:/gardens/" + gardenId, view);
    }
}
