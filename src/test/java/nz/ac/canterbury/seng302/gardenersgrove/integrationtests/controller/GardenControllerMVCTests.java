package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.weather.WeatherAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @Mock
    private static TagService tagService;

    @Mock
    private ModerationService moderationService;

    @Mock
    private ProfanityService profanityService;
    @Mock
    private LocationService locationService;

    private MockHttpSession mockHttpSession;




    private static Garden emptyGarden;
    private static Garden patternGarden;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the GardenUser and related services
        GardenUser owner = mock(GardenUser.class);
        gardenUserService = mock(GardenUserService.class);
        gardenService = mock(GardenService.class);
        plantService = mock(PlantService.class);
        moderationService = mock(ModerationService.class);
        profanityService = mock(ProfanityService.class);
        gardenController = new GardenController(gardenService, null, plantService, gardenUserService, weatherAPIService, friendService, moderationService, profanityService, locationService);
        locationService = mock(LocationService.class);
        mockHttpSession = new MockHttpSession();

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

    @Test
    void whenGetCreateGardenForm_thenRefererIsAdded() throws Exception {
        //Simulate navigating to create garden page
        MvcResult createPageResult = mockMvc.perform(get("/gardens/create")
                .header("Referer","/previousPage"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("referer"))
                .andExpect(model().attribute("referer","/previousPage"))
                .andExpect(view().name("gardens/createGarden"))
                .andReturn();
        //Verify that referer is stored in the session
        HttpSession session = createPageResult.getRequest().getSession();
        String referer = (String) session.getAttribute("referer");
        assertNotNull(referer);
        assertTrue(referer.contains("/previousPage"));

        //Simulate submitting invalid form
        GardenDTO invalidGardenDTO = new GardenDTO();
        invalidGardenDTO.setSubmissionToken((String) session.getAttribute("submissionToken"));
        invalidGardenDTO.setName("");

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "Some Image Content".getBytes()
        );

        mockMvc.perform(multipart("/gardens/create")
                        .file(mockMultipartFile)
                        .session((MockHttpSession) session)
                        .flashAttr("garden",invalidGardenDTO))
                        .andExpect(status().isOk())
                        .andExpect(view().name("gardens/createGarden"))
                        .andExpect(model().attributeHasFieldErrors("garden","name"))
                        .andReturn();

        //Check referer still exists in session
        assertNotNull(session.getAttribute("referer"));
        assertTrue(session.getAttribute("referer").toString().contains("/previousPage"));
    }






}
