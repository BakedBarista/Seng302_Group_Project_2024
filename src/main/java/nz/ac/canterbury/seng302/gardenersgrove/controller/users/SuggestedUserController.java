package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
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
    private FriendService friendService;
    private static final String PASSWORD = "password";
    private final GardenUser user4 = new GardenUser("Max", "Doe", "max@gmail.com", PASSWORD,
            LocalDate.of(1970, 1, 1));

    @Autowired
    public SuggestedUserController(GardenService gardenService, GardenUserService gardenUserService, FriendService friendService) {
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
        this.friendService = friendService;
    }

    /**
     * Controls the initial home page controller when navigating to '/'
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

            if(user.getId() != null) {
                model.addAttribute("userId", suggestedUsers.get(0).getId());
                model.addAttribute("name", suggestedUsers.get(0).getFullName());
                model.addAttribute("description", suggestedUsers.get(0).getDescription());
            }
        }
        catch (Exception e) {
            logger.error("Error getting gardens for user");
        }
        return "home";
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> homeAccept(
            @RequestParam(name = "action") String action,
            @RequestParam(name = "id") Long requestedId,
            Authentication authentication,
            Model model) {
        logger.info("Post /");
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = gardenUserService.getUserById(loggedInUserId);
        GardenUser requestedUser = gardenUserService.getUserById(requestedId);

        Friends alreadyFriends = friendService.getAcceptedFriendship(loggedInUserId, requestedId);
        Map<String, Object> response = new HashMap<>();
        boolean success = false;

        if ("accept".equals(action) && !requestedId.equals(loggedInUserId)) {
            if (alreadyFriends == null) {
                // Not already friends
                List<Friends> sentRequests = friendService.getSentRequests(loggedInUserId);
                List<Friends> receivedRequests = friendService.getReceivedRequests(loggedInUserId);

                // Attempt to accept an existing friend request
                for (Friends receivedRequest : receivedRequests) {
                    if (receivedRequest.getSender().getId().equals(requestedId)) {
                        receivedRequest.setStatus(Friends.Status.ACCEPTED);
                        friendService.save(receivedRequest);
                        success = true;
                        break;
                    }
                }

                // If no request was accepted, check if a request was already sent
                if (!success) {
                    boolean requestAlreadySent = false;
                    for (Friends sentRequest : sentRequests) {
                        if (sentRequest.getReceiver().getId().equals(requestedId)) {
                            requestAlreadySent = true;
                            break;
                        }
                    }

                    // If no request was already sent, send a new request
                    if (!requestAlreadySent) {
                        Friends newRequest = new Friends(loggedInUser, requestedUser, Friends.Status.PENDING);
                        friendService.save(newRequest);
                    }
                }
            }
        }
        // telling us when to trigger the toast, only when we have made a connection!
        response.put("success", success);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
