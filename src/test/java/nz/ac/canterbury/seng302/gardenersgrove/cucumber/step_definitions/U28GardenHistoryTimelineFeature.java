//package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;
//
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
//import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantHistoryItem;
//import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
//import nz.ac.canterbury.seng302.gardenersgrove.service.PlantHistoryService;
//import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
//import org.junit.jupiter.api.Assertions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//public class U28GardenHistoryTimelineFeature {
//    @Autowired
//    private PlantService plantService;
//    @Autowired
//    private PlantHistoryService plantHistoryService;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private CustomAuthenticationProvider customAuthenticationProvider;
//
//    private String plantDescription = "plant description";
//
//    private MockHttpServletResponse getPlantDetails(Long gardenId, Long plantId) throws Exception {
//        Authentication authentication = new UsernamePasswordAuthenticationToken("jan.doe@gmail.com", "password");
//        Authentication authenticatedToken = customAuthenticationProvider.authenticate(authentication);
//        SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
//
//        return mockMvc.perform(get("/gardens/" + gardenId + "/plants/" + plantId))
//                .andReturn()
//                .getResponse();
//    }
//
//    @Given("I view my plant")
//    public void i_view_my_plant() throws Exception {
//        var result = getPlantDetails(1L, 1L).getContentAsString();
//        Assertions.assertFalse(result.isEmpty(), "Result should not be empty");
//    }
//    @Given("the plant has records with images and descriptions")
//    @Transactional
//    public void the_plant_has_records_with_images_and_descriptions() throws Exception {
//        Plant plant = plantService.getPlantById(1L).get();
//        String contentType = "image/png";
//        byte[] image = new byte[] { 1, 2, 3, 4, 5 };
//
//        plantHistoryService.addHistoryItem(plant, contentType, image, plantDescription);
//
//        System.out.println(plantHistoryService.getPlantHistory(plant));
//        List<PlantHistoryItem> history = plantHistoryService.getPlantHistory(plant);
//        Assertions.assertEquals(history.get(0).getDescription(), plantDescription);
//        Assertions.assertNotNull(history.get(0).getImage());
//    }
//
//    @When("I view my plant's timeline")
//    public void i_view_my_plant_s_timeline() throws Exception {
//        var result = getPlantDetails(1L, 1L).getContentAsString();
//        Assertions.assertFalse(result.isEmpty(), "Result should not be empty");
//    }
//    @Then("I am presented with a timeline of specific plant records")
//    public void i_am_presented_with_a_timeline_of_specific_plant_records() throws Exception {
//        var result = getPlantDetails(1L, 1L).getContentAsString();
//        Assertions.assertFalse(result.isEmpty(), "Result should not be empty");
//        Assertions.assertTrue(result.contains(plantDescription));
//    }
//    @Then("each record includes dates, images, and descriptions of the plant at these dates")
//    public void each_record_includes_dates_images_and_descriptions_of_the_plant_at_these_dates() throws Exception {
//        var result = getPlantDetails(1L, 1L).getContentAsString();
//        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//
//        Assertions.assertFalse(result.isEmpty(), "Result should not be empty");
//        Assertions.assertTrue(result.contains(plantDescription), "Plant Description");
//        Assertions.assertTrue(result.contains(today), "History Date of today");
//        Assertions.assertTrue(result.contains("/plants/1/history/1/image"), "History Date of today");
//    }
//}
