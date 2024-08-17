package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

class MessageControllerTest {

    private MessageController messageController;
    private Model model;

    private GardenUserService gardenUserService;

    private FriendService friendService;

    private static Authentication authentication;

    @BeforeEach
    public void setup() {
        messageController = new MessageController(gardenUserService, friendService);
        model = null;
    }

    @Test
    void whenClickMessageButton_thenReturnMessagePage() {
        String result = messageController.messageFriend(1L, authentication,  model);
        Assertions.assertEquals("users/message", result);
    }
}
