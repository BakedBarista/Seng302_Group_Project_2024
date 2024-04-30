package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.UploadController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    @Mock
    private PlantService plantService;

    @Mock
    private GardenService gardenService;

    @Mock
    private GardenUserService gardenUserService;


    @InjectMocks
    private UploadController uploadController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        // Create a mock GardenUser and ensure it returns a valid ID when getId() is called
        GardenUser owner = mock(GardenUser.class);
        gardenUserService = mock(GardenUserService.class);
        gardenService = mock(GardenService.class);
        plantService = mock(PlantService.class);
        uploadController = new UploadController(plantService, gardenUserService, gardenService);

        lenient().when(owner.getId()).thenReturn(1L);
        lenient().when(gardenUserService.getCurrentUser()).thenReturn(owner);
        lenient().when(gardenService.getGardensByOwnerId(1L)).thenReturn(Collections.emptyList());
        mockMvc = MockMvcBuilders.standaloneSetup(uploadController).build();
    }






    @Test
    void displayUploadForm() throws Exception {
        mockMvc.perform(get("/uploadImage")
                        .param("garden_Id", "1")
                        .param("plant_Id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("images/uploadPage"))
                .andExpect(model().attributeExists("plantId", "gardenId", "gardens"));
    }




    @Test
    void givenCorrectFileSelectedThenUploadSuccessful() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("image", "test.png", "image/png", "test data".getBytes());

        when(plantService.getPlantById(1)).thenReturn(Optional.of(new Plant("","1","","")));

        mockMvc.perform(multipart("/uploadImage")
                        .file(multipartFile)
                        .param("garden_Id", "1")
                        .param("plant_Id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/1/plants/1/edit?plant_Id=1&garden_Id=1"));
    }
}
