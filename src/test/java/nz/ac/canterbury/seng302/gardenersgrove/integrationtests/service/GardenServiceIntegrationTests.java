package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@DataJpaTest
@Import(GardenUserService.class)
public class GardenServiceIntegrationTests {

    @Autowired
    private GardenUserRepository gardenUserRepository;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private PlantRepository plantRepository;

    private GardenUserService gardenUserService;

    private GardenService gardenService;

    private PlantService plantService;

    private Garden garden;

    private Plant plant;

    @BeforeEach
    public void setUp() {
        gardenUserService = new GardenUserService(gardenUserRepository);
        gardenService = new GardenService(gardenRepository);
        plantService = new PlantService(plantRepository, gardenRepository);

        GardenUser gardenUser = new GardenUser("John", "Doe", "john.doe@gmail.com", "password", "01/01/2000");
        gardenUserService.addUser(gardenUser);

        garden = new Garden("Garden Name", "Garden Location", "100", "Garden Description");
        garden.setPublic(true);
        garden.setOwner(gardenUser);
        gardenService.addGarden(garden);

        plant = new Plant("Plant Name", "1", "Plant Description", "02/01/2000");
        plantService.addPlant(plant, garden.getId());
    }

    @Test
    public void testWhenISearchGardenName_AndThereIsAGardenWithGardenName_ReturnListWithGarden() {
        String search = "Garden Name";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertTrue(gardens.contains(garden));
    }

    @Test
    public void testWhenISearchPlantName_AndThereIsAGardenWithAPlantWithPlantName_ReturnListWithGarden() {
        String search = "Plant Name";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertTrue(gardens.contains(garden));
    }

    @Test
    public void testWhenISearchGardenNameFullCaps_AndThereIsAGardenWithGardenName_ReturnListWithGarden() {
        String search = "GARDEN NAME";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertTrue(gardens.contains(garden));
    }

    @Test
    public void testWhenISearchPlantNameFullCaps_AndThereIsAGardenWithAPlantWithPlantName_ReturnListWithGarden() {
        String search = "PLANT NAME";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertTrue(gardens.contains(garden));
    }

    @Test
    public void testWhenISearchGardenNameSubstring_AndThereIsAGardenWithGardenName_ReturnListWithGarden() {
        String search = "Garden Na";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertTrue(gardens.contains(garden));
    }

    @Test
    public void testWhenISearchPlantNameSubstring_AndThereIsAGardenWithPlantWithPlantName_ReturnListWithGarden() {
        String search = "Plant Na";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertTrue(gardens.contains(garden));
    }

    @Test
    public void testWhenISearchARandomString_ThatIsNotASubstringOfAnyGardenNorPlant_ReturnAnEmptyList() {
        String search = "dfiuhgfidufhghughufkcvhjbkvcjhbhjvcbjhfhjksdfhjgksdhjkgdfsgdhfghudsgdfuhigdg";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertTrue(gardens.isEmpty());
    }

    @Test
    public void testWhenISearchGardenName_AndThereIsAPrivateGardenWithGardenName_ReturnListWithoutGarden() {
        garden.setPublic(false);

        String search = "Garden Name";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertFalse(gardens.contains(garden));
    }

    @Test
    public void testWhenISearchPlantName_AndThereIsAPrivateGardenWithAPlantWithPlantName_ReturnListWithoutGarden() {
        garden.setPublic(false);

        String search = "Plant Name";
        List<Garden> gardens = gardenService.findAllThatContainQuery(search);

        Assertions.assertFalse(gardens.contains(garden));
    }

    @Test
    public void testWhenISearchEmptyString_ReturnAllPublicGardens() {
        List<Garden> blankQueryGardenList = gardenService.findAllThatContainQuery("");
        List<Garden> allGardens = gardenService.getPublicGardens();

        Assertions.assertEquals(allGardens, blankQueryGardenList);
    }
}
