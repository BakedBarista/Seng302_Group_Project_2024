package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")

class MessageControllerTest {

    private MessageController messageController;
    private Model model;

    @Autowired
    private GardenUserService gardenUserService;
    private static FriendService mockedFriendService;

    private static Authentication authentication;

    @BeforeEach
    public void setup() {
        gardenUserService = mock(GardenUserService.class);
        mockedFriendService = mock(FriendService.class);
        messageController = new MessageController(gardenUserService, mockedFriendService, null);

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
}
