package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GardenControllerTest {
    @Mock
    private GardenService gardenService;

    @Mock
    private PlantService plantService;

    @Mock
    private WeatherAPIService weatherAPIService;

    @Mock
    private GardenUserService gardenUserService;

    @Mock
    private FriendService friendService;

    @Mock
    private ModerationService moderationService;

    @Mock
    private ProfanityService profanityService;

    @Mock
    private LocationService locationService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @Mock
    private TagService tagService;

    private GardenController gardenController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gardenController = new GardenController(gardenService,plantService,gardenUserService,weatherAPIService,tagService, friendService,moderationService,profanityService, locationService);
    }

    @Test
    void SubmitForm_LatLngReturnedProperly_LatLngSaved() {
        Garden garden = new Garden();
        garden.setStreetNumber("2");
        garden.setStreetName("Janet Street");
        garden.setSuburb("Upper Riccarton");
        garden.setCity("Christchurch");
        garden.setPostCode("8041");
        garden.setCountry("New Zealand");

        GardenUser gardenUser = new GardenUser();
        gardenUser.setId(1L);

        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenUserService.getUserById(1L)).thenReturn(gardenUser);
        when(locationService.getLatLng(anyString())).thenReturn(List.of(-43.53, 172.63));
        when(gardenService.addGarden(any(Garden.class))).thenReturn(garden);

        gardenController.submitCreateGardenForm(garden, bindingResult, authentication, model);

        assertEquals(-43.53, garden.getLat());
        assertEquals(172.63, garden.getLon());
    }

    @Test
    void SubmitForm_LatLngReturnedEmpty_LatLngNull() {
        Garden garden = new Garden();
        garden.setStreetNumber("2");
        garden.setStreetName("Janet Street");
        garden.setSuburb("Upper Riccarton");
        garden.setCity("Christchurch");
        garden.setPostCode("8041");
        garden.setCountry("New Zealand");

        GardenUser gardenUser = new GardenUser();
        gardenUser.setId(1L);

        when(authentication.getPrincipal()).thenReturn(1L);
        when(gardenUserService.getUserById(1L)).thenReturn(gardenUser);
        when(locationService.getLatLng(anyString())).thenReturn(new ArrayList<>());
        when(gardenService.addGarden(any(Garden.class))).thenReturn(garden);

        gardenController.submitCreateGardenForm(garden, bindingResult, authentication, model);

        assertNull(garden.getLat());
        assertNull(garden.getLon());
    }
}
