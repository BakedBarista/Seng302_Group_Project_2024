package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ManageFriendsController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    private FriendService friendService;
    private GardenUserService userService;
    private RequestService requestService;

    @Autowired
    public ManageFriendsController(FriendService friendService, GardenUserService userService, RequestService requestService) {
        this.userService = userService;
        this.friendService = friendService;
        this.requestService = requestService;
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
    public String manageFriends(Authentication authentication,
                        Model model) {
        logger.info("users/manageFriends");
        
        Long loggedInUserId = (Long) authentication.getPrincipal();

        List<GardenUser> allUsers = userService.getUser();
        
        List<GardenUser> Friends = friendService.getAllFriends(loggedInUserId);
        List<Requests> sentRequests = requestService.getSentRequests(loggedInUserId);
        List<Requests> receivedRequests = requestService.getReceivedRequests(loggedInUserId);
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);
        return "users/manageFriends";
    }

    @PostMapping("users/manageFriends/invite")
    public String manageFriends(Authentication authentication, 
        @RequestParam(name = "requestedUser", required = false) Long requestedUser) {
        
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser sentTo = userService.getUserById(requestedUser);
        Friends alreadyFriends = friendService.getRequest(loggedInUser.getId(), sentTo.getId());
        Optional<Requests> requestExists = requestService.getRequest(sentTo.getId(), loggedInUser.getId());

        if (!requestExists.isPresent()) {
            if (alreadyFriends == null){
                if(loggedInUser != sentTo){
                    Requests requestEntity = new Requests(loggedInUser, sentTo, "pending");
                    requestService.save(requestEntity);
                }
            }
        }
        return "redirect:/users/manageFriends";
    }

    @PostMapping("users/manageFriends/accept")
    public String manageFriendsAccepts(Authentication authentication, 
        @RequestParam(name = "acceptUser", required = false) Long acceptUser) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser receivedFrom = userService.getUserById(acceptUser);
        Friends newFriends = new Friends(loggedInUser, receivedFrom);

        friendService.save(newFriends);

        Optional<Requests> updateStatusOptional = requestService.getRequest(loggedInUser.getId(), receivedFrom.getId());

        if (updateStatusOptional.isPresent()) {
            System.out.println("gets here");
            Requests updateStatus = updateStatusOptional.get();
            requestService.delete(updateStatus);
        }

        return "redirect:/users/manageFriends";
    }

    @PostMapping("users/manageFriends/decline")
    public String manageFriendsDecline(Authentication authentication, 
        @RequestParam(name = "declineUser", required = false) Long declineUser,
        @RequestParam(required = false) String error) {
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser receivedFrom = userService.getUserById(declineUser);
        Optional<Requests> updateStatusOptional = requestService.getRequest(loggedInUser.getId(), receivedFrom.getId());
        Requests updateStatus = updateStatusOptional.get();
        updateStatus.setStatus("declined");
        requestService.save(updateStatus);
  
        return "redirect:/users/manageFriends";
    }

    @PostMapping("users/manageFriends/search")
    public String manageFriendsSearch(Authentication authentication, 
        @RequestParam(name = "searchUser", required = false) String searchUser, RedirectAttributes rm) {
            
        Long loggedInUserId = (Long) authentication.getPrincipal();
        List<GardenUser> searchResults = userService.getUserBySearch(searchUser, loggedInUserId);

        rm.addFlashAttribute("searchResults", searchResults);
        return "redirect:/users/manageFriends";
    }
}