package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.ChatPreview;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.mock;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U800_3_7_UnreadMessageFeature {

    @Autowired
    private GardenUserService gardenUserService;
    @Autowired
    private MessageController messageController;
    @Autowired
    private MessageService messageService;
    private static Model model;
    private static Authentication authentication;

    private static Long myId;
    private static MessageDTO messageDTO;
    private static MockHttpSession session;
    private static BindingResult bindingResult;

    @BeforeAll
    public static void setup() {
        session = new MockHttpSession();
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        bindingResult = mock(BindingResult.class);
    }
    @Given("I, {string}, have a message from {string}")
    public void i_have_a_message_from(String myName, String senderName) {
        Long senderId = gardenUserService.getUserByEmail(senderName + "cucumber@email.com").getId();
        myId = gardenUserService.getUserByEmail(myName + "cucumber@email.com").getId();
        String token = UUID.randomUUID().toString();
        messageDTO = new MessageDTO("hi", token);
        session.setAttribute("submissionToken", token);

        messageService.setReadTime(myId, senderId);

        Mockito.when(authentication.getPrincipal()).thenReturn(senderId);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        messageController.sendMessage(myId, messageDTO, bindingResult, authentication, model, session, null);
    }

    @Then("I can see an unread message badge with {string} next to {string}")
    public void i_can_see_an_unread_message_badge_with_next_to(String badgeNum, String senderName) {
        Long badgeInt = (long) Integer.parseInt(badgeNum);
        Long senderId = gardenUserService.getUserByEmail(senderName + "cucumber@email.com").getId();

        List<Message> allMessages = messageService.findAllRecentChats(myId);
        Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, myId);
        Map<GardenUser, ChatPreview> previewMap = messageService.convertToPreview(myId, recentMessagesMap);
        GardenUser user = previewMap.keySet().stream()
                .filter(gardenUser -> Objects.equals(gardenUser.getId(), senderId)).findFirst().get();

        Assertions.assertEquals(badgeInt, previewMap.get(user).getUnreadMessages());
    }

    @Then("I do not see an unread message badge next to {string}")
    public void i_do_not_see_an_unread_message_badge_next_to(String senderName) {
        Long badgeInt = 0L;
        Long senderId = gardenUserService.getUserByEmail(senderName + "cucumber@email.com").getId();
        List<Message> allMessages = messageService.findAllRecentChats(myId);
        Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, myId);
        Map<GardenUser, ChatPreview> previewMap = messageService.convertToPreview(myId, recentMessagesMap);

        GardenUser user = previewMap.keySet().stream()
                .filter(gardenUser -> Objects.equals(gardenUser.getId(), senderId)).findFirst().get();

        Assertions.assertEquals(badgeInt, previewMap.get(user).getUnreadMessages());
    }
}
