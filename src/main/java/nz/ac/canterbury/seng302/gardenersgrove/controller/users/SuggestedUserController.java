package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
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

    private final GardenService gardenService;
    private final GardenUserService gardenUserService;
    private final FriendService friendService;
    private final SuggestedUserService suggestedUserService;
    private static final String PASSWORD = "password";
    private final GardenUser user4 = new GardenUser("Max", "Doe", "max@gmail.com", PASSWORD,
            LocalDate.of(1970, 1, 1));

    @Autowired
    public SuggestedUserController(GardenService gardenService, GardenUserService gardenUserService, FriendService friendService, SuggestedUserService suggestedUserService) {
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
        this.friendService = friendService;
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
        try {
            //  hard-coding a mock user for the card
            gardenUserService.addUser(user4);
            user4.setDescription("I am here to meet some handsome young men who love gardening as much as I do! My passion is growing carrots and eggplants. In my spare time, I like to thrift, ice skate and hang out with my kid, Liana. She's three, and the love of my life. The baby daddy is my former sugar daddy, John Doe. He died of a heart attack on his yacht in Italy last summer");
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
            logger.error("Error getting gardens for user");
        }
        return "home";
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> homeAccept(
            @RequestParam(name = "action") String action,
            @RequestParam(name = "suggestedId") Long suggestedId,
            Authentication authentication,
            Model model) {
        logger.info("Post /");
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = gardenUserService.getUserById(loggedInUserId);
        GardenUser suggestedUser = gardenUserService.getUserById(suggestedId);
        Map<String, Object> response = new HashMap<>();
        boolean doNothing = false;


        boolean requestedIdIsLoggedInUser = suggestedId.equals(loggedInUserId);
        if (requestedIdIsLoggedInUser) {
            logger.error("Suggested friend request was to logged in user's own ID. Doing nothing.");
            doNothing = true;
        }

        boolean alreadyFriends = friendService.getAcceptedFriendship(loggedInUserId, suggestedId) != null;
        if (alreadyFriends) {
            logger.error("Suggested friend and logged in user are already friends. Doing nothing");
            doNothing = true;
        }

        boolean alreadyDeclined = friendService.getDeclinedFriendship(loggedInUserId, suggestedId).isPresent();
        if (alreadyDeclined) {
            logger.error("Suggest friend and logged in user have a decline friendship. Doing nothing");
            doNothing = true;
        }

        // No more action to be taken if the above checks fail
        if (doNothing) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // Accept button pressed flow
        if ("accept".equals(action)) {
            // Look for a pending request and accept it if one exists.
            boolean pendingRequestAccepted = suggestedUserService.attemptToAcceptPendingRequest(suggestedId, loggedInUserId);
            if (pendingRequestAccepted) {
                logger.info("Pending request from suggested user accepted, user's are now friends");
                response.put("success", true);
            } else { // Send a new pending request to the suggested user
                boolean newRequestSent = suggestedUserService.sendNewRequest(loggedInUser, suggestedUser);
                if (!newRequestSent) {
                    logger.error("Something went wrong trying to send a new request. Doing nothing");
                    response.put("success", false);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);

        // Decline button pressed flow
//        if ("decline".equals(action)) {
//            logger.info("Accept button pressed by: {} on: {}", loggedInUserId, suggestedId);
//
//            // Logged in user sent request to suggested user already then we do nothing.
//
//
//
//
//            // If there is no friendship relation at all then we want to send a pending
//
//
//        }
//
//
//        // Do the accept button
//        // Checks:
//        // 1. Is there a pending friend request sent from the suggestedUser to us? Accept this
//        // 2. Is there a pending friend request sent from us to the suggestedUser? Do nothing
//        // 3. Is there already an accepted or decline status between us and the suggestedUser? Do nothing
//        // 4. If there is no friend request at all then we want to send a request from us to the suggestedUser
//
//
//
//        // Do the decline button
//        // 1. Is there a pending friend request sent from the suggestedUser to us? Remove this request and make it declined
//        // 2. Is there a pending friend request sent from us to the suggestedUser? Decline their request
//        // 3. Is there already an accepted or decline status between us and the suggestedUser? Do nothing
//        // 4. If there is no friend request at all then we want to create a declined friendship from us to the suggestedUser
//
//
//
//
//
//        boolean success = false;
//
//
//
//        if ("accept".equals(action) && requestedIdNotUser && notAlreadyFriends) {
//
//
//            // If no request was accepted, check if a request was already sent
//            if (!success) {
//                boolean requestAlreadySent = suggestedUserService.friendRecordExists(loggedInUserId, suggestedId);
//
//                if (!requestAlreadySent) {
//                    Friends newRequest = new Friends(loggedInUser, suggestedUser, Friends.Status.PENDING);
//                    friendService.save(newRequest);
//                }
//            }
//        }
//        if ("decline".equals(action) && !requestedId.equals(loggedInUserId) && alreadyFriends == null) {
//            logger.info("Decline button pressed by: {} on: {}", loggedInUserId, requestedId);
//
//            List<Friends> receivedRequests = friendService.getReceivedRequests(loggedInUserId);
//
//            // If a request exists from the other user, we decline it
//            for (Friends receivedRequest : receivedRequests) {
//                if (receivedRequest.getSender().getId().equals(requestedId)) {
//                    logger.info("There was a pending request from that user, declining them.");
//                    receivedRequest.setStatus(Friends.Status.DECLINED);
//                    friendService.save(receivedRequest);
//                    success = true;
//                    break;
//                }
//            }
//
//            // If there is no existing request from the other user
//            if (!success) {
//                boolean requestAlreadySent = suggestedUserService.doesFriendRequestExist(loggedInUserId, requestedId);
//
//                // If we haven't already sent the user a request of some kind then we create a declined invitation.
//                if (!requestAlreadySent) {
//                    logger.info("There was no pending request, creating a decline to hide user from feed.");
//                    Friends newRequest = new Friends(loggedInUser, requestedUser, Friends.Status.DECLINED);
//                    friendService.save(newRequest);
//                }
//            }
//
//
//            // TODO: Implement hiding them in the feed in Make the connection feed work task
//            // NB: You shouldn't be able to see people in the feed that YOU have sent a request to. i.e. hearted them
//        }


        // telling us when to trigger the toast, only when we have made a connection!
//        response.put("success", success);
    }
}
