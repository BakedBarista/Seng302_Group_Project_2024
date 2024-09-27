package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.MessageDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.ChatPreview;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ThymeLeafDateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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

    private static final String SUBMISSION_TOKEN = "submissionToken";
    private static final String MSG_HOME_ENDPOINT = "users/message-home";
    private static final String MANAGE_FRIENDS_REDIRECT = "redirect:/users/manage-friends";

    @Autowired
    public MessageController(GardenUserService userService,
            FriendService friendService,
            MessageService messageService) {
        this.userService = userService;
        this.friendService = friendService;
        this.messageService = messageService;
    }

    /**
     * Handles GET requests to display the message page for a specific user.
     * 
     * @param requestedUserId the ID of the user to view messages with
     * @param authentication  the authentication information of the currently
     *                        logged-in user
     * @param model           the model to be populated with view attributes
     * @param session         the HTTP session to store and retrieve the submission
     *                        token
     * @return the view name for the message page or a redirect to manage friends if
     *         not friends
     */
    @GetMapping("users/message")
    public String messageFriend(@RequestParam("id") Long requestedUserId,
            Authentication authentication,
            Model model,
            HttpSession session) {
        
        Long loggedInUserId = (Long) authentication.getPrincipal();
        messageService.setReadTime(loggedInUserId, requestedUserId);

        return setupMessagePage(requestedUserId, authentication, model, session);
    }

    /**
     * Handles GET requests to display the message home page.
     * 
     * @param authentication the authentication information of the currently
     *                       logged-in user
     * @param model          the model to be populated with view attributes
     * @param session        the HTTP session to store and retrieve the submission
     *                       token
     * @return the view name for the message home page or a redirect to manage
     *         friends if not friends
     */
    @GetMapping("message-home")
    public String messageHome(Authentication authentication,
            Model model,
            HttpSession session) {

        logger.info("GET /message-home");

        Long loggedInUserId = (Long) authentication.getPrincipal();
        Long requestedUserId = getLatestRequestedUserId(authentication);
        messageService.setReadTime(loggedInUserId, requestedUserId);

        return setupMessagePage(requestedUserId, authentication, model, session);
    }

    /**
     * Sets up the message page with required attributes and checks friendship
     * status.
     * 
     * @param requestedUserId the ID of the user to view messages with
     * @param authentication  the authentication information of the currently
     *                        logged-in user
     * @param model           the model to be populated with view attributes
     * @param session         the HTTP session to store and retrieve the submission
     *                        token
     * @return the view name for the message page or a redirect to manage friends if
     *         not friends
     */
    public String setupMessagePage(Long requestedUserId,
            Authentication authentication,
            Model model,
            HttpSession session) {
        logger.info("GET message page opened to user {}", requestedUserId);

        if(requestedUserId == null) {
            return MSG_HOME_ENDPOINT;
        }
        String submissionToken = UUID.randomUUID().toString();
        session.setAttribute(SUBMISSION_TOKEN, submissionToken);

        Long loggedInUserId = (Long) authentication.getPrincipal();

        GardenUser sentToUser = userService.getUserById(requestedUserId);

        Friends isFriend = friendService.getFriendship(loggedInUserId, requestedUserId);
        if (isFriend == null) {
            return MANAGE_FRIENDS_REDIRECT;
        }

        List<Message> allMessages = messageService.findAllRecentChats(loggedInUserId);

        Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, loggedInUserId);

        Map<GardenUser, ChatPreview> recentChats = messageService.convertToPreview(loggedInUserId, recentMessagesMap);

        messageService.setupModelAttributes(model, loggedInUserId, requestedUserId, sentToUser, recentChats,
                submissionToken);

        messageFriendList(requestedUserId, authentication, model, session);
        model.addAttribute("messageDTO", new MessageDTO("", ""));

        return MSG_HOME_ENDPOINT;
    }

    /**
     * Retrieves the ID of the most recent user with whom the logged-in user has
     * exchanged messages.
     * 
     * @param authentication the authentication information of the currently
     *                       logged-in user
     * @return the ID of the latest active user or null if no recent messages exist
     */
    private Long getLatestRequestedUserId(Authentication authentication) {
        Long loggedInUserId = (Long) authentication.getPrincipal();
        List<Message> allMessages = messageService.findAllRecentChats(loggedInUserId);

        if (!allMessages.isEmpty()) {
            Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, loggedInUserId);
            return messageService.getActiveChat(recentMessagesMap);
        }
        return null;
    }

    /**
     * Handles the post mapping for sending messages between users
     * 
     * @param receiver       the ID of the user the message is being sent to/.
     * @param authentication the authentication of the authenticated user
     * @param model          the model data in the html request
     * @return Redirects the user to the send message page
     */
    @PostMapping("users/message")
    public String sendMessage(
            @RequestParam("id") Long receiver,
            @Valid @ModelAttribute("messageDTO") MessageDTO messageDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            HttpSession session,
            @RequestParam(value = "addImage", required = false) MultipartFile file) {
        logger.info("POST send message to {}", receiver);
        Long sender = (Long) authentication.getPrincipal();
        Friends friends = friendService.getFriendship(sender,receiver);
        if (!friends.getStatus().toString().equals("ACCEPTED")) {
            return MSG_HOME_ENDPOINT;
        }
        String tokenFromForm = messageDTO.getSubmissionToken();
        String sessionToken = (String) session.getAttribute(SUBMISSION_TOKEN);

        if (bindingResult.hasErrors()) {
            logger.info("Binding result has errors");
            model.addAttribute("messageDTO", messageDTO);
            GardenUser sentToUser = userService.getUserById(receiver);
            model.addAttribute("sentToUser", sentToUser);
            Long loggedInUserId = (Long) authentication.getPrincipal();
            model.addAttribute("messagesMap", messageService.getMessagesBetweenFriends(loggedInUserId, receiver));
            model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
            model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
            model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
            model.addAttribute("submissionToken", tokenFromForm);

            return "users/message-home";
        }

        if (sessionToken != null && sessionToken.equals(tokenFromForm)) {

            if (file != null && !file.isEmpty()) {
                logger.info("Processing image upload for user {}", sender);
                try {
                    Message messageWithImage = messageService.sendImage(sender, receiver, messageDTO, file);
                    model.addAttribute("imageMessage", messageWithImage);
                } catch (IOException e) {
                    logger.error("Error uploading image", e);
                    model.addAttribute("fileError", "File too large or wrong file type");
                    return messageFriend(receiver, authentication, model, session);
                }
            } else {
                messageService.sendMessage(sender, receiver, messageDTO);
            }
            session.removeAttribute(SUBMISSION_TOKEN);
        }
        return messageFriend(receiver, authentication, model, session);
    }

    /**
     * Page that is fetch by JS to replace the content of the scrollable message list
     * @param requestedUserId friend's id to send message to
     * @param authentication the authentication of the authenticated user
     * @param model the model data in the html request
     * @param session the session data in the html request
     * @return messages list page
     */
    @GetMapping("api/messages/{id}")
    public String messageFriendList(@PathVariable("id") Long requestedUserId,
                                Authentication authentication,
                                Model model,
                                HttpSession session) {
        logger.info("GET message friend page opened to user {}", requestedUserId);

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser sentToUser = userService.getUserById(requestedUserId);

        messageService.setReadTime(loggedInUserId, requestedUserId);

        // need to be friends to send a message
        Friends isFriend = friendService.getFriendship(loggedInUserId, requestedUserId);
        if (isFriend == null) {
            return "redirect:/users/manage-friends";
        }

        model.addAttribute("dateFormatter", new ThymeLeafDateFormatter());
        model.addAttribute("TIMESTAMP_FORMAT", TIMESTAMP_FORMAT);
        model.addAttribute("DATE_FORMAT", WEATHER_CARD_FORMAT_DATE);
        model.addAttribute("messagesMap", messageService.getMessagesBetweenFriends(loggedInUserId, requestedUserId));
        model.addAttribute("sentToUser", sentToUser);

        return "users/messagesList";
    }

    /**
     * Processes a POST request to update the message home view.
     *
     * @param requestedUserId the ID of the user to whom the message is being sent.
     * @param authentication  the current logged-in user's authentication object.
     * @param model           the model to be populated with view attributes.
     * @param session         the HTTP session used to store the submission token.
     * @return the view name, or a redirect if the user is not a friend.
     */
    @PostMapping("message-home")
    public String messageHomeSend(@RequestParam("userId") Long requestedUserId,
            Authentication authentication,
            Model model,
            HttpSession session) {

        logger.info("POST /message-home");
        logger.info(String.valueOf(requestedUserId));

        Long loggedInUserId = (Long) authentication.getPrincipal();

        List<Message> allMessages = messageService.findAllRecentChats(loggedInUserId);

        messageService.setReadTime(loggedInUserId, requestedUserId);

        if (!allMessages.isEmpty()) {
            Map<Long, Message> recentMessagesMap = messageService.getLatestMessages(allMessages, loggedInUserId);
            Map<GardenUser, ChatPreview> recentChats = messageService.convertToPreview(loggedInUserId, recentMessagesMap);

            String submissionToken = UUID.randomUUID().toString();
            session.setAttribute(SUBMISSION_TOKEN, submissionToken);
            GardenUser sentToUser = userService.getUserById(requestedUserId);

            Friends isFriend = friendService.getFriendship(loggedInUserId, requestedUserId);
            if (isFriend == null) {
                return MANAGE_FRIENDS_REDIRECT;
            }

            messageService.setupModelAttributes(model, loggedInUserId, requestedUserId, sentToUser, recentChats,
                    submissionToken);
        }
        model.addAttribute("messageDTO", new MessageDTO("", ""));
        return MSG_HOME_ENDPOINT;
    }
}
