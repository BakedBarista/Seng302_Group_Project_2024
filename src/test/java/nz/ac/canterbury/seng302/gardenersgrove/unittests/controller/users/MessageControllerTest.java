package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")

class MessageControllerTest {

    private MessageController messageController;
    private Model model;

    @Autowired
    private GardenUserService gardenUserService;
    private static FriendService mockedFriendService;
    private static MessageService mockedMessageService;

    private static Authentication authentication;
    private static BindingResult bindingResult;


    @BeforeEach
    public void setup() {
        gardenUserService = mock(GardenUserService.class);
        mockedFriendService = mock(FriendService.class);
        mockedMessageService = mock(MessageService.class);
        bindingResult = mock(BindingResult.class);

        messageController = new MessageController(gardenUserService, mockedFriendService, mockedMessageService);

        model = mock(Model.class);
        authentication = mock(Authentication.class);
    }

    @Test
    void whenClickMessageButton_thenReturnMessagePage() {
        Mockito.when(authentication.getPrincipal()).thenReturn(1L);
        Mockito.when(mockedFriendService.getFriendship(any(), any())).thenReturn(new Friends());
        Mockito.when(gardenUserService.getUserById(1L)).thenReturn(new GardenUser());

        String result = messageController.messageFriend(1L, authentication,  model);
        Assertions.assertEquals("users/message", result);
    }

    @Test
    void whenClickSendMessageButton_thenReturnMessagePage() {
        Long sender = 1L;
        Long receiver = 2L;
        MessageDTO messageDTO = new MessageDTO("Hello");

        Mockito.when(authentication.getPrincipal()).thenReturn(sender);
        Mockito.when(mockedFriendService.getFriendship(any(), any())).thenReturn(new Friends());
        Mockito.when(gardenUserService.getUserById(sender)).thenReturn(new GardenUser());
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        String result = messageController.sendMessage(receiver, messageDTO, bindingResult, authentication,  model);
        Assertions.assertEquals("users/message", result);
    }

    @Test
    void whenClickSendMessageButton_thenMessageSaved() {
        Long sender = 1L;
        Long receiver = 2L;
        MessageDTO messageDTO = new MessageDTO("Hello");

        Mockito.when(authentication.getPrincipal()).thenReturn(sender);
        Mockito.when(mockedFriendService.getFriendship(any(), any())).thenReturn(new Friends());
        Mockito.when(gardenUserService.getUserById(sender)).thenReturn(new GardenUser());
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        messageController.sendMessage(receiver, messageDTO, bindingResult, authentication,  model);
        Mockito.verify(mockedMessageService).sendMessage(sender, receiver, messageDTO);
    }
}
