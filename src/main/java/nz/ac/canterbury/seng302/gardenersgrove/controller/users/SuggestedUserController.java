package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SuggestedUserController {

    private static final Logger logger = LoggerFactory.getLogger(SuggestedUserController.class);

    private final GardenUserService gardenUserService;
    private final SuggestedUserService suggestedUserService;
    private static final String PASSWORD = "password";
    private static final String SUCCESS = "success";
    private final GardenUser user4 = new GardenUser("Max", "Doe", "max@gmail.com", PASSWORD,
            LocalDate.of(1970, 1, 1));

    @Autowired
    public SuggestedUserController(GardenUserService gardenUserService, SuggestedUserService suggestedUserService) {
        this.gardenUserService = gardenUserService;
        this.suggestedUserService = suggestedUserService;
    }

    /**
     * Controls the initial home page controller when navigating to '/'
     *
     * @return the home page
     */
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        logger.info("GET /");

        if (authentication == null) {
            logger.info("User is not logged in, directing to landing page.");
            return "home";
        }

        try {
            //  hard-coding a mock user for the card
            gardenUserService.addUser(user4);
            user4.setDescription("I am here to meet some handsome young men who love gardening as much as I do! In my spare time, I like to thrift, ice skate, and grow vege. The baby daddy is my former sugar daddy John Doe. He died of a heart attack on his yacht in Italy last summer.");
            List<GardenUser> suggestedUsers = new ArrayList<>();
            suggestedUsers.add(user4);

            Long userId = (Long) authentication.getPrincipal();
            GardenUser user = gardenUserService.getUserById(userId);

            if (user.getId() != null) {
                model.addAttribute("userId", suggestedUsers.get(0).getId());
                model.addAttribute("name", suggestedUsers.get(0).getFullName());
                model.addAttribute("description", suggestedUsers.get(0).getDescription());
            }
        } catch (Exception e) {
            logger.error("Error with getting suggested friends for user");
        }
        return "suggestedFriends";
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> handleAcceptDecline(
            @RequestParam(name = "action") String action,
            @RequestParam(name = "suggestedId") Long suggestedId,
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
                        return ResponseEntity.status(HttpStatus.OK).body(response);
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
                        return ResponseEntity.status(HttpStatus.OK).body(response);
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
