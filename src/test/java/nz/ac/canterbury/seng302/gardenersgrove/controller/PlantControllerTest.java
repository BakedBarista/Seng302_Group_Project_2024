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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    String dateInvalidStr = "Date Invalid";

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
    void testSubmitAddPlantForm_DataIsValid_ReturnToGardenDetailPage() {
        Plant validPlant = new Plant("Plant", "10", "Yellow", "11/03/2024");
        long gardenId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        String returnPage = plantController.submitAddPlantForm(gardenId, validPlant, bindingResult, file, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testSubmitAddPlantForm_DataIsInvalid_ReturnToAddPlantForm() {
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
    void testSubmitEditPlantForm_DataIsValid_ReturnToGardenDetailPage_PlantAddedToRepository() {
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
    void testSubmitEditPlantForm_DataIsInvalid_ReturnToEditPlantForm() {
        Plant invalidPlant = new Plant("#invalid", "10", "Yellow", "11/03/2024");
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

        assertEquals(expectedReturnPage, returnPage);
    }
}
