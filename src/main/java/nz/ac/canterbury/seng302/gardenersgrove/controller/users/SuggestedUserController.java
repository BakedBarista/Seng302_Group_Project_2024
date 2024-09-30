package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.SuggestedUserDTO;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SuggestedUserController {

    private static final Logger logger = LoggerFactory.getLogger(SuggestedUserController.class);

    private final GardenUserService gardenUserService;
    private final SuggestedUserService suggestedUserService;
    private final ObjectMapper objectMapper;

    private static final String SUCCESS = "success";

    @Autowired
    public SuggestedUserController(GardenUserService gardenUserService, SuggestedUserService suggestedUserService, ObjectMapper objectMapper) {
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
    public String home(Authentication authentication, Model model, HttpServletRequest request, HttpServletResponse response) {
        logger.info("GET /");

        if (authentication == null) {
            logger.info("User is not logged in, directing to landing page.");
            return "home";
        }

        try {
            Long userId = (Long) authentication.getPrincipal();
            GardenUser user = gardenUserService.getUserById(userId);

            List<SuggestedUserDTO> feedContents = suggestedUserService.getSuggestionFeedContents(user, request, response);
            if (feedContents.isEmpty()) {
                return "suggestedFriends";
            }

            GardenUser suggestedUser = gardenUserService.getUserById(feedContents.get(0).getId());
            model.addAttribute("profilePicture", suggestedUser.getProfilePicture());
            model.addAttribute("userId", feedContents.get(0).getId());
            model.addAttribute("name", feedContents.get(0).getFullName());
            model.addAttribute("description", feedContents.get(0).getDescription());
            logger.info("Description: {}", feedContents.get(0).getDescription());

            String jsonUsers = objectMapper.writeValueAsString(feedContents);
            model.addAttribute("userList", jsonUsers);
        } catch (Exception e) {
            logger.error("Error getting suggested users", e);
        }
        return "suggestedFriends";
    }

    /**
     * Handles Post request for accepting or declining friend requests or connection suggestions
     *
     * @param action action to perform (accept or decline)
     * @param suggestedId ID of suggested garden user
     * @param authentication authentication
     * @param model model
     * @return ResponseEntity containing result
     */
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

        if (loggedInUser == null) {
            logger.error("User is not logged in, doing nothing");
            response.put(SUCCESS, false);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

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
