package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("SpringJavaInjectionsPointsAutowiringInspection")
@SpringBootTest
class MessageControllerTest {

    @Autowired
    MessageController messageController;

    @Autowired
    FriendService friendService;

    @Autowired
    GardenUserService gardenUserService;

    @Autowired
    MessageRepository messageRepository;

    private static Authentication authentication;
    private static Model model;
    private static GardenUser sender;
    private static GardenUser receiver;
    private static HttpSession session;
    private Boolean hasNotSetUp = true;
    private static BindingResult bindingResult;

    @BeforeAll
    static void setUp() {
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        session = new MockHttpSession();
        bindingResult = mock(BindingResult.class);
        session.setAttribute("submissionToken", "token");

        sender = new GardenUser();
        sender.setFname("jane");
        sender.setEmail("janeintegration@email.com");
        sender.setPassword("password");
        sender.setDateOfBirth(LocalDate.now());

        receiver = new GardenUser();
        receiver.setFname("john");
        receiver.setEmail("johnintegration@email.com");
        receiver.setPassword("password");
        receiver.setDateOfBirth(LocalDate.now());
    }

    @BeforeEach
    void setup() {
        if (hasNotSetUp) {
            gardenUserService.addUser(sender);
            gardenUserService.addUser(receiver);
            Friends friendship = new Friends(sender, receiver, Friends.Status.ACCEPTED);

            friendService.save(friendship);

            hasNotSetUp = false;
        }
    }

    @Test
    void givenHaveFriend_whenSendAMessageToFriend_thenSaveMessageBetweenFriendAndMyself() {
        String message = "Hello";
        MessageDTO messageDTO = new MessageDTO(message,"token");
        BindingResult bindingResult = mock(BindingResult.class);

        HttpSession session = new MockHttpSession();
        session.setAttribute("submissionToken", "token");

        Mockito.when(authentication.getPrincipal()).thenReturn(sender.getId());
        when(bindingResult.hasErrors()).thenReturn(false);

        messageController.sendMessage(receiver.getId(), messageDTO,bindingResult, authentication, model, session,null);
        String redirect = messageController.sendMessage(receiver.getId(), messageDTO, bindingResult, authentication, model, session, null);
        List<Message> savedMessages = messageRepository.findMessagesBetweenUsers(sender.getId(), receiver.getId());
        Assertions.assertEquals("users/message-home", redirect);

        // verify message is saved to repository
        Assertions.assertTrue(savedMessages.stream().map(Message::getMessageContent).toList().contains(message));
    }

    @Test
    void givenNoToken_whenSendMessageToFriend_thenDontSaveMessageBetweenFriendAndMyself() {
        String message = UUID.randomUUID().toString();
        MessageDTO messageDTO = new MessageDTO(message, "token");
        session.setAttribute("submissionToken", null);

        Mockito.when(authentication.getPrincipal()).thenReturn(sender.getId());
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        messageController.sendMessage(receiver.getId(), messageDTO, bindingResult, authentication, model, session, null);
        List<Message> savedMessages = messageRepository.findMessagesBetweenUsers(sender.getId(), receiver.getId());

        // verify message is saved to repository
        Assertions.assertFalse(savedMessages.stream().map(Message::getMessageContent).toList().contains(message));
    }

    @Test
    void givenOldToken_whenSendMessageToFriend_thenDontSaveMessageBetweenFriendAndMyself() {
        String message = UUID.randomUUID().toString();
        MessageDTO messageDTO = new MessageDTO(message, "token");
        session.setAttribute("submissionToken", "tokenOld");

        Mockito.when(authentication.getPrincipal()).thenReturn(sender.getId());
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        messageController.sendMessage(receiver.getId(), messageDTO, bindingResult, authentication, model, session, null);
        List<Message> savedMessages = messageRepository.findMessagesBetweenUsers(sender.getId(), receiver.getId());

        // verify message is saved to repository
        Assertions.assertFalse(savedMessages.stream().map(Message::getMessageContent).toList().contains(message));
    }
}
