package nz.ac.canterbury.seng302.gardenersgrove.controller.api;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Message;
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


@RestController
public class RecentChatsController {

    private final Logger logger = LoggerFactory.getLogger(RecentChatsController.class);

    private final MessageService messageService;


    @Autowired
    public RecentChatsController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     *
     * @param authentication
     * @return
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
