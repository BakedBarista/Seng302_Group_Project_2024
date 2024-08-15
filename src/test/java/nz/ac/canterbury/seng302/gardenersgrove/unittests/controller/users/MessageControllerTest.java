package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

class MessageControllerTest {

    private MessageController messageController;
    private Model model;

    @BeforeEach
    void setup() {
        messageController = new MessageController();
        model = null;
    }
    @Test
    void whenClickMessageButton_thenReturnMessagePage() {
        String result = messageController.messageFriend(1L,model);
        Assertions.assertEquals("users/message", result);
    }
}
