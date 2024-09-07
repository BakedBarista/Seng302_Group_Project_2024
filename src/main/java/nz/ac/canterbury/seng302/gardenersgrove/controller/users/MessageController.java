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

        model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
        model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
        model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
        model.addAttribute("submissionToken", submissionToken);
        model.addAttribute("messagesMap", messageService.getMessagesBetweenFriends(loggedInUserId, requestedUserId));
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
     * Method to return the message page
     * @param requestedUserId friend's id to send message to
     * @param model may not need one
     * @return message page
     */
    @GetMapping("message-home")
    public String messageHome(Authentication authentication,
        Model model,
        HttpSession session) {

        logger.info("GET message Home");

        Long loggedInUserId = (Long) authentication.getPrincipal();
        
        List<Message> chats = messageService.findAllRecentChats(loggedInUserId);
        Long requestedUserId;
        Map<GardenUser, String> recentChats = new HashMap<>();

        // this is to get who we are loading onto first
        if (!chats.isEmpty()) {

            // Get the first chat message
            Message firstChat = chats.get(0);
            if (loggedInUserId != firstChat.getSender()) {
                requestedUserId = firstChat.getSender();
            } else {
                requestedUserId = firstChat.getReceiver();
            }

            for (Message chat : chats) {
                logger.info("Sender: {}", chat.getSender());
                logger.info("Receiver: {}", chat.getReceiver());
                logger.info("Timestamp: {}", chat.getTimestamp());
                logger.info("Message: {}", chat.getMessageContent());
                logger.info("---------");

                GardenUser requestedUser = userService.getUserById(requestedUserId);
                recentChats.put(requestedUser, chat.getMessageContent());
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
        }

        return "users/message-home";
    }   

    @PostMapping("message-home")
public String messageHome(@RequestParam("userId") Long userId,
        Authentication authentication,
        Model model,
        HttpSession session) {

    logger.info("POST message Home");

    Long loggedInUserId = (Long) authentication.getPrincipal();
    
    List<Message> chats = messageService.findAllRecentChats(loggedInUserId);
    Map<GardenUser, String> recentChats = new HashMap<>();

    // Process recent chats
    for (Message chat : chats) {
        if (chat.getSender().equals(userId) || chat.getReceiver().equals(userId)) {
            GardenUser requestedUser = userService.getUserById(userId);
            recentChats.put(requestedUser, chat.getMessageContent());
            break; // Exit loop once the relevant chat is found
        }
    }

    String submissionToken = UUID.randomUUID().toString();
    session.setAttribute("submissionToken", submissionToken);
    GardenUser sentToUser = userService.getUserById(userId);

    Friends isFriend = friendService.getFriendship(loggedInUserId, userId);
    if (isFriend == null) {
        return "redirect:/users/manage-friends";
    }

    model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
    model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
    model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
    model.addAttribute("submissionToken", submissionToken);
    model.addAttribute("messagesMap", messageService.getMessagesBetweenFriends(loggedInUserId, userId));
    model.addAttribute("sentToUser", sentToUser);
    model.addAttribute("recentChats", recentChats);

    return "users/message-home";
}
}
