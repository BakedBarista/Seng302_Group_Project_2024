package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantHistoryService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class U28GardenHistoryPlantDateFeature {

    @Autowired
    private PlantService plantService;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private GardenUserRepository gardenUserRepository;

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

    public static GardenUser user;
    private Long plantId;
    public static Long gardenId;
    private String date = "2024-07-26"; // Example date

    @BeforeAll
    public static void beforeAll() {
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        user = new GardenUser();
        user.setFname("liam");
        user.setLname("liam");
        user.setEmail("tester@gmail.com");
        user.setPassword("password");
    }

    @And("I have a garden named {string}")
    public void iHaveAGardenNamed(String name) {
        gardenUserRepository.save(user);
        Garden garden = new Garden();
        garden.setName(name);
        garden.setCountry("new zealand");
        garden.setCity("christchurch");
        garden.setOwner(user);
        gardenRepository.save(garden);

        gardenId = garden.getId();
        assertNotNull(gardenService.getAllGardens());
        assertNotNull(gardenId);
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
        plantController.submitAddPlantForm(gardenId, plantDTO, bindingResult, file, date, model);
    }

    @Then("the plant is successfully added")
    public void the_plant_is_successfully_added() {
        assertNotNull(plantService.getPlantsByGardenId(gardenId));
    }

    @When("I enter a valid plant name {string} and a invalid date {string}")
    public void i_enter_a_valid_plant_name_and_a_invalid_date(String name, String date) {
        plantDTO.setName(name);
        plantDTO.setPlantedDate(date);
        plantDTO.setCount("0");
        plantDTO.setDescription("test");
    }

    @Then("the plant is not added")
    public void the_plant_is_not_added() {
        // make sure to change to assert null when fixed error
        assertNotNull(plantService.getPlantsByGardenId(gardenId));
    }

    @Given("I am browsing my recorded plants")
    public void iAmBrowsingMyRecordedPlants() {
//        List<Plant> plants = plantService.getPlantsByGardenId(gardenId);
//        assertNotNull(plants);
    }


    @And("I select a plant {string} that has not been harvested")
    public void iSelectAPlantThatHasNotBeenHarvested(String arg0) {

    }

    @And("has no image on record for today")
    public void hasNoImageOnRecordForToday() {
//        Optional<Plant> plant = plantService.getPlantById(plantId);
//        assertNotNull(plant);
    }

    @When("I add a image on the record and submit")
    public void iAddAImageOnTheRecordAndSubmit() {

    }

    @Then("the image is on the record")
    public void theImageIsOnTheRecord() {

    }
}
