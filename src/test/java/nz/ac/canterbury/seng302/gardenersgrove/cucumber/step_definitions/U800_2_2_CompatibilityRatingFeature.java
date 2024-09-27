package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.SuggestedUserController;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800_2_2_CompatibilityRatingFeature {
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private SuggestedUserController suggestedUserController;

    private Authentication authentication;
    private Model model;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private String userList;

    @Before
    public void setup() {
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
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
}
