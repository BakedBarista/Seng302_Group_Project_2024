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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        //base attributes to set up the page.
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);
        return "users/manageFriends";
    }

    /**
     * Handles inviting a new friend
     *
     * @param authentication An Authentication object representing the current user's authentication details
     * @param requestedUser  The ID of the user to whom the invitation is sent
     * @return A redirection to the "/users/manageFriends"
     */
    @PostMapping("users/manageFriends/invite")
    public String manageFriendsInvite(Authentication authentication,
                                @RequestParam(name = "requestedUser", required = false) Long requestedUser) {
        
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser sentTo = userService.getUserById(requestedUser);
        Friends alreadyFriends = friendService.getFriendship(loggedInUser.getId(), sentTo.getId());
        Optional<Requests> requestExists = requestService.getRequest(sentTo.getId(), loggedInUser.getId());
        //checking is they have already been declined/ already friends                           
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

    /**
     * Handles accepting a new friend.
     *
     * @param authentication An Authentication object representing the current user's authentication details
     * @param acceptUser     The ID of the user whose friend request is being accepted
     * @return A redirection to the "/users/manageFriends"
     */
    @PostMapping("users/manageFriends/accept")
    public String manageFriendsAccept(Authentication authentication,
        @RequestParam(name = "acceptUser", required = false) Long acceptUser) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser receivedFrom = userService.getUserById(acceptUser);

        if ( loggedInUser != null && receivedFrom != null ) {
            Optional<Requests> requestExists = requestService.getRequest(loggedInUserId, acceptUser);
            Friends alreadyFriends = friendService.getFriendship(loggedInUserId, acceptUser);

            if (requestExists.isPresent() && alreadyFriends == null) {
                Friends newFriends = new Friends(loggedInUser, receivedFrom);
                friendService.save(newFriends);

                Requests updateStatus = requestExists.get();
                requestService.delete(updateStatus);
            }
        }

        return "redirect:/users/manageFriends";
    }

    /**
     * Handles the declining a friend request.
     *
     * @param authentication An Authentication object representing the current user's authentication details
     * @param declineUser    The ID of the user whose friend request is being declined
     * @return A redirection to the "/users/manageFriends"
    */ 
    @PostMapping("users/manageFriends/decline")
    public String manageFriendsDecline(Authentication authentication, 
        @RequestParam(name = "declineUser", required = false) Long declineUser) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser receivedFrom = userService.getUserById(declineUser);

        if ( loggedInUser != null && receivedFrom != null ) {
            Optional<Requests> optionalRequest = requestService.getRequest(loggedInUserId, declineUser);
            Friends alreadyFriends = friendService.getFriendship(loggedInUserId, declineUser);

            if (optionalRequest.isPresent() && alreadyFriends == null) {
                Requests request = optionalRequest.get();
                request.setStatus("declined");
                requestService.save(request);
            }
        }
        return "redirect:/users/manageFriends";
    }

    /**
     * Handles the search for users to add as friends
     *
     * @param authentication An Authentication object representing the current user's authentication details
     * @param searchUser     The username to search for
     * @param rm             RedirectAttributes used to add flash attributes to
     * @return A redirection to the "/users/manageFriends" .
     */
    @PostMapping("users/manageFriends/search")
    public String manageFriendsSearch(Authentication authentication,
                                      @RequestParam(name = "searchUser", required = false) String searchUser,
                                      RedirectAttributes rm) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        List<GardenUser> searchResults = userService.getUserBySearch(searchUser, loggedInUserId);

        rm.addFlashAttribute("searchResults", searchResults);
        return "redirect:/users/manageFriends";
    }

    /**
     * Displays the profile of a friend
     *
     * @param authentication An Authentication object representing the current user's authentication details.
     * @param id             The ID of the friend whose profile is being viewed.
     * @param model          Thymeleaf model
     * @return The name of the view template for displaying the friend's profile.
     */
    @GetMapping("users/friendProfile/{id}")
    public String viewFriendProfile(Authentication authentication,
                                    @PathVariable() Long id,
                                    Model model) {
        Long loggedInUserId = (Long) authentication.getPrincipal();
        Friends isFriend = friendService.getFriendship(loggedInUserId, id);
        var friend = userService.getUserById(id);
        // a user cannot view another non friends page
        if (isFriend == null) {
            return "redirect:/";
        }
        
        model.addAttribute("Friend", friend);
        return "users/friendProfile";
    }
}