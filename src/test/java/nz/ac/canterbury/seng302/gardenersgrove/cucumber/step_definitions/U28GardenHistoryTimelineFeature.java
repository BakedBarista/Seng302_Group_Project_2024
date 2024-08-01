package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.GardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.GardenHistoryItemDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;

public class U28GardenHistoryTimelineFeature {
    @Autowired
    private GardenService gardenService;

    @Autowired
    private GardenController gardenController;

    private static Authentication authentication;
    private static Model model;
    private static Garden garden;

    @BeforeAll
    public static void setup() {
        authentication = mock(Authentication.class);
        model = mock(Model.class);
    }

    @Given("I am on the garden detials page for {string}")
    public void i_am_on_the_garden_detials_page_for(String name) {
        // Assume that the most recently created garden is the one we're after
        // It is difficult to get a garden by name as we don't have a method to do so
        Long gardenId = U28GardenHistoryPlantDateFeature.gardenId;
        Long userId = U28GardenHistoryPlantDateFeature.user.getId();
        when(authentication.getPrincipal()).thenReturn(userId);

        garden = gardenService.getGardenById(gardenId).get();
        assertEquals(name, garden.getName());
    }

    @When("I view my gardens timeline")
    @Transactional
    public void i_view_my_gardens_timeline() {
        gardenController.gardenHistory(authentication, garden.getId(), model);
    }

    @Then("there is a record of plant history for {string}")
    public void there_is_a_record_of_plant_history_for(String string) {
        verify(model).addAttribute(eq("history"),
                assertArg((SortedMap<LocalDate, List<GardenHistoryItemDTO>> history) -> {
            assertNotNull(history);
        }));
    }
}
