package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.message.Message;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for dealing with the API calls for refreshing the chat feed
 */
@RestController
public class ChatFeedController {
    private final Logger logger = LoggerFactory.getLogger(ChatFeedController.class);

    private final MessageService messageService;


    @Autowired
    public ChatFeedController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Handles the GET request for retrieving recent chats of the logged-in user.
     *
     * @param authentication The authentication object containing the logged-in user's credentials.
     * @param model The model object used for passing attributes to the view (not used in this case).
     * @param session The HTTP session object that can hold session-specific data (not used in this case).
     * @return A map where the key is the user ID and the value is the latest message associated with that user.
     */
    @GetMapping("api/messages/recent-chats")
    public Map<String, Message> messageRecentChat(Authentication authentication, Model model, HttpSession session) {
        logger.info("GET request to /api/messages/recent-chats");
        logger.info("Getting recent chats");

        Long loggedInUserId = (Long) authentication.getPrincipal();

        List<Message> allMessages = messageService.findAllRecentChats(loggedInUserId);

        Map<Long, Message> latestMessages = messageService.getLatestMessages(allMessages, loggedInUserId);
        Map<String, Message> stringLatestMessages = new HashMap<>();

        for (Map.Entry<Long, Message> entry : latestMessages.entrySet()) {
            stringLatestMessages.put(entry.getKey().toString(), entry.getValue());
        }

        return stringLatestMessages;
    }
}
