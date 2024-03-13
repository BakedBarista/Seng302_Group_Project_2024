package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    @Mock
    private PlantService plantService;

    @Mock
    private GardenService gardenService;



    @InjectMocks
    private UploadController uploadController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void displayUploadForm() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(uploadController).build();
        mockMvc.perform(get("/uploadImage")
                        .param("garden_Id", "1")
                        .param("plant_Id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("images/uploadPage"))
                .andExpect(model().attributeExists("plantId", "gardenId"));
    }

    @Test
    void givenCorrectFileSelectedThenUploadSuccessful() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(uploadController).build();
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
