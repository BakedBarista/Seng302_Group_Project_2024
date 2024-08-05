package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.springframework.web.servlet.ModelAndView;

public class U26PlantSearch {
    @Autowired
    private PlantController plantController;

    @Autowired
    private WikiDataAPIController wikidataAPIController;

    @Autowired
    private WikidataService wikidataService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode autocompleteData;

    @Given("I am anywhere on the system")
    public void i_am_anywhere_on_the_system() {

    }

    @When("I click on a link to search for a plant")
    public void i_click_on_a_link_to_search_for_a_plant() {
        plantController.plantInformationForm(null);
    }
    @Then("I am taken to a page where I can search for plant information by name")
    public void i_am_taken_to_a_page_where_i_can_search_for_plant_information_by_name() {
        assertFalse(plantController.plantInformationForm(null).contains("plantSearch"));
    }
    @Given("I am on the plant search page")
    public void i_am_on_the_plant_search_page() {
        plantController.plantInformationForm(null);
    }

    @When("I search a plant name {string}")
    public void i_search_a_plant_name(String name) throws Exception {
        String plantInfo = "{\"plants\":[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]}";
        JsonNode plantInfoJson = objectMapper.readTree(plantInfo);
        when(wikidataService.getPlantInfo(name)).thenReturn(plantInfoJson);

        autocompleteData = wikidataAPIController.searchPlantAutocomplete(name).getBody();
        System.out.println(autocompleteData.get("results"));

    }

    @Then("different autocomplete suggestions pop up matching my search")
    public void different_autocomplete_suggestions_pop_up_matching_my_search() {
        assertFalse(autocompleteData.get("results").isEmpty());
    }

    @Then("meaningful information about that plant pops up")
    public void meaningful_information_about_that_plant_pops_up() {
        assertFalse(autocompleteData.get("results").isEmpty());
        autocompleteData.get("results").forEach(result -> {
            assertTrue(result.has("label"));
            assertTrue(result.has("description"));
            assertTrue(result.has("id"));
            assertTrue(result.has("image"));
        });
    }

}
