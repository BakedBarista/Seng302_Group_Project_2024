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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;

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

    private Authentication authentication;
    private Model model;
    private GardenUser sender;
    private GardenUser receiver;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        model = mock(Model.class);
        authentication = mock(Authentication.class);
        session = new MockHttpSession();
        session.setAttribute("submissionToken", "token");

        sender = new GardenUser();
        sender.setFname("jane");
        sender.setEmail("janeintegration@email.com");
        sender.setPassword("password");
        sender.setDateOfBirth(LocalDate.now());
        gardenUserService.addUser(sender);

        receiver = new GardenUser();
        receiver.setFname("john");
        receiver.setEmail("johnintegration@email.com");
        receiver.setPassword("password");
        receiver.setDateOfBirth(LocalDate.now());
        gardenUserService.addUser(receiver);

        Friends friendship = new Friends(sender, receiver, Friends.Status.ACCEPTED);
        friendService.save(friendship);
    }

    @Test
    void givenHaveFriend_whenSendAMessageToFriend_thenSaveMessageBetweenFriendAndMyself() {
        String message = "Hello";
        MessageDTO messageDTO = new MessageDTO(message, "token");

        Mockito.when(authentication.getPrincipal()).thenReturn(sender.getId());

        String redirect = messageController.sendMessage(receiver.getId(), messageDTO, authentication, model, session);
        List<Message> savedMessages = messageRepository.findMessagesBetweenUsers(sender.getId(), receiver.getId());

        // verify return and model
        Assertions.assertEquals("users/message", redirect);
        Mockito.verify(model).addAttribute(Mockito.eq("sentToUser"), ArgumentCaptor.forClass(GardenUser.class).capture());

        // verify message is saved to repository
        Assertions.assertTrue(savedMessages.stream().map(Message::getMessageContent).toList().contains(message));
    }

    @Test
    void givenNoToken_whenSendMessageToFriend_thenDontSaveMessageBetweenFriendAndMyself() {
        String message = "Hello";
        MessageDTO messageDTO = new MessageDTO(message, "token");
        session.setAttribute("submissionToken", null);

        Mockito.when(authentication.getPrincipal()).thenReturn(sender.getId());

        String redirect = messageController.sendMessage(receiver.getId(), messageDTO, authentication, model, session);
        List<Message> savedMessages = messageRepository.findMessagesBetweenUsers(sender.getId(), receiver.getId());

        // verify return and model
        Assertions.assertEquals("users/message", redirect);
        Mockito.verify(model).addAttribute(Mockito.eq("sentToUser"), ArgumentCaptor.forClass(GardenUser.class).capture());

        // verify message is saved to repository
        Assertions.assertFalse(savedMessages.stream().map(Message::getMessageContent).toList().contains(message));
    }

    @Test
    void givenOldToken_whenSendMessageToFriend_thenDontSaveMessageBetweenFriendAndMyself() {
        String message = "Hello";
        MessageDTO messageDTO = new MessageDTO(message, "token");
        session.setAttribute("submissionToken", "tokenOld");

        Mockito.when(authentication.getPrincipal()).thenReturn(sender.getId());

        String redirect = messageController.sendMessage(receiver.getId(), messageDTO, authentication, model, session);
        List<Message> savedMessages = messageRepository.findMessagesBetweenUsers(sender.getId(), receiver.getId());

        // verify return and model
        Assertions.assertEquals("users/message", redirect);
        Mockito.verify(model).addAttribute(Mockito.eq("sentToUser"), ArgumentCaptor.forClass(GardenUser.class).capture());

        // verify message is saved to repository
        Assertions.assertFalse(savedMessages.stream().map(Message::getMessageContent).toList().contains(message));
    }
}
