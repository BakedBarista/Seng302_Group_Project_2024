package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.websockets.MessageWebSocketHandler;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800_3_1_MessageFeature {
    @Autowired
    private GardenUserService gardenUserService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageController messageController;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageWebSocketHandler messageWebSocketHandler;

    private static Model model;
    private String result;
    private static Authentication authentication;

    private static GardenUser user = new GardenUser();
    public static Long myId;
    public static Long receiverId;
    private static MessageDTO messageDTO;
    private static MockHttpSession session;
    private static int currentMessages;
    private static BindingResult bindingResult;

    @BeforeAll
    public static void setup() {
        session = new MockHttpSession();
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        bindingResult = mock(BindingResult.class);
        user.setId(1L);
    }

    @Given("I am a user named {string}")
    public void i_am_a_user_named(String name) {
        boolean userExists = gardenUserService.getUserByEmail(name + "cucumber@email.com") != null;

        if (!userExists) {
            GardenUser user = new GardenUser();
            user.setFname(name);
            user.setEmail(name + "cucumber@email.com");
            user.setPassword("password");
            user.setDateOfBirth(LocalDate.now());

            GardenUser newUser = gardenUserService.addUser(user);
            myId = newUser.getId();
        }
    }

    @Given("There is a user named {string}")
    public void there_is_a_user_named(String name) {
        boolean userExists = gardenUserService.getUserByEmail(name + "cucumber@email.com") != null;

        if (!userExists) {
            GardenUser user = new GardenUser();
            user.setFname(name);
            user.setEmail(name + "cucumber@email.com");
            user.setPassword("password");
            user.setDateOfBirth(LocalDate.now());

            gardenUserService.addUser(user);
        }
    }

    @Given("{string} and {string} are friends")
    public void and_are_friends(String username1, String username2) {
        GardenUser user1 = gardenUserService.getUserByEmail(username1 + "cucumber@email.com");
        GardenUser user2 = gardenUserService.getUserByEmail(username2 + "cucumber@email.com");

        Friends friendship = new Friends(user1, user2, Friends.Status.ACCEPTED);
        friendService.save(friendship);
    }

    @Given("I am viewing my friends list")
    public void i_am_viewing_my_friends_list() {
        messageController = new MessageController(gardenUserService, friendService, messageService, messageWebSocketHandler);
    }

    @When("I click the message button next to my friend {string}")
    public void i_click_the_message_button_next_to_my_friend(String friendName) {
        Mockito.when(authentication.getPrincipal()).thenReturn(myId);
        Long friendsId = gardenUserService.getUserByEmail(friendName + "cucumber@email.com").getId();

        result = messageController.messageFriend(friendsId, authentication, model, session);
    }

    @Then("I am taken to the message page")
    public void i_am_taken_to_the_message_page() {
        assertEquals("users/message-home", result);
    }

    // AC5
    @Given("I am on a direct messaging page for my friend {string}")
    public void i_am_on_a_direct_messaging_page_for_my_friend(String friendName) {
        receiverId = gardenUserService.getUserByEmail(friendName + "cucumber@email.com").getId();
        currentMessages = messageRepository.findMessagesBetweenUsers(myId, receiverId).size();
    }

    @When("I have typed a text-based message {string}")
    public void i_have_typed_a_text_based_message(String messageContent) {
        String token = UUID.randomUUID().toString();
        messageDTO = new MessageDTO(messageContent, token);
        session.setAttribute("submissionToken", token);
    }

    @When("They have typed a text-based message {string}")
    public void they_have_typed_a_text_based_message(String messageContent) {
        String token = UUID.randomUUID().toString();
        messageDTO = new MessageDTO(messageContent, token);
        session.setAttribute("submissionToken", token);
    }

    @When("They press Send")
    public void they_press_send() {
        Mockito.when(authentication.getPrincipal()).thenReturn(receiverId);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        result = messageController.sendMessage(myId, messageDTO, bindingResult, authentication, model, session, null);
    }

    @When("I press Send")
    public void i_press_send() {
        Mockito.when(authentication.getPrincipal()).thenReturn(myId);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        result = messageController.sendMessage(receiverId, messageDTO, bindingResult, authentication, model, session, null);
    }

    @Then("The message is sent to that friend.")
    public void the_message_is_sent_to_that_friend() {
        Message message = messageRepository.findMessagesBetweenUsers(myId, receiverId).get(0);
        Assertions.assertEquals(messageDTO.getMessage(), message.getMessageContent());
        Assertions.assertEquals(myId, message.getSender());
        Assertions.assertEquals(receiverId, message.getReceiver());
        // Don't test timestamp as tested in unit and integration
    }

    @And("The messages are displayed in chronological order")
    public void the_messages_are_displayed_in_chronological_order() {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(myId, receiverId);

        Assertions.assertEquals(currentMessages + 4, messages.size());
        for (int i = 1; i < messages.size(); i++) {
            Assertions.assertTrue(messages.get(i).getTimestamp().isAfter(messages.get(i - 1).getTimestamp()));
        }
    }

    @Then("The {string} existing chats are displayed on the side in chronological order")
    public void the_existing_chats_are_displayed_on_the_side_in_chronological_order(String numOfChats) {
        int expectedChatCount = Integer.parseInt(numOfChats);

        result = messageController.messageHomeSend(receiverId, authentication, model, session);
        List<Message> allMessages = messageService.findAllRecentChats(myId);
        Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, myId);
        Map<GardenUser, String> recentChats = messageService.convertToPreview(recentMessagesMap);

        assertEquals(expectedChatCount, recentChats.size());
        assertEquals("users/message-home", result);
    }

    @When("I send invalid message")
    public void i_send_invalid_message() {
        Mockito.when(authentication.getPrincipal()).thenReturn(myId);
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);
        result = messageController.sendMessage(receiverId, messageDTO, bindingResult, authentication, model, session, null);
    }

    @Then("The message is not sent.")
    public void the_message_is_not_sent() {
        List<Message> message = messageRepository.findMessagesBetweenUsers(myId, receiverId);
        Assertions.assertEquals(1, message.size());
    }

}
