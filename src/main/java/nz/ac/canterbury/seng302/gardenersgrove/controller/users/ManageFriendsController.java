package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nz.ac.canterbury.seng302.gardenersgrove.entity.Friends.Status.*;

@Controller
public class ManageFriendsController {
    private Logger logger = LoggerFactory.getLogger(ManageFriendsController.class);
    private FriendService friendService;
    private GardenUserService userService;

    @Autowired
    public ManageFriendsController(FriendService friendService, GardenUserService userService) {
        this.userService = userService;
        this.friendService = friendService;
    }

    /**
     * Shows the manage friends page
     *
     * @param model Thymeleaf model
     * @return login page view
     */
    @GetMapping("users/manage-friends")
    public String manageFriends(Authentication authentication,
            Model model) {
        logger.info("users/manage-friends");

        Long loggedInUserId = (Long) authentication.getPrincipal();
        List<GardenUser> allUsers = userService.getUser();
        List<GardenUser> friends = friendService.getAllFriends(loggedInUserId);
        List<Friends> sentRequests = friendService.getSentRequests(loggedInUserId);
        List<Friends> receivedRequests = friendService.getReceivedRequests(loggedInUserId);
        // base attributes to set up the page.
        model.addAttribute("friends", friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);
        return "users/manage-friends";
    }

    /**
     * Handles inviting a new friend
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details
     * @param requestedUserId  The ID of the user to whom the invitation is sent
     * @return A redirection to the "/users/manage-friends"
     */
    @PostMapping("users/manage-friends/invite")
    public String manageFriendsInvite(Authentication authentication,
            @RequestParam(name = "requestedUser", required = false) Long requestedUserId) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser sentToUser = userService.getUserById(requestedUserId);
        
        boolean requestingMyself = loggedInUserId.equals(requestedUserId);
        if (requestingMyself) {
            return "redirect:/users/manage-friends";
        }

        List<Friends> iDeclinedFriend = friendService.getSentRequestsDeclined(loggedInUserId);
        for (Friends receiver : iDeclinedFriend) {
            if (sentToUser.getId().equals(receiver.getReceiver().getId())) {
                return "redirect:/users/manage-friends";
            }
        }

        Friends alreadyFriends = friendService.getAcceptedFriendship(loggedInUserId, requestedUserId);
        if (alreadyFriends != null) {
            return "redirect:/users/manage-friends";
        }

        Friends requestsPending = friendService.getFriendship(loggedInUserId, requestedUserId);
        if (requestsPending != null && requestsPending.getStatus().equals(PENDING)) {
            return "redirect:/users/manage-friends";
        }

        Friends newFriends = new Friends(loggedInUser, sentToUser, PENDING);
        friendService.save(newFriends);

