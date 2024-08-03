package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.PlantStatusApiController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantHarvestedDateDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.mock;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U28GardenHistoryHarvestDateFeature {

    @Autowired
    private PlantService plantService;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private PlantStatusApiController plantStatusApiController;

    private static BindingResult bindingResult;
    private Long selectedPlantId;
    private Long selectedGardenId;
    private final PlantHarvestedDateDTO plantHarvestDTO = new PlantHarvestedDateDTO();

    @BeforeAll
    public static void setup() {
        bindingResult = mock(BindingResult.class);
    }

    @Given("I am browsing my recorded plants for garden {string}")
    public void i_am_browsing_my_recorded_plants_for_garden(String gardenName) {
        selectedGardenId = gardenRepository.findByName(gardenName).get(0).getId();
    }

    @And("{string} has no harvested date")
    public void plant_has_no_harvested_date(String plantName) {
        List<Plant> gardenPlants = plantService.getPlantsByGardenId(selectedGardenId);
        Plant plant = gardenPlants.stream().filter(_plant -> _plant.getName().equals(plantName)).findFirst().get();
        plant.setHarvestedDate(null);
        plantService.save(plant);
    }

    @When("I select a plant {string} to be harvested")
    public void i_select_a_plant_to_be_harvested(String plantName) {
        List<Plant> gardenPlants = plantService.getPlantsByGardenId(selectedGardenId);
        selectedPlantId = gardenPlants.stream().filter(plant -> plant.getName().equals(plantName)).findFirst().get().getId();
    }

    @And("I do not change the default date")
    public void i_do_not_change_the_default_date() {
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        plantHarvestDTO.setHarvestedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @And("I change the date to yesterday's date")
    public void i_change_the_date_to_yesterday_s_date() {
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        plantHarvestDTO.setHarvestedDate(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @And("I change the date to tomorrow's date")
    public void i_change_the_date_to_tomorrow_s_date() {
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        // plus 2 days to avoid race condition at midnight
        plantHarvestDTO.setHarvestedDate(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @And("I submit the harvest date form")
    public void i_submit_the_harvest_date_form() {
        plantStatusApiController.updateHarvestedDate(selectedPlantId, plantHarvestDTO, bindingResult);
    }

    @Then("The plant is marked harvested on today's date")
    public void the_plant_is_marked_harvested_on_today_s_date() {
        Plant plant = plantService.getPlantById(selectedPlantId).get();

        Assertions.assertEquals(LocalDate.now(), plant.getHarvestedDate());
    }

    @Then("The plant is marked harvested on yesterday's date")
    public void the_plant_is_marked_harvested_on_yesterday_s_date() {
        Plant plant = plantService.getPlantById(selectedPlantId).get();

        Assertions.assertEquals(LocalDate.now().minusDays(1), plant.getHarvestedDate());
    }

    @Then("The plant is not marked harvested")
    public void the_plant_is_not_marked_harvested() {
        Plant plant = plantService.getPlantById(selectedPlantId).get();

        Assertions.assertNull(plant.getHarvestedDate());
    }
}
