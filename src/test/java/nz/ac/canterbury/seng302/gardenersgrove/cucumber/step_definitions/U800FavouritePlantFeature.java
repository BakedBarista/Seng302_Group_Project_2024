package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.api.DeleteFavouritePlantController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.FavouritePlantsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800FavouritePlantFeature {

    @Autowired
    private GardenUserService gardenUserService;

    @Autowired
    private PlantService plantService;

    @Autowired
    private FavouritePlantsController favouritePlantsController;

    @Autowired
    private DeleteFavouritePlantController deleteFavouritePlantController;

    private Authentication authentication;

    private GardenUser user;

    @Given("I have setup favourite plant feature")
    public void i_have_setup() {
        authentication = Mockito.mock(Authentication.class);

        user = gardenUserService.getUserByEmail("john.doe@favouriteplants.com");
        if (user == null) {
            user = new GardenUser("john", "doe", "john.doe@favouriteplants.com",
                    "password", LocalDate.of(1999, 1, 1));
            user = gardenUserService.addUser(user);
        }

        Mockito.when(authentication.getPrincipal()).thenReturn(user.getId());

        user.getFavouritePlants().forEach(plant ->
                deleteFavouritePlantController.deleteFavouritePlant(Map.of("plantId", plant.getId()), authentication)
        );
    }

    @Then("I can see an editable section called “My Favourite Plants” where I can showcase my three favourite public plants that are mine")
    public void iCanSeeAnEditableSectionCalledMyFavouritePlantsWhereICanShowcaseMyThreeFavouritePublicPlantsThatAreMine() {
        Set<Plant> favouritePlants = new HashSet<>();
        Plant plant1 = new Plant();
        Plant plant2 = new Plant();
        Plant plant3 = new Plant();
        favouritePlants.add(plant1);
        favouritePlants.add(plant2);
        favouritePlants.add(plant3);
        user.setFavouritePlants(favouritePlants);
        assertEquals(user.getFavouritePlants(), favouritePlants);
    }

    @When("I select {string} from the list of public plants")
    public void iSelectAPlantFromTheListOfPublicPlants(String plantName) {
        Plant plant = new Plant(plantName, "1", "Green", LocalDate.now());
        plant = plantService.save(plant);

        List<Long> idList = new ArrayList<>(gardenUserService.getUserById(user.getId()).getFavouritePlants()
                .stream()
                .map(Plant::getId)
                .toList());
        idList.add(plant.getId());
        Map<String, List<Long>> request = Map.of("ids", idList);
        favouritePlantsController.updateFavouritePlants(request, authentication);
    }

    @Then("{string} is favourited")
    public void thePlantIsDisplayed(String plantName) {
        Set<Plant> favouritePlants = gardenUserService.getUserById(user.getId()).getFavouritePlants();
        assertEquals(1, favouritePlants.size());
        assertTrue(favouritePlants.stream().anyMatch(plant -> plant.getName().equals(plantName)));
    }

    @When("I click the delete button on {string}")
    public void i_click_the_delete_button_on(String plantName) {
        Long plantId = gardenUserService.getUserById(user.getId()).getFavouritePlants()
                .stream()
                .filter(plant -> plant.getName().equals(plantName))
                .findFirst()
                .get()
                .getId();

        Map<String, Long> request = Map.of("plantId", plantId);

        deleteFavouritePlantController.deleteFavouritePlant(request, authentication);
    }

    @Then("I no longer have {string} favourited")
    public void i_no_longer_have_favourited(String plantName) {
        Set<Plant> favouritePlants = gardenUserService.getUserById(user.getId()).getFavouritePlants();
        assertFalse(favouritePlants.stream().anyMatch(plant -> plant.getName().equals(plantName)));
    }
}
