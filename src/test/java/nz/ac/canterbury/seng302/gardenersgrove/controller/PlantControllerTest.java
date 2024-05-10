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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void testAddPlantForm_ReturnsToAddPlant() {
        long gardenId = 0;
        String expectedReturnPage = "plants/addPlant";

        String returnPage = plantController.addPlantForm(gardenId, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    public void testSubmitAddPlantForm_DataIsValid_ReturnToGardenDetailPage() throws Exception {
        Plant validPlant = new Plant("Plant", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        String returnPage = plantController.submitAddPlantForm(gardenId, validPlant, bindingResult, file, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    public void testSubmitAddPlantForm_DataIsInvalid_ReturnToAddPlantForm() throws Exception {
        Plant invalidPlant = new Plant("#invalid", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        String expectedReturnPage = "plants/addPlant";

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitAddPlantForm(gardenId, invalidPlant, bindingResult, file,  model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    public void testEditPlantForm_ReturnsToEditPlant() {
        Plant plant = new Plant("#invalid", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "plants/editPlant";

        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(plant));

        String returnPage = plantController.editPlantForm(gardenId, plantId, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    public void testSubmitEditPlantForm_DataIsValid_ReturnToGardenDetailPage_PlantAddedToRepository() throws Exception {
        Plant validPlant = new Plant("Plant", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(validPlant));
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, validPlant, bindingResult, model);

        assertEquals(expectedReturnPage, returnPage);

        verify(plantService, times(1)).addPlant(validPlant, gardenId);
    }

    @Test
    public void testSubmitEditPlantForm_DataIsInvalid_ReturnToEditPlantForm() throws Exception {
        Plant invalidPlant = new Plant("#invalid", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "plants/editPlant";

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, invalidPlant, bindingResult, model);

        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void whenPlantImageExists_returnPlantImage() {
        byte[] image = {};
        String contentType = "image/jpg";
        Plant plant = new Plant();
        plant.setPlantImage(contentType,image);
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));

        ResponseEntity<byte[]> response = plantController.plantImage(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contentType, response.getHeaders().getContentType().toString());
        assertEquals(image, response.getBody());
    }

    @Test
    void whenPlantImageNotExist_returnDefaultImage() {
        Plant plant = new Plant();
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        ResponseEntity<byte[]> response = plantController.plantImage(1L);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/img/plant.png", response.getHeaders().getFirst(HttpHeaders.LOCATION));
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
        String response = plantController.uploadPlantImage(file,1L, referer);
        assertEquals("redirect:/gardens/1", response);
    }
}
