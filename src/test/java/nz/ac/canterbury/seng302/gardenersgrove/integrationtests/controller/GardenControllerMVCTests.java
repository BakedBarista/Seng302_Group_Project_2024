package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.service.weatherAPI.WeatherAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension .class)
public class GardenControllerMVCTests {
    @Mock
    private GardenService gardenService;

    private static Authentication mockAuthentication;

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

    @Mock
    private ModerationService moderationService;

    @Mock
    private Authentication authrne;
    
    private static Garden emptyGarden;
    private static Garden patternGarden;
    private static GardenUser mockuser;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the GardenUser and related services
        GardenUser owner = mock(GardenUser.class);
        gardenUserService = mock(GardenUserService.class);
        gardenService = mock(GardenService.class);
        plantService = mock(PlantService.class);
        moderationService = mock(ModerationService.class);
        gardenController = new GardenController(gardenService, plantService, gardenUserService, weatherAPIService, friendService, moderationService);
        mockAuthentication = mock(Authentication.class);

        Garden mockGarden1 = new Garden();
        Mockito.when(gardenService.getGardenById(1)).thenReturn(Optional.of(mockGarden1));
        emptyGarden = new Garden();
        patternGarden = new Garden();
        patternGarden.setName("tets");
        patternGarden.setCountry("!");
        patternGarden.setCity("!");
      
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
        given(gardenService.getPageForPublicGardens(PageRequest.of(page, size))).willReturn(expectedGardens);
        // Perform the GET request
        mockMvc.perform(get("/gardens/public")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(view().name("gardens/publicGardens"))
                .andExpect(model().attributeExists("gardenPage"))
                .andExpect(model().attribute("gardenPage", expectedGardens));
    }

//    @Test
//    public void testCreateGardens() throws Exception {
//        Double where = 1D;
//        Garden garden = new Garden("test", "1", "ilam", "chch", "chch", "nz", "8041", where, where, "10", "test");
//        GardenUser user = new GardenUser("John", "Doe", "john@email.com", "P#ssw0rd", "10/10/2000");
//        when(gardenUserService.getUserById(1L)).thenReturn(user);
//        Mockito.when(mockAuthentication.getPrincipal()).thenReturn(user.getId());
//        mockMvc.perform(post("/gardens/create").flashAttr("garden", garden))
//                .andExpect(status().isOk())
//                .andExpect(view().name("gardens/1")
//                );
//    }

    @Test
    public void testLocationEmptyErrorGardens() throws Exception {

        // Perform the GET request
        mockMvc.perform(post("/gardens/create").flashAttr("garden", emptyGarden))
                .andExpect(status().isOk())
                .andExpect(view().name("gardens/createGarden"))
                .andExpect(model().attribute("locationError", "Location cannot be empty"));
    }

    @Test
    public void testLocationPatternErrorGardens() throws Exception {

        // Perform the GET request
        mockMvc.perform(post("/gardens/create").flashAttr("garden", patternGarden))
                .andExpect(status().isOk())
                .andExpect(view().name("gardens/createGarden"))
                .andExpect(model().attribute("locationError", "Location name must only include letters, numbers, spaces, dots, hyphens or apostrophes"));
    }
}
