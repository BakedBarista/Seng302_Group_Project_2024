package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.ACCEPTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")

class MessageControllerTest {

    private MessageController messageController;
    private Model model;

    @Autowired
    private GardenUserService gardenUserService;
    private static FriendService mockedFriendService;
    private static MessageService mockedMessageService;

    private static Authentication authentication;

    private static HttpSession session;

    private final Long loggedInUserId = 1L;
    private final Long requestedUserId = 2L;

    @BeforeEach
    public void setup() {
        gardenUserService = mock(GardenUserService.class);
        mockedFriendService = mock(FriendService.class);
        mockedMessageService = mock(MessageService.class);
        messageController = new MessageController(gardenUserService, mockedFriendService, mockedMessageService);
        session= new MockHttpSession();
        model = mock(Model.class);
        authentication = mock(Authentication.class);
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
        GardenUser user1 = new GardenUser();
        user1.setId(loggedInUserId);
        GardenUser user2 = new GardenUser();
        user2.setId(requestedUserId);

        List<Message> allMessages = List.of(new Message(loggedInUserId, requestedUserId, LocalDateTime.now(), "Hey"));
        Map<Long, Message> recentMessagesMap = Map.of(requestedUserId, allMessages.get(0));
        Map<GardenUser, String> recentChats = Map.of(user2, "Hey");
        Friends friend = new Friends(user1, user2, ACCEPTED);

        when(mockedMessageService.findAllRecentChats(loggedInUserId)).thenReturn(allMessages);
        when(mockedMessageService.getLatestMessages(allMessages, loggedInUserId)).thenReturn(recentMessagesMap);
        when(mockedMessageService.convertToPreview(recentMessagesMap)).thenReturn(recentChats);
        when(mockedMessageService.getActiveChat(recentMessagesMap)).thenReturn(requestedUserId);
        when(gardenUserService.getUserById(requestedUserId)).thenReturn(user1);
        when(mockedFriendService.getFriendship(loggedInUserId, requestedUserId)).thenReturn(friend);

        // Act
        String view = messageController.messageHome(authentication, model, session);

        // Assert
        assertEquals("users/message-home", view);
        verify(model).addAttribute(eq("recentChats"), eq(recentChats));
        verify(model).addAttribute(eq("sentToUser"), any(GardenUser.class));
        verify(model).addAttribute(eq("activeChat"), eq(requestedUserId));
        verify(model).addAttribute(eq("messagesMap"), any());
    }
}
