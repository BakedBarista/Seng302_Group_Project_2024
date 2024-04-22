package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.repository.RequestRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.List;

import org.h2.engine.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ManageFriendsController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    private FriendService friendService;

    @Autowired
    private GardenUserService gardenUserService;
    private GardenUserService userService;
    private RequestRepository requestRepository;

    @Autowired
    public void setUserService(GardenUserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setRequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    @Autowired
    public void ManageUserController(GardenUserService gardenUserService) {
        this.gardenUserService = gardenUserService;
    }

    public ManageFriendsController(FriendService friendService) {
        this.friendService = friendService;
    }


    long id =1 ;
    /**
     * Shows the login page
     *
     * @param error error message, if there's any
     * @param model Thymeleaf model
     * @return login page view
     */
    @GetMapping("users/manageFriends")
    public String login(Authentication authentication, @RequestParam(required = false) String error,
                        Model model) {
        logger.info("users/manageFriends");
        
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);

        List<GardenUser> allUsers = gardenUserService.getUser();
        
        List<GardenUser> Friends = friendService.getAllFriends(id);

        List<Requests> sentRequests = requestRepository.getSentRequests(loggedInUserId);
        List<Requests> receivedRequests = requestRepository.getReceivedRequests(loggedInUserId);
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);
        return "users/manageFriends";
    }

    @PostMapping("users/manageFriends")
    public String login(Authentication authentication, 
        @RequestParam(name = "requestedUser") Long requestedUser, 
        Model model,
        HttpServletRequest request) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser sentTo = userService.getUserById(requestedUser);
        
        Requests requestEntity = new Requests(loggedInUser, sentTo, "pending");
        
        
        requestRepository.save(requestEntity);

        List<GardenUser> allUsers = gardenUserService.getUser();
        List<GardenUser> Friends = friendService.getAllFriends(id);
        List<Requests> sentRequests = requestRepository.getSentRequests(loggedInUserId);
        List<Requests> receivedRequests = requestRepository.getReceivedRequests(loggedInUserId);
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);

        return "users/manageFriends";
    }

}