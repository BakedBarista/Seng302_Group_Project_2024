package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MessageController {

    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final GardenUserService userService;
    private final FriendService friendService;
    private final MessageService messageService;

    @Autowired
    public MessageController(GardenUserService userService,
                             FriendService friendService,
                             MessageService messageService) {
        this.userService = userService;
        this.friendService = friendService;
        this.messageService = messageService;
    }

    /**
     * Method to return the message page
     * @param requestedUserId friend's id to send message to
     * @param model may not need one
     * @return message page
     */
    @GetMapping("users/message")
    public String messageFriend(@RequestParam("id") Long requestedUserId,
                                Authentication authentication,
                                Model model) {
        logger.info("GET message friend page opened to user {}", requestedUserId);

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser sentToUser = userService.getUserById(requestedUserId);

        // need to be friends to send a message
        Friends isFriend = friendService.getFriendship(loggedInUserId, requestedUserId);
        if (isFriend == null) {
            return "redirect:/users/manage-friends";
        }

        model.addAttribute("messageDTO", new MessageDTO(""));
        model.addAttribute("sentToUser", sentToUser);

        return "users/message";
    }

    /**
     * Handles the post mapping for sending messages between users
     * @param receiver the ID of the user the message is being sent to/.
     * @param authentication the authentication of the authenticated user
     * @param model the model data in the html request
     * @return Redirects the user to the send message page
     */
    @PostMapping("users/{id}/send-message")
    public String sendMessage(
            @PathVariable("id") Long receiver,
            @Valid @ModelAttribute("messageDTO") MessageDTO messageDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model) {

        if (bindingResult.hasErrors()) {
            logger.info("Binding result has errors");
            logger.info("Receiver is {}", receiver);
            model.addAttribute("messageDTO", messageDTO);

            GardenUser sentToUser = userService.getUserById(receiver);
            model.addAttribute("sentToUser", sentToUser);

            return "users/message";
        }

        Long sender = (Long) authentication.getPrincipal();
        messageService.sendMessage(sender, receiver, messageDTO);

        return messageFriend(receiver, authentication, model);
    }
}
