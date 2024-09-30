package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import nz.ac.canterbury.seng302.gardenersgrove.service.ExternalServiceException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.PlantController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.PlantInfoDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
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
    public void i_click_on_a_link_to_search_for_a_plant() throws ExternalServiceException {
        plantController.plantInformationForm(null, null);
    }
    @Then("I am taken to a page where I can search for plant information by name")
    public void i_am_taken_to_a_page_where_i_can_search_for_plant_information_by_name() throws ExternalServiceException {
        assertFalse(plantController.plantInformationForm(null, null).contains("plantSearch"));
    }
    @Given("I am on the plant search page")
    public void i_am_on_the_plant_search_page() throws ExternalServiceException {
        plantController.plantInformationForm(null, null);
    }

    @When("I search a plant name {string}")
    public void i_search_a_plant_name(String name) throws Exception {
        String plantInfo = "[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]";
        List<PlantInfoDTO> plantInfoList = objectMapper.readValue(plantInfo, new TypeReference<List<PlantInfoDTO>>() {
        });
        when(wikidataService.getPlantInfo(name)).thenReturn(plantInfoList);

        autocompleteData = wikidataAPIController.searchPlantAutocomplete(name).join().getBody();
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

    @When("I search a plant name {string} with no autocomplete")
    public void i_search_a_plant_name_with_no_autocomplete(String plantName) throws Exception {
        when(wikidataService.getPlantInfo(plantName)).thenReturn(List.of());

        autocompleteData = wikidataAPIController.searchPlantAutocomplete(plantName).join().getBody();
    }

    @Then("plants with similar name {string} pops up")
    public void plants_with_similar_name_pops_up(String similarName) {
        boolean hasSimilarName = false;
        for (JsonNode result : autocompleteData.get("results")) {
            if (result.get("label").asText().equals(similarName)) {
                hasSimilarName = true;
                break;
            }
        }

        assertEquals(similarName, autocompleteData.get("results").get(0).get("label").toString().replace("\"", ""));
        assertTrue(hasSimilarName);
        assertTrue(autocompleteData.get("results").get(0).has("description"));
    }
}
