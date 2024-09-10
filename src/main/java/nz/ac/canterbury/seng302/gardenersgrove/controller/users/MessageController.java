package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ThymeLeafDateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.TIMESTAMP_FORMAT;
import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.WEATHER_CARD_FORMAT_DATE;

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
                                Model model,
                                HttpSession session) {
        logger.info("GET message friend page opened to user {}", requestedUserId);

        String submissionToken = UUID.randomUUID().toString();
        session.setAttribute("submissionToken", submissionToken);
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser sentToUser = userService.getUserById(requestedUserId);

        // need to be friends to send a message
        Friends isFriend = friendService.getFriendship(loggedInUserId, requestedUserId);
        if (isFriend == null) {
            return "redirect:/users/manage-friends";
        }

        model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
        model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
        model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
        model.addAttribute("submissionToken", submissionToken);
        model.addAttribute("messagesMap", messageService.getMessagesBetweenFriends(loggedInUserId, requestedUserId));
        model.addAttribute("messageDTO", new MessageDTO(""));
        model.addAttribute("sentToUser", sentToUser);

        return "users/message";
    }

    @PostConstruct
    public void dummyMessages() {
        GardenUser u1 = userService.getUserByEmail("stynesluke@gmail.com");
        GardenUser u2 = userService.getUserByEmail("jan.doe@gmail.com");
        if (u1 != null && u2 != null) {
            messageService.sendMessageWithTimestamp(u1.getId(), u2.getId(),
                    new MessageDTO("Hello I am Luke Stynes! :)"), LocalDateTime.now().minusDays(2));
            messageService.sendMessageWithTimestamp(u2.getId(), u1.getId(),
                    new MessageDTO("Hello Luke Stynes, I am Jan Doe."), LocalDateTime.now().minusDays(1));
            messageService.sendMessageWithTimestamp(u1.getId(), u2.getId(),
                    new MessageDTO("Wow! What great bananas you grow Jan Doe."), LocalDateTime.now().minusDays(1));
            messageService.sendMessageWithTimestamp(u1.getId(), u2.getId(),
                    new MessageDTO("I'm sending a really really long message here so that Ryan does not have to manually " +
                            "write in a really long message each time he runs the application locally, it is really " +
                            "annoying so he asked me to write one that goes past the end of the screen"), LocalDateTime.now());
        }
    }
}
