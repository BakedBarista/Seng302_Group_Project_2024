package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.ACCEPTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
class MessageControllerTest {

    private MessageController messageController;
    private MessageController messageController2;

    private Model model;

    @Autowired
    private GardenUserService gardenUserService;

    @Autowired
    private GardenUserRepository gardenUserRepository;
    private static FriendService mockedFriendService;

    private static MessageService mockedMessageService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    private static Authentication authentication;

    private static HttpSession session;

    private final Long loggedInUserId = 1L;
    private final Long requestedUserId = 2L;

    @BeforeEach
    public void setup() {
        session= new MockHttpSession();
        model = mock(Model.class);
        authentication = mock(Authentication.class);

        gardenUserService = mock(GardenUserService.class);
        mockedFriendService = mock(FriendService.class);
        mockedMessageService = mock(MessageService.class);
        messageController = new MessageController(gardenUserService, mockedFriendService, mockedMessageService);


        messageController2 = new MessageController(gardenUserService, mockedFriendService, messageService);

    }

    @Test
    void whenClickMessageButton_thenReturnMessagePage() {
        when(authentication.getPrincipal()).thenReturn(1L);
        when(mockedFriendService.getFriendship(any(), any())).thenReturn(new Friends());
        when(gardenUserService.getUserById(1L)).thenReturn(new GardenUser());

        String result = messageController.messageFriend(1L, authentication,  model,session);
        assertEquals("users/message-home", result);
    }

    @Test
    void whenClickSendMessageButton_thenReturnMessagePage() {
        Long sender = 1L;
        Long receiver = 2L;
        MessageDTO messageDTO = new MessageDTO("Hello","token");

        when(authentication.getPrincipal()).thenReturn(sender);
        when(mockedFriendService.getFriendship(any(), any())).thenReturn(new Friends());
        when(gardenUserService.getUserById(sender)).thenReturn(new GardenUser());

        String result = messageController.sendMessage(receiver, messageDTO, authentication,  model, session);
        assertEquals("users/message-home", result);
    }

    @Test
    void whenClickSendMessageButton_thenMessageSaved() {
        Long sender = 1L;
        Long receiver = 2L;
        MessageDTO messageDTO = new MessageDTO("Hello","token");
        session.setAttribute("submissionToken", "token");
        when(authentication.getPrincipal()).thenReturn(sender);
        when(mockedFriendService.getFriendship(any(), any())).thenReturn(new Friends());
        when(gardenUserService.getUserById(sender)).thenReturn(new GardenUser());

        messageController.sendMessage(receiver, messageDTO, authentication,  model,session);
        verify(mockedMessageService).sendMessage(sender, receiver, messageDTO);
    }


    @Test
    void whenMessageHomeGET_thenReturnMessageHome() {

        GardenUser user1 = new GardenUser("test", "2", "Tester@gmail.com",  "Password1!", null);
        GardenUser user2 = new GardenUser("John", "Doe", "postTester@gmail.com",  "Password1!", null);
        gardenUserRepository.save(user1);
        gardenUserRepository.save(user2);

        LocalDateTime testTime = LocalDateTime.of(2024, 9, 10, 15, 30, 0);
        Message testMessage = new Message(user1.getId(), user2.getId(), testTime, "HI");
        messageRepository.save(testMessage);
        Friends friend = new Friends(user1, user2, ACCEPTED);


        when(gardenUserService.getUserById(user2.getId())).thenReturn(user2);
        when(mockedFriendService.getFriendship(user1.getId(), user2.getId())).thenReturn(friend);
        when(authentication.getPrincipal()).thenReturn(user1.getId());

        String view = messageController2.messageHome(authentication, model, session);

        assertEquals("users/message-home", view);
    }

    @Test
    void whenMessageHomeParameterGET_thenReturnMessageHome() {

        GardenUser user1 = new GardenUser("test", "2", "Tester@gmail.com",  "Password1!", null);
        GardenUser user2 = new GardenUser("John", "Doe", "postTester@gmail.com",  "Password1!", null);
        gardenUserRepository.save(user1);
        gardenUserRepository.save(user2);

        LocalDateTime testTime = LocalDateTime.of(2024, 9, 10, 15, 30, 0);
        Message testMessage = new Message(user1.getId(), user2.getId(), testTime, "HI");
        messageRepository.save(testMessage);
        Friends friend = new Friends(user1, user2, ACCEPTED);


        when(gardenUserService.getUserById(user2.getId())).thenReturn(user2);
        when(mockedFriendService.getFriendship(user1.getId(), user2.getId())).thenReturn(friend);
        when(authentication.getPrincipal()).thenReturn(user1.getId());

        String view = messageController2.messageHomeSend(user2.getId(), authentication, model, session);

        assertEquals("users/message-home", view);
    }
}
