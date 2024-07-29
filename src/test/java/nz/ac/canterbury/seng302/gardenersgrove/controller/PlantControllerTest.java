package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

        Garden mockGarden = new Garden();
        mockGarden.setOwner(mockUser);
        when(gardenService.getGardenById(0L)).thenReturn(Optional.of(mockGarden));

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
    void testAddPlantForm_GardenNotPresent_ReturnsAccessDenied() {
        long gardenId = 0;
        String expectedReturnPage = "/error/accessDenied";

        when(gardenService.getGardenById(gardenId)).thenReturn(Optional.empty());
        String returnPage = plantController.addPlantForm(gardenId, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testAddPlantForm_UserNotOwner_ReturnsAccessDenied() {
        long gardenId = 0;
        String expectedReturnPage = "/error/accessDenied";

        GardenUser owner = new GardenUser();
        owner.setId(1L);

        Garden garden = new Garden("Test Garden", "1", "test", "test suburb", "test city", "test country", "1234", 0.0, 0.0, "test description", 100D);
        garden.setOwner(owner);

        when(gardenService.getGardenById(gardenId) ).thenReturn(Optional.of(garden));
        when(gardenUserService.getCurrentUser()).thenReturn(new GardenUser());

        String returnPage = plantController.addPlantForm(gardenId, model);
        assertEquals(expectedReturnPage, returnPage);

    }


    @Test
    void testSubmitAddPlantForm_DataIsValid_ReturnToGardenDetailPage() throws Exception {
        PlantDTO validPlantDTO = new PlantDTO("Plant", "10", "Yellow", "2024-11-03");
        long gardenId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        String returnPage = plantController.submitAddPlantForm(gardenId, validPlantDTO, bindingResult, file, dateValidStr, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testSubmitAddPlantForm_DataIsInvalid_ReturnToAddPlantForm() throws Exception {
        PlantDTO invalidPlantDTO = new PlantDTO("#invalid", "10", "Yellow", "2024-11-03");
        long gardenId = 0;
        String expectedReturnPage = "plants/addPlant";

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitAddPlantForm(gardenId, invalidPlantDTO, bindingResult, file, dateValidStr, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    /**
     * plantedDate is only passed to the controller if the date is valid, it is otherwise considered empty
     * So, dateValidation.js flags if the date input is incorrect
     * Testing that the controller throws an error when dateValidation.js picks up an error, but everything else is valid
     */
    @Test
    void testSubmitAddPlantForm_PlantedDateInvalidFromJS_ReturnToAddPlantForm() {
        PlantDTO invalidPlantDTO = new PlantDTO("validName", "1", "Yellow", "");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "plants/editPlant";

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateInvalidStr, invalidPlantDTO, bindingResult, model);

        assertEquals(expectedReturnPage, returnPage);
    }

    /**
     * plantedDate is only passed to the controller if the date is valid, it is otherwise considered empty
     * So, dateValidation.js flags if the date input is incorrect
     * Testing that the controller throws an error when dateValidation.js picks up an error, but everything else is valid
     */
    @Test
    void testSubmitAddPlantForm_PlantedDateValidFromJS_ReturnToGardenDetailPage_PlantAddedToRepository() {
        PlantDTO validPlantDTO = new PlantDTO("validName", "1", "Yellow", "");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(new Plant(validPlantDTO)));
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, validPlantDTO, bindingResult, model);

        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testEditPlantForm_ReturnsToEditPlant() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate plantDate = LocalDate.parse("11/03/2024", formatter);

        Plant plant = new Plant("#invalid", "10", "Yellow", plantDate);
        long gardenId = 1L;
        long plantId = 1L;
        String expectedReturnPage = "plants/editPlant";

        GardenUser owner = new GardenUser();
        owner.setId(1L);

        Garden garden = new Garden("Test Garden", "1", "test", "test suburb", "test city", "test country", "1234", 0.0, 0.0, "test description", 100D);
        garden.setOwner(owner);

        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(plant));
        when(gardenService.getGardenById(gardenId)).thenReturn(Optional.of(garden));
        when(gardenUserService.getCurrentUser()).thenReturn(owner);
        when(gardenService.getGardensByOwnerId(owner.getId())).thenReturn(Collections.singletonList(garden));

        String returnPage = plantController.editPlantForm(gardenId, plantId, model);
        assertEquals(expectedReturnPage, returnPage);
        verify(model).addAttribute("gardens", Collections.singletonList(garden));
        verify(model).addAttribute("gardenId", gardenId);
        verify(model).addAttribute("plantId", plantId);
        verify(model).addAttribute("plant", plant);
    }

    @Test
    public void givenOnSubmitEditPlantForm_whenDataIsValid_thenReturnToEditPlantForm() throws Exception {
        PlantDTO validPlantDTO = new PlantDTO("Plant", "10", "Yellow", "2024-11-03");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;
        Plant plant = new Plant(validPlantDTO);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(plant));
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, validPlantDTO, bindingResult, model);

        assertEquals(expectedReturnPage, returnPage);

        verify(plantService, times(1)).addPlant(plant, gardenId);
    }

    @Test
    public void gvenOnSubmitEditPlantForm_whenCountIsInvalid_thenReturnToEditPlantForm() throws Exception {
        PlantDTO invalidPlant = new PlantDTO("Lotus", "abc", "Yellow", "11/03/2024");
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
        PlantDTO invalidPlant = new PlantDTO("validName", "1", "Yellow", "");
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
        PlantDTO validPlantDTO = new PlantDTO("validName", "1", "Yellow", "");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId;
        Plant plant = new Plant(validPlantDTO);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(plant));
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, validPlantDTO, bindingResult, model);

        verify(plantService, times(1)).addPlant(plant, gardenId);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    public void testSubmitEditPlantForm_NameIsInvalid_ReturnToEditPlantForm() throws Exception {
        PlantDTO invalidPlantDTO = new PlantDTO("#invalid", "abc", "Yellow", "2024-11-03");
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "plants/editPlant";
        Plant plant = new Plant(invalidPlantDTO);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String returnPage = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, invalidPlantDTO, bindingResult, model);

        verify(plantService, times(0)).addPlant(plant, gardenId);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void whenPlantImageExists_returnPlantImage() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
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

            ResponseEntity<byte[]> response = plantController.plantImage(1L, mockRequest);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(contentType, response.getHeaders().getContentType().toString());
            assertEquals(image, response.getBody());

        } catch (IOException e) {
            System.out.println("Error with resource file " + e.getMessage());
        }
    }

    @Test
    void whenPlantImageNotExist_returnDefaultImage() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        Plant plant = new Plant();
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        ResponseEntity<byte[]> response = plantController.plantImage(1L, mockRequest);
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
        PlantDTO plantDTO = new PlantDTO();
        plantDTO.setPlantedDate("2023-05-14");
        BindingResult bindingResult = mock(BindingResult.class);

        Plant plant = new Plant(plantDTO);
        plant.setId(gardenId);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.addPlant(any(Plant.class), eq(gardenId))).thenReturn(plant);
        when(file.isEmpty()).thenReturn(false);
        doThrow(new RuntimeException("Image processing error"))
                .when(plantService).setPlantImage(anyLong(), anyString(), any(byte[].class));

        String view = plantController.submitAddPlantForm(gardenId, plantDTO, bindingResult, file, dateValidStr, model);

        assertEquals("redirect:/gardens/" + gardenId, view);
    }

    @Test
    void testSubmitEditPlantFormWithImage() throws Exception {
        long gardenId = 1L;
        long plantId = 1L;
        PlantDTO plantDTO = new PlantDTO("Plant", "10", "Yellow", "2024-11-03");
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

        String view = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, plantDTO, bindingResult, model);

        assertEquals("redirect:/gardens/" + gardenId, view);
    }


}