        return "redirect:/users/manage-friends";
    }

    /**
     * Handles accepting a new friend.
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details
     * @param acceptUserId     The ID of the user whose friend request is being
     *                       accepted
     * @return A redirection to the "/users/manage-friends"
     */
    @PostMapping("users/manage-friends/accept")
    public String manageFriendsAccept(Authentication authentication,
            @RequestParam(name = "acceptUser", required = false) Long acceptUserId) {

        Long loggedInUserId = (Long) authentication.getPrincipal();

        List<Friends> sentAndDeclinedList = friendService.getSentRequestsDeclined(loggedInUserId);
        for (Friends request : sentAndDeclinedList) {
            if (request.getSender().getId().equals(loggedInUserId) && request.getReceiver().getId().equals(acceptUserId)) {
                friendService.delete(request);
            }
        }

        Friends alreadyFriends = friendService.getAcceptedFriendship(loggedInUserId, acceptUserId);
        if (alreadyFriends != null) {
            return "redirect:/users/manage-friends";
        }

        Friends friendShip = friendService.getFriendship(loggedInUserId, acceptUserId);

        if (friendShip != null) {
            friendShip.setStatus(ACCEPTED);
            friendService.save(friendShip);
        }

        return "redirect:/users/manage-friends";
    }

    /**
     * Handles the declining a friend request.
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details
     * @param declineUserId    The ID of the user whose friend request is being
     *                       declined
     * @return A redirection to the "/users/manage-friends"
     */
    @PostMapping("users/manage-friends/decline")
    public String manageFriendsDecline(Authentication authentication,
            @RequestParam(name = "declineUser", required = false) Long declineUserId) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser receivedFrom = userService.getUserById(declineUserId);

        Friends alreadyFriends = friendService.getAcceptedFriendship(loggedInUserId, declineUserId);
        if (alreadyFriends != null) {
            return "redirect:/users/manage-friends";
        }

        if (loggedInUser != null && receivedFrom != null) {
            List<Friends> friendShip = friendService.getReceivedRequests(loggedInUserId);
            for (Friends user : friendShip) {
                if (user.getSender().getId().equals(declineUserId) && user.getReceiver().getId().equals(loggedInUserId)) {
                    user.setStatus(DECLINED);
                    friendService.save(user);
                }
            }
        }
        return "redirect:/users/manage-friends";
    }

    /**
     * Handles the search for users to add as friends
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details
     * @param searchUser     The username to search for
     * @param rm             RedirectAttributes used to add flash attributes to
     * @return A redirection to the "/users/manage-friends" .
     */
    @PostMapping("users/manage-friends/search")
    public String manageFriendsSearch(Authentication authentication,
                                      @RequestParam(name = "searchUser", required = false) String searchUser,
                                      RedirectAttributes rm) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        List<GardenUser> searchResults = userService.getUserBySearch(searchUser, loggedInUserId);
        Optional<GardenUser> checkMyself = userService.checkSearchMyself(searchUser, loggedInUserId);

        List<GardenUser> alreadyFriendsList = new ArrayList<>();
        List<GardenUser> requestPendingList = new ArrayList<>();
        List<GardenUser> alreadyFriendsDeclineSent = new ArrayList<>();
        List<GardenUser> receivedRequestList = new ArrayList<>();

        // so we don't get index out of range
        List<GardenUser> copyOfSearchResults = List.copyOf(searchResults);

        for (GardenUser user : copyOfSearchResults) {
            Friends existingRequest = friendService.getSent(loggedInUserId, user.getId());
            if (existingRequest != null) {
                if (existingRequest.getStatus().equals(ACCEPTED)) {
                    alreadyFriendsList.add(user);
                    searchResults.remove(user);
                } else if (existingRequest.getReceiver().getId().equals(loggedInUserId)) {
                    receivedRequestList.add(user);
                    searchResults.remove(user);
                } else if (existingRequest.getStatus().equals(PENDING)) {
                    requestPendingList.add(user);
                    searchResults.remove(user);
                } else if (existingRequest.getStatus().equals(DECLINED)) {
                    alreadyFriendsDeclineSent.add(user);
                    searchResults.remove(user);
                }
            }
        }

        checkMyself.ifPresent(gardenUser -> rm.addFlashAttribute("mySelf", gardenUser));

        rm.addFlashAttribute("alreadyFriends", alreadyFriendsList);
        rm.addFlashAttribute("requestPending", requestPendingList);
        rm.addFlashAttribute("declineSent", alreadyFriendsDeclineSent);
        rm.addFlashAttribute("requestReceived", receivedRequestList);
        rm.addFlashAttribute("searchResults", searchResults);

        return "redirect:/users/manage-friends";
    }

    /**
     * Displays the profile of a friend
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details.
     * @param id             The ID of the friend whose profile is being viewed.
     * @param model          Thymeleaf model
     * @return The name of the view template for displaying the friend's profile.
     */
    @GetMapping("users/friend-profile/{id}")
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
        return "users/friend-profile";
    }

    /**
     * Removes a friend from the user's friend list
     * @param authentication An Authentication object representing the current user's authentication details
     * @param friendId The ID of the friend to be removed
     * @return A redirection to the "/users/manage-friends"
     */
    @PostMapping("users/manage-friends/remove")
    public String removeFriend(Authentication authentication, @RequestParam(name = "friendId") Long friendId) {
        Long loggedInUserId = (Long) authentication.getPrincipal();
        friendService.removeFriend(loggedInUserId, friendId);
        return "redirect:/users/manage-friends";
    }

    /**
     * Cancel an existing friend request
     * @param authentication object contain user's current authentication details
     * @param requestedUser of the user who received the friend request
     */
    @PostMapping("users/manage-friends/cancel")
    public String cancelSentRequest(Authentication authentication,
                                    @RequestParam(name = "userId", required = false) Long requestedUser) {
        Long loggedInUserId = (Long) authentication.getPrincipal();
        logger.info("Canceling request to {}",userService.getUserById(requestedUser).getFname());
        Friends friendship = friendService.getFriendship(loggedInUserId,requestedUser);
        friendService.removeFriendship(friendship);

        return "redirect:/users/manage-friends";
    }
}