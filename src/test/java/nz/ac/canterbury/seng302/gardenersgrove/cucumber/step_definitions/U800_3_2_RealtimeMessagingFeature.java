package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800_3_2_RealtimeMessagingFeature {
    @Autowired
    private GardenUserRepository userRepository;
    @Autowired
    private MessageController messageController;
    @Autowired
    private WebSocketHandler wsHandler;

    private Authentication authentication;
    private Model model;
    private HttpSession session;
    private WebSocketSession wsSession;
    private Principal principal;

    private GardenUser user;
    private GardenUser friend;

    @Before
    public void setup() {
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        session = mock(HttpSession.class);
        wsSession = mock(WebSocketSession.class);
        principal = mock(Principal.class);
    }

    @Given("I am on the direct-messaging page for {string}")
    public void i_am_on_the_direct_messaging_page_for(String name) throws Exception {
        user = userRepository.findById(U800MessageFeature.myId).get();
        friend = userRepository.findBySearchNoLname(name, user.getId()).get(0);
        when(authentication.getPrincipal()).thenReturn(user.getId());
        when(wsSession.isOpen()).thenReturn(true);
        when(wsSession.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(user.getId().toString());

        messageController.messageFriend(friend.getId(), authentication, model, session);

        wsHandler.handleMessage(wsSession, new TextMessage("{\"type\":\"subscribe\"}"));
    }

    @When("I send a new message")
    public void i_send_a_new_message() throws Exception {
        wsHandler.handleMessage(wsSession, sendMessageTo(friend));
    }

    @When("{string} sends me a message")
    public void sends_me_a_message(String string) throws Exception {
        WebSocketSession friendSession = mock(WebSocketSession.class);
        Principal friendPrincipal = mock(Principal.class);
        when(friendSession.getPrincipal()).thenReturn(friendPrincipal);
        when(friendPrincipal.getName()).thenReturn(friend.getId().toString());

        wsHandler.handleMessage(friendSession, sendMessageTo(user));
    }

    private TextMessage sendMessageTo(GardenUser user) {
        return new TextMessage("{\"type\":\"sendMessage\",\"receiver\":" + user.getId() + ",\"message\":\"test\"}");
    }

    @Then("it shows up without me having to reload the page")
    public void it_shows_up_without_me_having_to_reload_the_page() throws IOException {
        verify(wsSession, times(2)).sendMessage(assertArg((TextMessage message) -> {
            assertTrue(message.getPayload().contains("updateMessages"));
        }));
    }
}
