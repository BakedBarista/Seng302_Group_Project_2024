package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
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
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantHistoryService;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class U28GardenHistoryPlantDateFeature {

    @Autowired
    private PlantService plantService;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private PlantHistoryService plantHistoryService;
    @Autowired
    private GardenUserService gardenUserService;

    @Autowired
    private PlantController plantController;

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
    }

    @And("I have a garden named {string}")
    public void iHaveAGardenNamed(String arg0) {

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
        plantController.submitAddPlantForm(gardenId, plantDTO, bindingResult, file, date, model);
    }

    @Then("the plant is successfully added")
    public void the_plant_is_successfully_added() {
        assertNotNull(plantService.getPlantById(1));
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
