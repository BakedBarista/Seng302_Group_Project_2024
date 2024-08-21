package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * This controller defines the base application end points.
 */
@Controller
public class ApplicationController {
    private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private GardenService gardenService;
    private GardenUserService gardenUserService;
    private FriendService friendService;

    @Autowired
    public ApplicationController(GardenService gardenService, GardenUserService gardenUserService,  FriendService friendService) {
        this.gardenService = gardenService;
        this.gardenUserService = gardenUserService;
        this.friendService = friendService;
    }

    /**
     * Controls the initial home page controller when navigating to '/'
     * @return the home page
     */
    @GetMapping("/")
    public String home( Model model) {
        logger.info("GET /");
        try {
            GardenUser owner = gardenUserService.getCurrentUser();
            if(owner.getId() != null) {
                List<Garden> gardens = gardenService.getGardensByOwnerId(owner.getId());
                model.addAttribute("gardens", gardens);
            }
        }
        catch (Exception e) {
        logger.error("Error getting gardens for user");
        }
        return "home";
    }

    /**
     * Controls the initial home page controller when navigating to '/'
     * @return the home page
     */
    @PostMapping("/")
    public String homeAccept(@RequestParam(name = "action") String action, 
        @RequestParam(name = "id") Long requestedId,  
        Authentication authentication,
        Model model) {

        logger.info("Post /");

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = gardenUserService.getUserById(loggedInUserId);
        GardenUser requestedUser = gardenUserService.getUserById(requestedId);

        List<Friends> sentRequests = friendService.getSentRequests(loggedInUserId);
        List<Friends> receivedRequests = friendService.getReceivedRequests(loggedInUserId);
        
        if ("accept".equals(action)) {
            // accepting a already sent friend request
            for (Friends receivedRequest : receivedRequests) {
                if (receivedRequest.getReceiver().getId().equals(requestedId)) {
                    receivedRequest.setStatus(Friends.Status.ACCEPTED);
                    friendService.save(receivedRequest);
                    // return or do something here
                    return "home";
                } 
            }

            boolean requestAlreadySent = false;
        
            // Check if a request has already been sent
            for (Friends sentRequest : sentRequests) {
                if (sentRequest.getReceiver().getId().equals(requestedId)) {
                    requestAlreadySent = true;
                    break;
                }
            }
            
            if(!requestAlreadySent){
                //sending a new request to 
                Friends newRequest = new Friends(loggedInUser, requestedUser, Friends.Status.PENDING);
                friendService.save(newRequest);
            }
        }

        return "home";
    }


    @GetMapping("/ws-test")
    public String wsTest() {
        return "wsTest";
    }
}
