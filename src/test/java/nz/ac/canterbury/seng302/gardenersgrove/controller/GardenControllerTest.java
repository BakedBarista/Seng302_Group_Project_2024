package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GardenControllerTest {

    String EXPECTED_MODERATION_ERROR_MESSAGE = "The description does not match the language standards of the app.";

    @Mock
    private GardenService gardenService;

    @Mock
    private PlantService plantService;

    @Mock
    private ModerationService moderationService;

    @Mock
    private WeatherAPIService weatherAPIService;

    @Mock
    private GardenUserService gardenUserService;

    @Mock
    private ProfanityService profanityService;

    @InjectMocks
    private GardenController gardenController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        GardenUser mockUser = mock(GardenUser.class);
        when(mockUser.getId()).thenReturn(1L);
        when(gardenUserService.getCurrentUser()).thenReturn(mockUser);
        when(gardenService.getGardensByOwnerId(1L)).thenReturn(Collections.emptyList());

    }

    @Test
    public void testForm() {
        Model model = mock(Model.class);
        String result = gardenController.form(model);

        verify(model).addAttribute(eq("garden"), any(Garden.class));
        verify(model).addAttribute(eq("gardens"), anyList());

        assertEquals("gardens/createGarden", result);
    }

    @Test
    public void testSubmitForm_ValidationFailure() {
        Model model = mock(Model.class);
        Garden invalidGarden = new Garden("","","","","","","",0.0,0.0,"","");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());
        String result = gardenController.submitForm(invalidGarden, bindingResult, model);
        assertEquals("gardens/createGarden", result);
    }

    @Test
    public void testSubmitForm_ValidationSuccess() {
        Model model = mock(Model.class);
        Garden validGarden = new Garden("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"100","test description");
        validGarden.setId((long) 1);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(gardenService.addGarden(validGarden)).thenReturn(validGarden);
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());
        Mockito.when(moderationService.moderateDescription(anyString())).thenReturn(ResponseEntity.ok().build());
        String result = gardenController.submitForm(validGarden, bindingResult, model);
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
        Garden garden = new Garden("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"100","test description");
        when(gardenService.getGardenById(1L)).thenReturn(Optional.of(garden));
        when(plantService.getPlantsByGardenId(1L)).thenReturn(Collections.emptyList());


        String result = gardenController.gardenDetail(1L, model);
        assertEquals("gardens/gardenDetails", result);
        verify(model).addAttribute("garden", garden);
        verify(plantService).getPlantsByGardenId(1L);
    }

    @Test
    public void testGetGarden() {
        Model model = mock(Model.class);
        Garden garden = new Garden("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"100","test description");
        when(gardenService.getGardenById(1)).thenReturn(Optional.of(garden));

        String result = gardenController.getGarden(1, model);
        assertEquals("gardens/editGarden", result);
        verify(model).addAttribute("garden", garden);
    }

    @Test
    public void testUpdateGarden() {
        Model model = mock(Model.class);
        Garden garden = new Garden("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"100","test description");
        when(gardenService.getGardenById(1)).thenReturn(Optional.of(garden));
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        String result = gardenController.updateGarden(1, garden, bindingResult, model);
        assertEquals("redirect:/gardens/1", result);
        assertEquals("Test Garden", garden.getName());
        assertEquals("1", garden.getStreetNumber());
        assertEquals("test", garden.getStreetName());
        assertEquals("test suburb", garden.getSuburb());
        assertEquals("test city", garden.getCity());
        assertEquals("test country", garden.getCountry());
        assertEquals("1234",garden.getPostCode());
        assertEquals("100", garden.getSize());
        assertEquals("test description", garden.getDescription());
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
        Garden invalidGarden = new Garden("","","","","","","",0.0,0.0,"", description);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(profanityService.badWordsFound(anyString())).thenReturn(new ArrayList<>());
        when(moderationService.checkIfDescriptionIsFlagged(description)).thenReturn(true);
        gardenController.submitForm(invalidGarden, bindingResult, model);

        verify(model).addAttribute("profanity", EXPECTED_MODERATION_ERROR_MESSAGE);
        verify(model).addAttribute("garden", invalidGarden);
    }

    @Test
    public void testWhenImEditingAGarden_AndIHaveAGardenErrorAndAProfanityError_ThenTheModelHasProfanityError() {
        Model model = mock(Model.class);
        long id = 0;
        String description = "some really nasty words";
        Garden invalidGarden = new Garden("","","","","","","",0.0,0.0,"", description);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(moderationService.checkIfDescriptionIsFlagged(description)).thenReturn(true);
        gardenController.updateGarden(id, invalidGarden, bindingResult, model);

        verify(model).addAttribute("profanity", EXPECTED_MODERATION_ERROR_MESSAGE);
        verify(model).addAttribute("garden", invalidGarden);
    }
}
