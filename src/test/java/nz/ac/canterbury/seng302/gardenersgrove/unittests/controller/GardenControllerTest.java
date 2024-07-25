package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;


import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.weather.GardenWeather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GardenControllerTest {

    String EXPECTED_MODERATION_ERROR_MESSAGE = "The description does not match the language standards of the app.";
    String LOCATION_ERROR_MESSAGE = "Location name must only include letters, numbers, spaces, dots, hyphens or apostrophes";

    @Mock
    private GardenService gardenService;

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

    private static Authentication authentication;

    private static GardenRepository gardenRepository;
    private static Garden mockGarden;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gardenRepository = mock(GardenRepository.class);
        GardenUser mockUser = mock(GardenUser.class);
        mockGarden = mock(Garden.class);
        when(mockUser.getId()).thenReturn(1L);
        when(gardenUserService.getCurrentUser()).thenReturn(mockUser);
        when(gardenService.getGardensByOwnerId(1L)).thenReturn(Collections.emptyList());

        Garden mockGarden = new Garden();
        mockGarden.setOwner(mockUser);
        when(gardenService.getGardenById(0L)).thenReturn(Optional.of(mockGarden));

        authentication = mock(Authentication.class);
    }

    @Test
    public void testForm() {
        Model model = mock(Model.class);
        String result = gardenController.getCreateGardenForm(model);

        verify(model).addAttribute(eq("garden"), any(GardenDTO.class));
        verify(model).addAttribute(eq("gardens"), anyList());

        assertEquals("gardens/createGarden", result);
    }

    @Test
    public void testSubmitForm_ValidationFailure() {
        Model model = mock(Model.class);
        GardenDTO invalidGarden = new GardenDTO("","","","","","","",0.0,0.0,"",null);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        when(authentication.getPrincipal()).thenReturn((Long) 1L);
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());
        String result = gardenController.submitCreateGardenForm(invalidGarden, bindingResult, authentication, model);

        assertEquals("gardens/createGarden", result);
    }

    @Test
    public void testSubmitForm_ValidationSuccess() {
        Model model = mock(Model.class);
        GardenDTO validGardenDTO = new GardenDTO("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description", "100");
        validGardenDTO.setId((long) 1);
        Garden validGarden = validGardenDTO.toGarden();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(gardenService.addGarden(Mockito.any())).thenReturn(validGarden);

        when(authentication.getPrincipal()).thenReturn(1L);
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());

        Mockito.when(moderationService.moderateDescription(anyString())).thenReturn(ResponseEntity.ok().build());
        String result = gardenController.submitCreateGardenForm(validGardenDTO, bindingResult, authentication, model);
        assertEquals("redirect:/gardens/1", result);
    }

    @Test
    public void testResponses() {
        Model model = mock(Model.class);
        when(gardenService.getAllGardens()).thenReturn(Collections.emptyList());

        String result = gardenController.responses(model);
        assertEquals("gardens/viewGardens", result);
        verify(model).addAttribute("gardens", Collections.emptyList());
    }

    @Test
    public void testGardenDetail() {
        Model model = mock(Model.class);
        GardenDTO gardenDTO = new GardenDTO("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description","100");
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
        GardenDTO gardenDTO = new GardenDTO("Test Garden", "1", "test", "test suburb", "test city", "test country", "1234", 0.0, 0.0, "test description", "100");
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
        GardenDTO gardenDTO = new GardenDTO("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description", "100");
        when(gardenService.getGardenById(1)).thenReturn(Optional.of(gardenDTO.toGarden()));
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        String result = gardenController.updateGarden(1, gardenDTO, bindingResult, model);
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
        assertEquals("/error/accessDenied", result);
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
        String description = "some really nasty words";
        GardenDTO invalidGardenDTO = new GardenDTO("","","","","","","",0.0,0.0,description,"");
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
        gardenController.submitCreateGardenForm(invalidGardenDTO, bindingResult, authentication, model);

        verify(model).addAttribute("profanity", EXPECTED_MODERATION_ERROR_MESSAGE);
        verify(model).addAttribute("garden", invalidGardenDTO);
    }

    @Test
    public void testWhenImEditingAGarden_AndIHaveAGardenErrorAndAProfanityError_ThenTheModelHasProfanityError() {
        Model model = mock(Model.class);
        long id = 0;
        String description = "some really nasty words";
        GardenDTO invalidGarden = new GardenDTO("","","","","","","",0.0,0.0,description,null);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(moderationService.checkIfDescriptionIsFlagged(description)).thenReturn(true);
        gardenController.updateGarden(id, invalidGarden, bindingResult, model);

        verify(model).addAttribute("profanity", EXPECTED_MODERATION_ERROR_MESSAGE);
        verify(model).addAttribute("garden", invalidGarden);
    }

    @Test
    public void testGetGardenId() {
        Model model = mock(Model.class);
        Garden garden = new Garden("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"test description",null);
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
        GardenDTO gardenDTO = new GardenDTO("Test Garden","1","test","test suburb","test city","test country","1234",null,null,"100","test description");
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

        assertEquals("/error/accessDenied", result);
    }

}
