package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension .class)
public class GardenControllerMVCTests {
    @Mock
    private GardenService gardenService;



    @InjectMocks
    private GardenController gardenController;

    private MockMvc mockMvc;
    @Mock
    private PlantService plantService;  // Mocking PlantService

    @Mock
    private GardenUserService gardenUserService;  // Mocking GardenUserService

    @Mock
    private WeatherAPIService weatherAPIService;

    @Mock
    private FriendService friendService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the GardenUser and related services
        GardenUser owner = mock(GardenUser.class);
        gardenUserService = mock(GardenUserService.class);
        gardenService = mock(GardenService.class);
        plantService = mock(PlantService.class);
        gardenController = new GardenController(gardenService, plantService, gardenUserService, weatherAPIService, friendService);

        // Setting up lenient behavior to avoid unnecessary stubbing exceptions
        lenient().when(owner.getId()).thenReturn(1L);
        lenient().when(gardenUserService.getCurrentUser()).thenReturn(owner);
        lenient().when(gardenService.getGardensByOwnerId(1L)).thenReturn(Collections.emptyList());

        // Build the MockMvc instance with the gardenController
        mockMvc = MockMvcBuilders.standaloneSetup(gardenController).build();
    }

    @Test
    public void testPublicGardens() throws Exception {
        // Setup our mocked service
        int page = 0;
        int size = 10;

        Page<Garden> expectedGardens = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        given(gardenService.getPublicGardens(PageRequest.of(page, size))).willReturn(expectedGardens);
        // Perform the GET request
        mockMvc.perform(get("/gardens/public")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(view().name("gardens/publicGardens"))
                .andExpect(model().attributeExists("gardenPage"))
                .andExpect(model().attribute("gardenPage", expectedGardens));
    }
}
