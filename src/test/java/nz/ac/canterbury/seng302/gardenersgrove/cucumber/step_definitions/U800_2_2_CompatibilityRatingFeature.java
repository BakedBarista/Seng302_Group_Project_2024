package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.core.type.TypeReference;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.SuggestedUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800_2_2_CompatibilityRatingFeature {
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private SuggestedUserController suggestedUserController;

    @Autowired
    private GardenUserRepository gardenUserRepository;

    @Autowired
    private GardenRepository gardenRepository;
    private Authentication authentication;
    private Model model;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private String userList;
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        objectMapper = new ObjectMapper();
    }

    @Given("I am on the home page")
    public void i_am_on_the_home_page() {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(request.getServletContext()).thenReturn(servletContext);

        suggestedUserController.home(authentication, model, request, response);

        ArgumentCaptor<String> userListCaptor = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(eq("userList"), userListCaptor.capture());
        userList = userListCaptor.getValue();
    }

    @Then("I can see the friendship compatibility rating between the friend candidate and me, as a percentage")
    public void i_can_see_the_friendship_compatibility_rating_between_the_friend_candidate_and_me_as_a_percentage() {
        assertTrue(userList.contains("\"compatibility\":"));
    }

    @Then("the profiles that are shown below the users who have sent me friend requests are ranked by a combination of geographic proximity, similarity of plants we grow, and similarity of age")
    public void theProfilesThatAreShownBelowTheUsersWhoHaveSentMeFriendRequestsAreRankedByACombinationOfGeographicProximitySimilarityOfPlantsWeGrowAndSimilarityOfAge() throws Exception {
        GardenUser user = new GardenUser("test", "user", "test@gmail.com", "password", LocalDate.of(1970, 10, 10));
        Garden garden = new Garden("Test Garden", "1", "test", "test suburb", "test city", "test country", "1234", 0.0, 0.0, "test description", 100D, null, null);
        gardenUserRepository.save(user);
        garden.setOwner(user);
        gardenRepository.save(garden);

        List<SuggestedUserDTO> users = objectMapper.readValue(userList, new TypeReference<List<SuggestedUserDTO>>() {});

        for (int i = 0; i < users.size() - 1; i++) {
            int currentCompatibility = users.get(i).getCompatibility();
            int nextCompatibility = users.get(i + 1).getCompatibility();
            assertTrue(currentCompatibility >= nextCompatibility);
        }

    }
}
