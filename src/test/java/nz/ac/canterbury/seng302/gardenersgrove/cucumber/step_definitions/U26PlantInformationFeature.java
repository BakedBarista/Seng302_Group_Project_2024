package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.service.ExternalServiceException;
import nz.ac.canterbury.seng302.gardenersgrove.service.LocalPlantDataService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.mockito.Mockito;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class U26PlantInformationFeature {

    private WikiDataAPIController wikiDataAPIController;
    private WikidataService wikidataService;
    private JsonNode response;
    private ObjectMapper objectMapper;
    private LocalPlantDataService localPlantDataService;

    @Given("the plant information service is not available")
    public void thePlantInformationServiceIsNotAvailable() throws ExternalServiceException {
        wikidataService = mock(WikidataService.class);
        objectMapper = new ObjectMapper();
        wikiDataAPIController = new WikiDataAPIController(wikidataService,localPlantDataService,objectMapper);
        when(wikidataService.getPlantInfo(Mockito.anyString())).thenThrow(new ExternalServiceException("Service unavailable"));
    }

    @When("I search for a plant named {string}")
    public void iSearchForAPlantNamed(String plantName) {
        response = wikiDataAPIController.searchPlantAutocomplete(plantName).join().getBody();
    }

    @Then("I'm prompted with error message {string}")
    public void iMPromptedWithErrorMessage(String expectedMessage) throws Exception {
        String expected = "{\"error\":\"" + expectedMessage + "\"}";
        JsonNode expectedJson = objectMapper.readTree(expected);
        assertEquals(expectedJson, response);
    }
}
