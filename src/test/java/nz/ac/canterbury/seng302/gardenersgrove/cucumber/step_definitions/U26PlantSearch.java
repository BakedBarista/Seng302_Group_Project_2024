package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;

public class U26PlantSearch {
    @Autowired
    private PlantController plantController;

    @Autowired
    private WikiDataAPIController wikidataAPIController;

    @Autowired
    private WikidataService wikidataService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode autocompleteData;

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
    }

    @Then("different autocomplete suggestions pop up matching my search")
    public void different_autocomplete_suggestions_pop_up_matching_my_search() {
        assertFalse(autocompleteData.get("results").isEmpty());
    }
}
