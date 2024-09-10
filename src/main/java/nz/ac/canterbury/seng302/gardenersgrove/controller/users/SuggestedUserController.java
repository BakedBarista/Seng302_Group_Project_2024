package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.SuggestedUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SuggestedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SuggestedUserController {

    private static final Logger logger = LoggerFactory.getLogger(SuggestedUserController.class);

    private final FriendService friendService;
    private final GardenUserService gardenUserService;
    private final SuggestedUserService suggestedUserService;
    private final ObjectMapper objectMapper;

    private static final String SUCCESS = "success";

    @Autowired
    public SuggestedUserController(FriendService friendService, GardenUserService gardenUserService, SuggestedUserService suggestedUserService, ObjectMapper objectMapper) {
        this.friendService = friendService;
        this.gardenUserService = gardenUserService;
        this.suggestedUserService = suggestedUserService;
        this.objectMapper = objectMapper;
    }

    /**
     * Controls the initial home page controller when navigating to '/'
     *
     * @return the home page
     */
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        logger.info("GET /");
        try {
            Long userId = (Long) authentication.getPrincipal();
            GardenUser user = gardenUserService.getUserById(userId);
            List<GardenUser> suggestedUsers = new ArrayList<>();
            suggestedUsers.addAll(friendService.receivedConnectionRequests(user));
            suggestedUsers.addAll(friendService.availableConnections(user));

            if (suggestedUsers.isEmpty()) {
                return "home";
            }

            model.addAttribute("userId", suggestedUsers.get(0).getId());
            model.addAttribute("name", suggestedUsers.get(0).getFullName());
            model.addAttribute("description", suggestedUsers.get(0).getDescription());

            List<SuggestedUserDTO> userDtos = suggestedUsers.stream().map(SuggestedUserDTO::new).toList();
            String jsonUsers = objectMapper.writeValueAsString(userDtos);
            model.addAttribute("userList", jsonUsers);
        }
        catch (Exception e) {
            logger.error("Error getting suggested users", e);
        }
        return "home";
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> handleAcceptDecline(
            @RequestParam(name = "action") String action,
            @RequestParam(name = "id") Long suggestedId,
            Authentication authentication,
            Model model) {
        logger.info("Post /");
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = gardenUserService.getUserById(loggedInUserId);
        GardenUser suggestedUser = gardenUserService.getUserById(suggestedId);
        Map<String, Object> response = new HashMap<>();

        boolean validationPassed = suggestedUserService.validationCheck(loggedInUserId, suggestedId);
        if (!validationPassed) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        switch (action) {
            case "accept":
                // Look for a pending request and accept it if one exists.
                boolean pendingRequestAccepted = suggestedUserService.attemptToAcceptPendingRequest(loggedInUserId, suggestedId);
                if (pendingRequestAccepted) {
                    logger.info("Pending request from suggested user accepted, user's are now friends");
                    response.put(SUCCESS, true);
                } else { // Send a new pending request to the suggested user
                    boolean newRequestSent = suggestedUserService.sendNewPendingRequest(loggedInUser, suggestedUser);
                    if (!newRequestSent) {
                        logger.error("Users already have a pending request. Doing nothing");
                        break;
                    }
                }
                break;
            case "decline":
                // Look for a pending request and decline it
                boolean pendingRequestDeclined = suggestedUserService.attemptToDeclinePendingRequest(loggedInUserId, suggestedId);
                if (pendingRequestDeclined) {
                    logger.info("Pending request from suggested user declined, user won't be shown again");
                } else {
                    boolean declineStatusSet = suggestedUserService.setDeclinedFriendship(loggedInUser, suggestedUser);
                    if (!declineStatusSet) {
                        logger.error("Something went wrong trying to set a declined friendship. Doing nothing");
                        break;
                    }
                }
                break;
            default:
                logger.error("Action is neither 'accept' or 'decline'. Doing nothing");
                break;
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
