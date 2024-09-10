package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        List<Message> allMessages = messageService.findAllRecentChats(loggedInUserId);

        Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, loggedInUserId);

        Map<GardenUser, String> recentChats = messageService.convertToPreview(recentMessagesMap);

        model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
        model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
        model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
        model.addAttribute("submissionToken", submissionToken);
        model.addAttribute("messagesMap", messageService.getMessagesBetweenFriends(loggedInUserId, requestedUserId));
        model.addAttribute("sentToUser", sentToUser);
        model.addAttribute("recentChats", recentChats);
        model.addAttribute("activeChat", requestedUserId);

        return "users/message-home";
    }

    /**
     * Handles the post mapping for sending messages between users
     * @param receiver the ID of the user the message is being sent to/.
     * @param authentication the authentication of the authenticated user
     * @param model the model data in the html request
     * @return Redirects the user to the send message page
     */
    @PostMapping("users/message")
    public String sendMessage(
            @RequestParam("id") Long receiver,
            @Valid @ModelAttribute("messageDTO") MessageDTO messageDTO,
            Authentication authentication,
            Model model,
            HttpSession session) {
        logger.info("POST send message to {}", receiver);

        String tokenFromForm = messageDTO.getSubmissionToken();
        String sessionToken = (String) session.getAttribute("submissionToken");
        if (sessionToken != null && sessionToken.equals(tokenFromForm)) {
            Long sender = (Long) authentication.getPrincipal();
            messageService.sendMessage(sender, receiver, messageDTO);
            session.removeAttribute("submissionToken");
        }

        return messageFriend(receiver, authentication, model,session);
    }

    @PostConstruct
    public void dummyMessages() {
        GardenUser u1 = userService.getUserByEmail("stynesluke@gmail.com");
        GardenUser u2 = userService.getUserByEmail("jan.doe@gmail.com");
        if (u1 != null && u2 != null) {
            messageService.sendMessageWithTimestamp(u1.getId(), u2.getId(),
                    new MessageDTO("Hello I am Luke Stynes! :)", "token"), LocalDateTime.now().minusDays(2));
            messageService.sendMessageWithTimestamp(u2.getId(), u1.getId(),
                    new MessageDTO("Hello Luke Stynes, I am Jan Doe.", "token"), LocalDateTime.now().minusDays(1));
            messageService.sendMessageWithTimestamp(u1.getId(), u2.getId(),
                    new MessageDTO("Wow! What great bananas you grow Jan Doe.", "token"), LocalDateTime.now().minusDays(1));
            messageService.sendMessageWithTimestamp(u1.getId(), u2.getId(),
                    new MessageDTO("I'm sending a really really long message here so that Ryan does not have to manually " +
                            "write in a really long message each time he runs the application locally, it is really " +
                            "annoying so he asked me to write one that goes past the end of the screen", "token"), LocalDateTime.now());
        }
    }

    /**
     * Handles GET requests for the message home page.
     * @param authentication the authentication object representing the currently logged-in user.
     * @param model          the model to be populated with view attributes.
     * @param session        the HTTP session used to store the submission token.
     * @return               the view name "users/message-home" or a redirect to manage friends if the user
     *                       is not a friend.
     */
    @GetMapping("message-home")
    public String messageHome(Authentication authentication,
        Model model,
        HttpSession session) {

        logger.info("GET message Home");

        Long loggedInUserId = (Long) authentication.getPrincipal();
        List<Message> allMessages = messageService.findAllRecentChats(loggedInUserId);

        Long requestedUserId = null;

        if (!allMessages.isEmpty()) {
            Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, loggedInUserId);

            Map<GardenUser, String> recentChats = messageService.convertToPreview(recentMessagesMap);

            Long latestUserId = messageService.getActiveChat(recentMessagesMap);

            if (latestUserId != null) {
                requestedUserId = latestUserId;
            }

            String submissionToken = UUID.randomUUID().toString();
            session.setAttribute("submissionToken", submissionToken);
            GardenUser sentToUser = userService.getUserById(requestedUserId);


            Friends isFriend = friendService.getFriendship(loggedInUserId, requestedUserId);
            if (isFriend == null) {
                return "redirect:/users/manage-friends";
            }

            model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
            model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
            model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
            model.addAttribute("submissionToken", submissionToken);
            model.addAttribute("messagesMap", messageService.getMessagesBetweenFriends(loggedInUserId, requestedUserId));
            model.addAttribute("sentToUser", sentToUser);
            model.addAttribute("recentChats", recentChats);
            model.addAttribute("activeChat", requestedUserId);
        }

        return "users/message-home";
    }

    /**
     * Processes a POST request to update the message home view.
     *
     * @param requestedUserId the ID of the user to whom the message is being sent.
     * @param authentication  the current logged-in user's authentication object.
     * @param model           the model to be populated with view attributes.
     * @param session         the HTTP session used to store the submission token.
     * @return                the view name, or a redirect if the user is not a friend.
     */
    @PostMapping("message-home")
    public String messageHomeSend(@RequestParam("userId") Long requestedUserId,
        Authentication authentication,
        Model model,
        HttpSession session) {

    logger.info("POST message Home");

    Long loggedInUserId = (Long) authentication.getPrincipal();
    
    List<Message> allMessages = messageService.findAllRecentChats(loggedInUserId);

    if (!allMessages.isEmpty()) {

        Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, loggedInUserId);

        Map<GardenUser, String> recentChats = messageService.convertToPreview(recentMessagesMap);


        String submissionToken = UUID.randomUUID().toString();
        session.setAttribute("submissionToken", submissionToken);
        GardenUser sentToUser = userService.getUserById(requestedUserId);


        Friends isFriend = friendService.getFriendship(loggedInUserId, requestedUserId);
        if (isFriend == null) {
            return "redirect:/users/manage-friends";
        }

        model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
        model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
        model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
        model.addAttribute("submissionToken", submissionToken);
        model.addAttribute("messagesMap", messageService.getMessagesBetweenFriends(loggedInUserId, requestedUserId));
        model.addAttribute("sentToUser", sentToUser);
        model.addAttribute("recentChats", recentChats);
        model.addAttribute("activeChat", requestedUserId);
    }

    return "users/message-home";
}
}
