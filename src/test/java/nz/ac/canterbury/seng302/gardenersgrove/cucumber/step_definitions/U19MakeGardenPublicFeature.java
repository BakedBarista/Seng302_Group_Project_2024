package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;

import java.util.Optional;

import static org.mockito.Mockito.*;


public class U19MakeGardenPublicFeature {

   private static GardenService gardenService;
   private static GardenRepository gardenRepository;

    private static GardenUser gardenUser;
    private static Garden garden;

    @BeforeAll
    public static void beforeAll() {
        gardenRepository = mock(GardenRepository.class);
        gardenService = new GardenService(gardenRepository);
        gardenUser = new GardenUser();
        gardenUser.setId(1L);
        gardenUser.setFname("testUser");

        Garden garden = new Garden("Test Garden","1","test","test suburb","test city","test country","1234",0.0,0.0,"100","test description");
        garden.setId(1L);
        garden.setOwner(gardenUser);

        when(gardenRepository.findById(1L)).thenReturn(Optional.of(garden));

        when(gardenRepository.save(any(Garden.class))).thenReturn(garden);
    }

    @Given("I have a garden")
    public void i_have_a_garden() {

        Garden savedGarden = gardenService.getGardenById(1L).orElseThrow(() -> new RuntimeException("Garden not found"));
        assert savedGarden.getName().equals("Test Garden");
    }


    @When( "I make the garden public")
    public void i_make_the_garden_public() {
        Garden savedGarden = gardenService.getGardenById(1L).orElseThrow(() -> new RuntimeException("Garden not found"));
        savedGarden.setPublic(true);
        gardenService.addGarden(savedGarden);
    }
    @Then( "The garden is public")
    public void the_garden_is_public() {
        Garden savedGarden = gardenService.getGardenById(1L).orElseThrow(() -> new RuntimeException("Garden not found"));
        assert savedGarden.getIsPublic();
    }
}
