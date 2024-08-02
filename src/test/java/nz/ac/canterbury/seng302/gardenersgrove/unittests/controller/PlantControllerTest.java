package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHistoryItemDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantHistoryService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PlantControllerTest {
    @Mock
    private PlantService plantService;

    @Mock
    private PlantHistoryService plantHistoryService;

    @Mock
    private MultipartFile file;

    @Mock
    private MultipartFile fileFilled;
    
    @Mock
    private GardenService gardenService;

    @Mock
    private GardenUserService gardenUserService;

    private Model model;

    @InjectMocks
    private PlantController plantController;

    String dateValidStr = "";
    String dateInvalidStr = "dateInvalid";
    private static Authentication authentication;

    // For the garden timeline tests
    private Garden mockGardenTimeline;
    private Plant mockPlantTimeline;
    private GardenUser ownerTimeline;
    private GardenUser currentUserTimeline;
    private long ownerIdTimeline = 1L;
    private long currentUserIdTimeline = 1L;
    private long gardenIdTimeline = 1L;
    private long plantIdTimeline = 1L;

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
        fileFilled = new MockMultipartFile("image", "testImage.jpg", "image/jpeg", "test image content".getBytes());
        model = mock(Model.class);

        authentication = mock(Authentication.class);
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
        String expectedReturnPage = "error/accessDenied";

        when(gardenService.getGardenById(gardenId)).thenReturn(Optional.empty());
        String returnPage = plantController.addPlantForm(gardenId, model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testAddPlantForm_UserNotOwner_ReturnsAccessDenied() {
        long gardenId = 0;
        String expectedReturnPage = "error/accessDenied";

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

        verify(plantService, times(1)).updatePlant(plant, validPlantDTO);
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

        verify(plantService, times(1)).updatePlant(plant, validPlantDTO);
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

        verify(plantService, times(0)).updatePlant(eq(plant), any());
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
    void whenImageUploaded_thenRedirectToReferer() {
        Plant plant = new Plant();
        String referer = "/gardens/1";
        byte[] image = {};
        String contentType = "image/png";
        String name = "plant.png";
        String originalFilename = "plant.png";
        MultipartFile file = new MockMultipartFile(name,originalFilename,contentType,image);
        when(plantService.getPlantById(1L)).thenReturn(Optional.of(plant));
        doThrow(new RuntimeException("Image processing error"))
                .when(plantService).setPlantImage(anyLong(), any(MultipartFile.class));

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
        when(plantService.createPlant(any(PlantDTO.class), eq(gardenId))).thenReturn(plant);
        when(file.isEmpty()).thenReturn(false);
        doThrow(new RuntimeException("Image processing error"))
                .when(plantService).setPlantImage(anyLong(), any(MultipartFile.class));

        String view = plantController.submitAddPlantForm(gardenId, plantDTO, bindingResult, file, dateValidStr, model);

        assertEquals("redirect:/gardens/" + gardenId, view);
    }

    @Test
    void testSubmitEditPlantFormWithImage() {
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
                .when(plantService).setPlantImage(anyLong(), any(MultipartFile.class));

        String view = plantController.submitEditPlantForm(gardenId, plantId, file, dateValidStr, plantDTO, bindingResult, model);

        assertEquals("redirect:/gardens/" + gardenId, view);
    }

    @Test
    void testSubmitAddPlantHistoryFormNoImage_ReturnToGardenDetailPage_PlantHistoryAddedToRepository() {
        PlantHistoryItemDTO validHistoryPlantDTO = new PlantHistoryItemDTO("validDescription");
        Plant plant = new Plant();
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId + "/plants/" + plantId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(plant));
        try {
            String returnPage = plantController.submitPlantHistoryForm(gardenId, plantId, file, dateValidStr, validHistoryPlantDTO, bindingResult, model);
            assertEquals(expectedReturnPage, returnPage);
            verify(plantHistoryService, times(1)).addHistoryItem(plant, null, null, "");
        } catch (IOException e) {
            fail("IOException occurred during test: " + e.getMessage());
        }
    }

    @Test
    void testSubmitAddPlantHistoryFormWithImage_ReturnToGardenDetailPage_PlantHistoryAddedToRepository() {
        PlantHistoryItemDTO validHistoryPlantDTO = new PlantHistoryItemDTO("validDescription");
        Plant plant = new Plant();
        long gardenId = 0;
        long plantId = 0;
        String expectedReturnPage = "redirect:/gardens/" + gardenId + "/plants/" + plantId;

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(plantService.getPlantById(plantId)).thenReturn(Optional.of(plant));
        try {
            String returnPage = plantController.submitPlantHistoryForm(gardenId, plantId, fileFilled, "validDescription", validHistoryPlantDTO, bindingResult, model);
            assertEquals(expectedReturnPage, returnPage);

            verify(plantHistoryService).addHistoryItem(eq(plant), eq("image/jpeg"), any(byte[].class), eq("validDescription"));
        } catch (IOException e) {
            fail("IOException occurred during test: " + e.getMessage());
        }
    }

    @Test
    void whenDateTooOld_ReturnError() {
        PlantDTO plantDTO = new PlantDTO("Plant", "10", "Yellow", "1799-11-03");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String returnPage = plantController.submitAddPlantForm(1L, plantDTO, bindingResult, file, dateValidStr, model);
        verify(bindingResult).hasErrors();

        assertEquals("plants/addPlant", returnPage);
    }

    @Test
    void whenDateInFuture_ReturnError() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedTomorrow = tomorrow.format(formatter);

        PlantDTO plantDTO = new PlantDTO("Plant", "10", "Yellow", formattedTomorrow);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String returnPage = plantController.submitAddPlantForm(1L, plantDTO, bindingResult, file, dateValidStr, model);
        verify(bindingResult).hasErrors();
        assertEquals("plants/addPlant", returnPage);

    }

    void setUpForPlantTimelineTests() {
       mockGardenTimeline = mock(Garden.class);
       mockPlantTimeline = mock(Plant.class);
       ownerTimeline = mock(GardenUser.class);
       currentUserTimeline = mock(GardenUser.class);

       // Define the behavior of the mock objects
       when(ownerTimeline.getId()).thenReturn(ownerIdTimeline);
       when(currentUserTimeline.getId()).thenReturn(currentUserIdTimeline);
       when(mockGardenTimeline.getOwner()).thenReturn(ownerTimeline);
       when(mockGardenTimeline.getId()).thenReturn(gardenIdTimeline);
       when(mockGardenTimeline.getIsPublic()).thenReturn(true);
       when(mockGardenTimeline.getPlants()).thenReturn(List.of(mockPlantTimeline));
       when(mockPlantTimeline.getId()).thenReturn(plantIdTimeline);
       when(mockPlantTimeline.getGarden()).thenReturn(mockGardenTimeline);

       // Define the behavior of the services
       when(gardenUserService.getCurrentUser()).thenReturn(currentUserTimeline);
       when(plantService.getPlantById(plantIdTimeline)).thenReturn(Optional.of(mockPlantTimeline));
       when(gardenService.getGardenById(gardenIdTimeline)).thenReturn(Optional.of(mockGardenTimeline));
    }

    @Test
    void getPlantTimeline_AttemptToAccessNonExistingGardenId_Shown404() {
        setUpForPlantTimelineTests();
        gardenIdTimeline = 1000L;
        when(gardenService.getGardenById(gardenIdTimeline)).thenReturn(Optional.empty());

        String result = plantController.getPlantTimeline(gardenIdTimeline, plantIdTimeline, model);
        assertEquals("error/404", result);
    }

    @Test
    void getPlantTimeline_AttemptToAccessNonExistingPlantId_Shown404() {
        setUpForPlantTimelineTests();
        plantIdTimeline = 1000L;
        when(gardenService.getGardenById(gardenIdTimeline)).thenReturn(Optional.empty());

        String result = plantController.getPlantTimeline(gardenIdTimeline, plantIdTimeline, model);
        assertEquals("error/404", result);
    }

    @Test
    void getPlantTimeline_NotOwnerViewingPrivateGardenPlant_AccessDenied() {
        setUpForPlantTimelineTests();
        currentUserIdTimeline = 3L;

        when(mockGardenTimeline.getIsPublic()).thenReturn(false);
        when(currentUserTimeline.getId()).thenReturn(currentUserIdTimeline);

        String result = plantController.getPlantTimeline(gardenIdTimeline, plantIdTimeline, model);
        assertEquals("error/accessDenied", result);
    }

    @Test
    void getPlantTimeline_OwnerViewingPrivateGardenPlant_DetailPageShown() {
        setUpForPlantTimelineTests();

        when(mockGardenTimeline.getIsPublic()).thenReturn(false);

        String result = plantController.getPlantTimeline(gardenIdTimeline, plantIdTimeline, model);
        assertEquals("plants/plantDetails", result);
    }

    @Test
    void getPlantTimeline_NonOwnerViewingPublicGardenPlant_DetailPageShown() {
        setUpForPlantTimelineTests();

        String result = plantController.getPlantTimeline(gardenIdTimeline, plantIdTimeline, model);
        assertEquals("plants/plantDetails", result);
    }

    @Test
    void testPlantInformationForm_ReturnsToPlantInformation() {
        String expectedReturnPage = "plants/plantInformation";
        String returnPage = plantController.plantInformationForm(model);
        assertEquals(expectedReturnPage, returnPage);
    }

    @Test
    void testPlantInformationSubmitForm_ReturnsToPlantInformation() {
        String expectedReturnPage = "plants/plantInformation";
        String returnPage = plantController.plantInformationSubmit(1, model);
        assertEquals(expectedReturnPage, returnPage);
    }
}
