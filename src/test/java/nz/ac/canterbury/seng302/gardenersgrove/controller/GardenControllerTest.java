package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GardenControllerTest {
    @Mock
    private GardenService gardenService;

    @Mock
    private PlantService plantService;

    @Mock
    private GardenUserService gardenUserService;

    @InjectMocks
    private GardenController gardenController;

    private GardenUser gardenUser;

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
        Garden invalidGarden = new Garden("","","","");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        String result = gardenController.submitForm(invalidGarden, bindingResult, model);
        assertEquals("gardens/createGarden", result);
    }

    @Test
    public void testSubmitForm_ValidationSuccess() {
        Model model = mock(Model.class);
        Garden validGarden = new Garden("Test Garden","Test Location","100","Test Description");
        validGarden.setId((long) 1);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(gardenService.addGarden(validGarden)).thenReturn(validGarden);
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
        Garden garden = new Garden("Test Garden", "Test Location", "Test Size","Test Description");
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
        Garden garden = new Garden("Test Garden", "Test Location", "Test Size","Test Description");
        when(gardenService.getGardenById(1)).thenReturn(Optional.of(garden));

        String result = gardenController.getGarden(1, model);
        assertEquals("gardens/editGarden", result);
        verify(model).addAttribute("garden", garden);
    }

    @Test
    public void testUpdateGarden() {
        Model model = mock(Model.class);
        Garden garden = new Garden("Test Garden", "Test Location", "Test Size", "Test Description");
        when(gardenService.getGardenById(1)).thenReturn(Optional.of(garden));
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        String result = gardenController.updateGarden(1, garden, bindingResult, model);
        assertEquals("redirect:/gardens/1", result);
        assertEquals("Test Garden", garden.getName());
        assertEquals("Test Location", garden.getLocation());
        assertEquals("Test Size", garden.getSize());
        assertEquals("Test Description", garden.getDescription());
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
}
