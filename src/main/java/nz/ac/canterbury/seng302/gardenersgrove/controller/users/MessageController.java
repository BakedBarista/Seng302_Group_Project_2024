package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {

    public MessageController() {

    }

    /**
     * Method to return the message page
     * @param id friend's id to send message to
     * @param model may not need one
     * @return message page
     */
    @GetMapping("users/message")
    public String messageFriend(@RequestParam("id") Long id,
                                Model model) {
        return "users/message";
    }

}
