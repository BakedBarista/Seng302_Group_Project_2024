package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.repository.RequestRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;

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


    @Autowired
    private FriendService friendService;
    private GardenUserService gardenUserService;
    private GardenUserService userService;
    private RequestService requestService;

    @Autowired
    public void setUserService(GardenUserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
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
     * Shows the manage friends page
     *
     * @param error error message, if there's any
     * @param model Thymeleaf model
     * @return login page view
     */
    @GetMapping("users/manageFriends")
    public String manageFriends(Authentication authentication, @RequestParam(required = false) String error,
                        Model model) {
        logger.info("users/manageFriends");
        
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);

        List<GardenUser> allUsers = gardenUserService.getUser();
        
        List<GardenUser> Friends = friendService.getAllFriends(id);

        List<Requests> sentRequests = requestService.getSentRequests(loggedInUserId);
        List<Requests> receivedRequests = requestService.getReceivedRequests(loggedInUserId);
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);
        return "users/manageFriends";
    }

    @PostMapping("users/manageFriends")
    public String manageFriends(Authentication authentication, 
        @RequestParam(name = "requestedUser", required = false) Long requestedUser, 
        @RequestParam(name = "acceptUser", required = false) Long acceptUser, 
        @RequestParam(name = "declineUser", required = false) Long declineUser, 
        Model model,
        HttpServletRequest request) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser sentTo = userService.getUserById(requestedUser);
        
        Requests requestEntity = new Requests(loggedInUser, sentTo, "pending");
        
        
        requestService.save(requestEntity);

        List<GardenUser> allUsers = gardenUserService.getUser();
        List<GardenUser> Friends = friendService.getAllFriends(id);
        List<Requests> sentRequests = requestService.getSentRequests(loggedInUserId);
        List<Requests> receivedRequests = requestService.getReceivedRequests(loggedInUserId);
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);

        return "users/manageFriends";
    }

    @PostMapping("users/manageFriends/accept")
    public String manageFriendsAccepts(Authentication authentication, 
        @RequestParam(name = "acceptUser", required = false) Long acceptUser, 
        Model model,
        HttpServletRequest request) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);

        GardenUser receivedFrom = userService.getUserById(acceptUser);
        
        Friends test = new Friends(loggedInUser, receivedFrom);

        friendService.save(test);


        Requests updateStatus = requestService.getRequest(receivedFrom.getId(), loggedInUser.getId());
        requestService.delete(updateStatus);

        List<GardenUser> allUsers = gardenUserService.getUser();
        List<GardenUser> Friends = friendService.getAllFriends(id);
        List<Requests> sentRequests = requestService.getSentRequests(loggedInUserId);
        List<Requests> receivedRequests = requestService.getReceivedRequests(loggedInUserId);
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);

        return "users/manageFriends";
    }

    @PostMapping("users/manageFriends/decline")
    public String manageFriendsDecline(Authentication authentication, 
        @RequestParam(name = "declineUser", required = false) Long declineUser,
        @RequestParam(required = false) String error,
        Model model,    
        HttpServletRequest request) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);

        GardenUser receivedFrom = userService.getUserById(declineUser);
        
        Requests updateStatus = requestService.getRequest(receivedFrom.getId(), loggedInUser.getId());
        updateStatus.setStatus("declined");
        requestService.save(updateStatus);
        
        List<GardenUser> allUsers = gardenUserService.getUser();
        List<GardenUser> Friends = friendService.getAllFriends(id);
        List<Requests> sentRequests = requestService.getSentRequests(loggedInUserId);
        List<Requests> receivedRequests = requestService.getReceivedRequests(loggedInUserId);
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);

        return "users/manageFriends";
    }

    @PostMapping("users/manageFriends/search")
    public String manageFriendsSearch(Authentication authentication, 
        @RequestParam(name = "searchUser", required = false) String searchUser, 
        Model model,
        HttpServletRequest request) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        System.out.println(searchUser);
        
        List<GardenUser> searchResults = userService.getUserBySearch(searchUser);
        List<GardenUser> allUsers = gardenUserService.getUser();
        List<GardenUser> Friends = friendService.getAllFriends(id);
        List<Requests> sentRequests = requestService.getSentRequests(loggedInUserId);
        List<Requests> receivedRequests = requestService.getReceivedRequests(loggedInUserId);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);

        return "users/manageFriends";
    }
}