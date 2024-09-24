package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.MessageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
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
import static nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions.U800_3_1_MessageFeature.receiverId;
import static org.mockito.Mockito.when;

public class U800_3_3_ImageInMessage {
    private static MultipartFile image;
    private static MockHttpSession session;
    private static BindingResult bindingResult;


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
        System.out.println(receiverId);
        when(image.getContentType()).thenReturn("image/png");
        when(image.getBytes()).thenReturn(new byte[]{1,2,3,4,5,6,7,8});
        result = messageController.sendMessage(receiverId, new MessageDTO("", "token") , bindingResult, authentication, model, session, image);
    }
}
