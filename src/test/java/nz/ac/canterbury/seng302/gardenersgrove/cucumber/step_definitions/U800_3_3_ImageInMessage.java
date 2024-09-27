package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MessageRepository;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions.U800_3_1_MessageFeature.myId;
import static nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions.U800_3_1_MessageFeature.otherUserId;
import static org.mockito.Mockito.when;

public class U800_3_3_ImageInMessage {
    private static MultipartFile image;
    private static MockHttpSession session;
    private static BindingResult bindingResult;

    @Autowired
    private MessageRepository messageRepository;


    private static Model model;
    private String result;
    private static Authentication authentication;



    @Autowired
    private MessageController messageController;

    @BeforeAll
    public static void setup() {
        image = Mockito.mock(MultipartFile.class);
        session = new MockHttpSession();
        authentication = mock(Authentication.class);
        model = mock(Model.class);
        bindingResult = mock(BindingResult.class);

    }

    @When("I send an image")
    public void iSendAMessageWithAnImage() throws IOException {
        Mockito.when(authentication.getPrincipal()).thenReturn(myId);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        session.setAttribute("submissionToken", "token");
        when(image.getContentType()).thenReturn("image/png");
        when(image.getBytes()).thenReturn(new byte[]{1,2,3,4,5,6,7,8});
        result = messageController.sendMessage(otherUserId, new MessageDTO("", "token") , bindingResult, authentication, model, session, image);
        Message message = messageRepository.findMessagesBetweenUsers(myId, otherUserId).get(0);
        Assertions.assertEquals(image.getBytes().length, message.getImageContent().length);
        Assertions.assertEquals(myId, message.getSender());
        Assertions.assertEquals(otherUserId, message.getReceiver());
    }



    @When("{string} sends me an image")
    public void whenMyFriendSendsMeAnImage(String friend) throws IOException {
        Mockito.when(authentication.getPrincipal()).thenReturn(otherUserId);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        session.setAttribute("submissionToken", "token");
        when(image.getContentType()).thenReturn("image/png");
        when(image.getBytes()).thenReturn(new byte[]{1,2,3,4,5,6,7,8});
        result = messageController.sendMessage(myId, new MessageDTO("", "token") , bindingResult, authentication, model, session, image);

        Message message = messageRepository.findMessagesBetweenUsers(myId, otherUserId).get(0);
        Assertions.assertEquals(image.getBytes().length, message.getImageContent().length);
        Assertions.assertEquals(myId, message.getSender());
        Assertions.assertEquals(otherUserId, message.getReceiver());




    }
}
