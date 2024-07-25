package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;

import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class U28GardenHistoryPlantDateFeature {

    private static PlantService plantService;

    private PlantController plantController;

    private static PlantRepository mockPlantRepository;

    private static GardenRepository mockGardenRepository;
    private static BindingResult bindingResult;
    private static Model model;
    private PlantDTO plantDTO;
    private MultipartFile file;

    private long gardenId = 1L; // Example gardenId
    private String date = "2024-07-26"; // Example date

    @BeforeAll
    public static void beforeAll() {
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        mockPlantRepository = mock(PlantRepository.class);
        mockGardenRepository = mock(GardenRepository.class);
        plantService = new PlantService(mockPlantRepository, mockGardenRepository);
        Garden dummyGarden = new Garden();
        dummyGarden.setId(1L); // Ensure this matches the gardenId used in tests
        when(mockGardenRepository.findById(anyLong())).thenReturn(Optional.of(dummyGarden));
    }

    @Given("I am on the add plant form")
    public void i_am_on_the_add_plant_form() {
        plantDTO = new PlantDTO();
        file = new MockMultipartFile("image", "testImage.jpg", "image/jpeg", "test image content".getBytes());
    }
    @When("I enter a valid plant name {string} and a valid date {string}")
    public void i_enter_a_valid_plant_name_and_a_valid_date(String name, String date) {
        plantDTO.setName(name);
        plantDTO.setPlantedDate(date);
        plantDTO.setCount("0");
        plantDTO.setDescription("test");
    }

    @When("I submit the add plant form")
    public void i_submit_the_add_plant_form() {
        Plant savedPlant = new Plant(plantDTO);
        when(plantService.createPlant(plantDTO, gardenId)).thenReturn(new Plant(plantDTO));
        doNothing().when(plantService).setPlantImage(anyLong(), anyString(), any(byte[].class));
        plantController.submitAddPlantForm(gardenId, plantDTO, bindingResult, file, date, model);
    }
    @Then("the plant is successfully added")
    public void the_plant_is_successfully_added() {
        verify(plantService).createPlant(eq(plantDTO), eq(gardenId));
        verify(plantService).setPlantImage(anyLong(), anyString(), any(byte[].class));
    }

    @When("I enter a valid plant name {string} and a invalid date {string}")
    public void i_enter_a_valid_plant_name_and_a_invalid_date(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the plant is not added")
    public void the_plant_is_not_added() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
