package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenHistoryItemDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.FieldError;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GardenControllerTest {

    String EXPECTED_MODERATION_ERROR_MESSAGE = "The description does not match the language standards of the app.";
    String LOCATION_ERROR_MESSAGE = "Location name must only include letters, numbers, spaces, dots, hyphens or apostrophes";


    @Mock
    private GardenService gardenService;

    @Mock
    private GardenHistoryService gardenHistoryService;

    @Mock
    private PlantService plantService;

    @Mock
    private TagService tagService;

    @Mock
    private ModerationService moderationService;

    @Mock
    private WeatherAPIService weatherAPIService;

    @Mock
    private GardenUserService gardenUserService;

    @Mock
    private ProfanityService profanityService;
    @Mock
    private LocationService locationService;

    @InjectMocks
    private GardenController gardenController;

    private MultipartFile file;
    private static Authentication authentication;

    private static GardenRepository gardenRepository;
    private Garden mockGarden;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gardenRepository = mock(GardenRepository.class);
        GardenUser mockUser = mock(GardenUser.class);
        when(mockUser.getId()).thenReturn(1L);
        when(gardenUserService.getCurrentUser()).thenReturn(mockUser);
        when(gardenService.getGardensByOwnerId(1L)).thenReturn(Collections.emptyList());
        Page<Garden> mockGardenPage = mock(Page.class);
        when(mockGardenPage.getTotalPages()).thenReturn(0);
        when(gardenService.getGardensByOwnerId(eq(1L), any(PageRequest.class))).thenReturn(mockGardenPage);
        mockGarden = new Garden();
        mockGarden.setOwner(mockUser);
        when(gardenService.getGardenById(0L)).thenReturn(Optional.of(mockGarden));
        file = new MockMultipartFile("file", "filename.txt", "text/plain", "Some file content".getBytes());

        authentication = mock(Authentication.class);
    }

    @Test
    public void testForm() {
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(session.getAttribute("submissionToken")).thenReturn("mockToken123");
        when(request.getHeader("Referer")).thenReturn("");
        String result = gardenController.getCreateGardenForm(model,session,request);

        verify(model).addAttribute(eq("garden"), any(GardenDTO.class));
        verify(model).addAttribute(eq("gardens"), anyList());
        verify(model).addAttribute(eq("referer"), anyString());
        assertEquals("gardens/createGarden", result);
    }

    @Test
    public void testSubmitForm_ValidationFailure() {
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("submissionToken")).thenReturn("mockToken123");
        GardenDTO invalidGarden = new GardenDTO("","","","","","","",0.0,0.0,"",null,"Token123", null, null);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        when(authentication.getPrincipal()).thenReturn((Long) 1L);
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());
        String result = gardenController.submitCreateGardenForm(invalidGarden, bindingResult, file, authentication, model,session);

        assertEquals("gardens/createGarden", result);
    }

    @Test
    public void testSubmitForm_ValidationSuccess() {
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("submissionToken")).thenReturn("mockToken123");
        GardenDTO validGardenDTO = new GardenDTO("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description", "100","mockToken123", null, null);
        validGardenDTO.setId((long) 1);
        Garden validGarden = validGardenDTO.toGarden();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(gardenService.addGarden(Mockito.any())).thenReturn(validGarden);

        when(authentication.getPrincipal()).thenReturn(1L);
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());

        Mockito.when(moderationService.moderateDescription(anyString())).thenReturn(ResponseEntity.ok().build());
        String result = gardenController.submitCreateGardenForm(validGardenDTO, bindingResult, file, authentication, model, session);
        assertEquals("redirect:/gardens/1", result);
    }

    @Test
    public void testResponses() {
        Model model = mock(Model.class);
        when(gardenService.getAllGardens()).thenReturn(Collections.emptyList());

        String result = gardenController.responses(model,0,10);
        assertEquals("gardens/viewGardens", result);
        verify(model).addAttribute(any(String.class),any(Page.class));
    }

    @Test
    public void testGardenDetail() {
        Model model = mock(Model.class);
        GardenDTO gardenDTO = new GardenDTO("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description","100","Token123", null, null);
        Garden garden = gardenDTO.toGarden();
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        when(plantService.getPlantsByGardenId(1L)).thenReturn(Collections.emptyList());
        GardenUser owner = new GardenUser();
        owner.setId(1L);
        garden.setOwner(owner);
        when(gardenUserService.getCurrentUser()).thenReturn(owner);


        String result = gardenController.gardenDetail(1L, model);
        assertEquals("gardens/gardenDetails", result);
        verify(model).addAttribute("garden", garden);
        verify(plantService).getPlantsByGardenId(1L);
    }

    @Test
    public void testGetGarden() {
        Model model = mock(Model.class);
        GardenUser owner = new GardenUser();
        owner.setId(1L);
        GardenDTO gardenDTO = new GardenDTO("Test Garden", "1", "test", "test suburb", "test city", "test country", "1234", 0.0, 0.0, "test description", "100","Token123", null, null);
        Garden garden = gardenDTO.toGarden();
        garden.setOwner(owner);

        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        when(gardenUserService.getCurrentUser()).thenReturn(owner);
        when(gardenService.getGardensByOwnerId(owner.getId())).thenReturn(Collections.singletonList(garden));

        String result = gardenController.getGarden(1L, model);

        assertEquals("gardens/editGarden", result);
        verify(model).addAttribute("garden", garden);
        verify(model).addAttribute("gardens", Collections.singletonList(garden));
    }

    @Test
    public void testUpdateGarden() {
        Model model = mock(Model.class);
        MultipartFile file = new MockMultipartFile(
                "image",
                "profile.png",
                "image/png",
                "profile picture content".getBytes()
        );
        GardenDTO gardenDTO = new GardenDTO("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description", "100","Token123", null, null);
        when(gardenService.getGardenById(1)).thenReturn(Optional.of(gardenDTO.toGarden()));
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        String result = gardenController.updateGarden(1, gardenDTO, bindingResult, file, model);
        Garden garden = gardenService.getGardenById(1).get();

        assertEquals("redirect:/gardens/1", result);
        assertEquals("Test Garden", garden.getName());
        assertEquals("1", garden.getStreetNumber());
        assertEquals("test", garden.getStreetName());
        assertEquals("test suburb", garden.getSuburb());
        assertEquals("test city", garden.getCity());
        assertEquals("test country", garden.getCountry());
        assertEquals("1234",garden.getPostCode());
        assertEquals("test description", garden.getDescription());
        assertEquals(100L, garden.getSize());
    }

    @Test
    void testGetGarden_GardenNotPresent_ReturnsAccessDenied() {
        Model model = mock(Model.class);
        when(gardenService.getGardenById(0L)).thenReturn(Optional.empty());
        String result = gardenController.getGarden(0L, model);
        assertEquals("error/accessDenied", result);
    }

    @Test
    public void setUpdateStatusTrueQueryDatabaseReturnTrue() {
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(new Garden()));
        when(gardenService.addGarden(any())).thenReturn(new Garden());
        String result = gardenController.updatePublicStatus(1L, true);
        assertEquals(gardenService.getGardenById(1L).get().getIsPublic(), true);
        assertEquals("redirect:/gardens/1", result);
    }
    @Test
    public void setUpdateStatusFalseQueryDatabaseReturnFalse() {
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(new Garden()));
        when(gardenService.addGarden(any())).thenReturn(new Garden());
        String result = gardenController.updatePublicStatus(1L, false);
        assertEquals(gardenService.getGardenById(1L).get().getIsPublic(), false);
        assertEquals("redirect:/gardens/1", result);
    }

    @Test
    public void testWhenImMakingAGarden_AndIHaveAGardenErrorAndAProfanityError_ThenTheModelHasProfanityError() {
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("submissionToken")).thenReturn("mockToken123");
        String description = "some really nasty words";
        GardenDTO invalidGardenDTO = new GardenDTO("","","","","","","",0.0,0.0,description,"","mockToken123", null, null);
        Garden invalidGarden = invalidGardenDTO.toGarden();
        gardenService.addGarden(invalidGarden);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);


        when(gardenRepository.save(invalidGarden)).thenReturn(invalidGarden);
        when(gardenUserService.getUserById(1L)).thenReturn(new GardenUser());
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());
        when(moderationService.checkIfDescriptionIsFlagged(description)).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenService.addGarden(any())).thenReturn(invalidGarden);
        gardenController.submitCreateGardenForm(invalidGardenDTO, bindingResult, file, authentication, model,session);

        verify(model).addAttribute("profanity", EXPECTED_MODERATION_ERROR_MESSAGE);
        verify(model).addAttribute("garden", invalidGardenDTO);
    }

    @Test
    public void testWhenImEditingAGarden_AndIHaveAGardenErrorAndAProfanityError_ThenTheModelHasProfanityError() {
        Model model = mock(Model.class);
        long id = 0;
        MultipartFile file = new MockMultipartFile(
                "image",
                "profile.png",
                "image/png",
                "profile picture content".getBytes()
        );
        String description = "some really nasty words";
        GardenDTO invalidGarden = new GardenDTO("","","","","","","",0.0,0.0,description,null,"", null, null);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(moderationService.checkIfDescriptionIsFlagged(description)).thenReturn(true);
        gardenController.updateGarden(id, invalidGarden, bindingResult, file, model);

        verify(model).addAttribute("profanity", EXPECTED_MODERATION_ERROR_MESSAGE);
        verify(model).addAttribute("garden", invalidGarden);
    }

    @Test
    public void testGetGardenId() {
        Model model = mock(Model.class);
        Garden garden = new Garden("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description",null, null, null);
        GardenUser owner = new GardenUser();
        owner.setId(1L);
        garden.setOwner(owner);
        when(gardenUserService.getCurrentUser()).thenReturn(owner);
        when(gardenService.getGardenById(1)).thenReturn(Optional.of(garden));
        GardenWeather weatherResult = new GardenWeather();
        when(weatherAPIService.getWeatherData(1, 0.0, 0.0)).thenReturn(weatherResult);
        String result = gardenController.gardenDetail(1L, model);
        assertEquals("gardens/gardenDetails", result);
        verify(model).addAttribute("garden", garden);
    }

    @Test
    public void testGardenDetail_WithNullLatLon() {
        Model model = mock(Model.class);
        GardenDTO gardenDTO = new GardenDTO("Test Garden","1","test","test suburb","test city","test country","1234",null,null,"100","test description","", null, null);
        Garden garden = gardenDTO.toGarden();
        GardenUser owner = new GardenUser();
        owner.setId(1L);
        garden.setOwner(owner);
        when(gardenUserService.getCurrentUser()).thenReturn(owner);
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));


        String result = gardenController.gardenDetail(1L, model);

        assertEquals("gardens/gardenDetails", result);
        verify(model).addAttribute("garden", garden);
        verify(weatherAPIService, never()).getWeatherData(anyLong(), anyDouble(), anyDouble());

    }

    @Test
    void testCheckGardenError_WithProfanity() {
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        GardenDTO garden = new GardenDTO();
        ArrayList<String> profanity = new ArrayList<>();
        profanity.add("badword");
        garden.setDescription("badword");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(profanityService.badWordsFound("badword")).thenReturn(profanity);
        when(moderationService.checkIfDescriptionIsFlagged(anyString())).thenReturn(false);

        gardenController.checkGardenDTOError(model, bindingResult, garden);

        verify(model).addAttribute("profanity", "The description does not match the language standards of the app.");
        verify(model, never()).addAttribute(eq("locationError"), anyString());
    }

    @Test
    void testCheckGardenError_WithModerationFlagged() {
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        GardenDTO garden = new GardenDTO();
        garden.setDescription("suspicious description");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(profanityService.badWordsFound("suspicious description")).thenReturn(new ArrayList<>());
        when(moderationService.checkIfDescriptionIsFlagged("suspicious description")).thenReturn(true);

        gardenController.checkGardenDTOError(model, bindingResult, garden);

        verify(model).addAttribute("profanity", "The description does not match the language standards of the app.");
        verify(model, never()).addAttribute(eq("locationError"), anyString());
    }

    @Test
    void testCheckGardenError_NoErrors() {
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        GardenDTO garden = new GardenDTO();
        garden.setDescription("good description");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(profanityService.badWordsFound("good description")).thenReturn(new ArrayList<>());
        when(moderationService.checkIfDescriptionIsFlagged("good description")).thenReturn(false);

        gardenController.checkGardenDTOError(model, bindingResult, garden);

        verify(model, never()).addAttribute(eq("profanity"), anyString());
        verify(model, never()).addAttribute(eq("locationError"), anyString());
    }

    @Test
    void testCheckGardenError_WithLocationError() {
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);
        GardenDTO garden = new GardenDTO();

        FieldError fieldError = new FieldError("garden", "city", null,false, new String[]{"Pattern"},null,null);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());
        when(moderationService.checkIfDescriptionIsFlagged(anyString())).thenReturn(false);

        gardenController.checkGardenDTOError(model, bindingResult, garden);

        verify(model).addAttribute("locationError", "Location name must only include letters, numbers, spaces, dots, hyphens or apostrophes");
        verify(model, never()).addAttribute(eq("profanity"), anyString());
    }

    @Test
    void testSearchPublicGardens_WithInvalidTag() {
        Model model = mock(Model.class);

        String tags = "validTag,invalidTag";
        when(tagService.getTag("validTag")).thenReturn(new Tag("validTag"));
        when(tagService.getTag("invalidTag")).thenReturn(null);


        Pageable pageable = mock(Pageable.class);
        when(gardenService.findGardensBySearchAndTags(anyString(), anyList(), any(Pageable.class)))
                .thenReturn(null);


        String viewName = gardenController.searchPublicGardens(0, 10, "", tags, model);

        verify(model).addAttribute("tagString", tags);
        assertEquals("gardens/publicGardens", viewName);
    }

    @Test
    void testAccessPrivateGardensIfNotOwner_thenAccessDenied() {
        Model model = mock(Model.class);
        Garden garden = new Garden();

        GardenUser owner = new GardenUser();
        owner.setId(1L);
        garden.setOwner(owner);

        GardenUser currentUser = new GardenUser();
        currentUser.setId(2L);


        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        when(gardenUserService.getCurrentUser()).thenReturn(currentUser);

        String result = gardenController.gardenDetail(1L, model);

        assertEquals("error/accessDenied", result);
    }

    @Test
    void givenIGoToTheGardenHistoryPage_whenThereIsAGardenWithHistory_thenTheGardenHistoryIsAddedToTheModel() {
        Model model = mock(Model.class);
        when(authentication.getPrincipal()).thenReturn(1L);
        LocalDate expectedDate = LocalDate.of(1999, 1, 1);
        Long gardenId = 0L;
        List<Plant> plants = new ArrayList<>();
        Plant plant = new Plant("test", "1", "test", expectedDate);
        plants.add(plant);
        mockGarden.setPlants(plants);

        GardenHistoryItemDTO expectedDTO = new GardenHistoryItemDTO(plant, plant.getPlantedDate(), GardenHistoryItemDTO.Action.PLANTED);
        SortedMap<LocalDate, List<GardenHistoryItemDTO>> expectedHistory = new TreeMap<>(Comparator.reverseOrder());
        expectedHistory.put(expectedDate, List.of(expectedDTO));
        when(gardenHistoryService.getGardenHistory(mockGarden)).thenReturn(expectedHistory);

        String result = gardenController.gardenHistory(authentication, gardenId, model);

        verify(model).addAttribute("history", expectedHistory);
        Assertions.assertEquals("gardens/gardenHistory", result);
    }

    @Test
    void givenIGoToTheGardenHistoryPage_whenThereIsNoGardenWithHistory_thenReturnAnEmptyMap() {
        Model model = mock(Model.class);
        when(authentication.getPrincipal()).thenReturn(1L);
        LocalDate expectedDate = LocalDate.of(1999, 1, 1);
        Long gardenId = 0L;
        List<Plant> plants = new ArrayList<>();
        mockGarden.setPlants(plants);

        SortedMap<LocalDate, List<GardenHistoryItemDTO>> expectedHistory = new TreeMap<>(Comparator.reverseOrder());
        expectedHistory.put(expectedDate, List.of());
        when(gardenHistoryService.getGardenHistory(mockGarden)).thenReturn(expectedHistory);

        String result = gardenController.gardenHistory(authentication, gardenId, model);

        verify(model).addAttribute("history", expectedHistory);
        Assertions.assertEquals("gardens/gardenHistory", result);
    }

    @Test
    void givenIGoToTheGardenHistoryPage_whenThereIsNoGarden_thenRedirectTo404() {
        Model model = mock(Model.class);
        when(authentication.getPrincipal()).thenReturn(1L);
        LocalDate expectedDate = LocalDate.of(1999, 1, 1);
        Long gardenId = 999L;
        List<Plant> plants = new ArrayList<>();
        mockGarden.setPlants(plants);

        SortedMap<LocalDate, List<GardenHistoryItemDTO>> expectedHistory = new TreeMap<>(Comparator.reverseOrder());
        expectedHistory.put(expectedDate, List.of());
        when(gardenHistoryService.getGardenHistory(mockGarden)).thenReturn(expectedHistory);

        String result = gardenController.gardenHistory(authentication, gardenId, model);

        Assertions.assertEquals("error/404", result);
    }

    @Test
    void givenIGoToTheGardenHistoryPage_whenTheGardenIsNotMine_andItIsNotPublic_thenDontAuthenticate() {
        Model model = mock(Model.class);
        when(authentication.getPrincipal()).thenReturn(2L);
        mockGarden.setPublic(false);

        SortedMap<LocalDate, List<GardenHistoryItemDTO>> expectedHistory = new TreeMap<>(Comparator.reverseOrder());
        when(gardenHistoryService.getGardenHistory(mockGarden)).thenReturn(expectedHistory);

        String result = gardenController.gardenHistory(authentication, 0L, model);

        Assertions.assertEquals("error/accessDenied", result);
    }

    @Test
    void whenGardenImageExists_returnGardenImage() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        String imagePath = "static/img/garden.png";
        try (InputStream inputStream = GardenControllerTest.class.getClassLoader().getResourceAsStream(imagePath)) {
            if (inputStream == null) {
                throw new IOException("Image not found: " + imagePath);
            }
            byte[] image = inputStream.readAllBytes();
            String contentType = "image/png";
            Garden garden = new Garden();
            garden.setGardenImage(contentType, image);
            when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
            System.out.println("image " + garden.getGardenImage());
            System.out.println("content " + garden.getGardenImageContentType());
            ResponseEntity<byte[]> response = gardenController.gardenImage(1L, mockRequest);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(contentType, response.getHeaders().getContentType().toString());
            assertEquals(image, response.getBody());

        } catch (IOException e) {
            System.out.println("Error with resource file " + e.getMessage());
        }
    }

    @Test
    void whenGardenImageNotExist_returnNull() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        Garden garden = new Garden();
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        ResponseEntity<byte[]> response = gardenController.gardenImage(1L, mockRequest);
        assertNull(response);
    }


}
