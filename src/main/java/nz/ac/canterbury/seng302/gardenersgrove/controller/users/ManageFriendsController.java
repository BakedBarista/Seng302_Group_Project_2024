package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

import org.apache.catalina.User;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.*;

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

    @Autowired
    public ManageFriendsController(FriendService friendService, GardenUserService userService) {
        this.userService = userService;
        this.friendService = friendService;
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
        List<Friends> sentRequests = friendService.getSentRequests(loggedInUserId);
        List<Friends> receivedRequests = friendService.getReceivedRequests(loggedInUserId);
        // base attributes to set up the page.
        model.addAttribute("friends", Friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("receivedRequests", receivedRequests);
        return "users/manageFriends";
    }

    /**
     * Handles inviting a new friend
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details
     * @param requestedUser  The ID of the user to whom the invitation is sent
     * @return A redirection to the "/users/manageFriends"
     */
    @PostMapping("users/manageFriends/invite")
    public String manageFriendsInvite(Authentication authentication,
            @RequestParam(name = "requestedUser", required = false) Long requestedUser) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser sentTo = userService.getUserById(requestedUser);
        boolean requestingMyself = loggedInUserId == requestedUser;
        if (requestingMyself) {
            return "redirect:/users/manageFriends";
        }
        List<Friends> iDeclinedFriend = friendService.getReceivedRequestsDeclined(loggedInUser.getId());
        for (Friends user2 : iDeclinedFriend) {
            if (sentTo == user2.getUser1()) {
                return "redirect:/users/manageFriends";
            }
        }
        Friends newFriends = new Friends(loggedInUser, sentTo, "pending");
        friendService.save(newFriends);

        return "redirect:/users/manageFriends";
    }

    // sent is 1 request is 2

    /**
     * Handles accepting a new friend.
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details
     * @param acceptUser     The ID of the user whose friend request is being
     *                       accepted
     * @return A redirection to the "/users/manageFriends"
     */
    @PostMapping("users/manageFriends/accept")
    public String manageFriendsAccept(Authentication authentication,
            @RequestParam(name = "acceptUser", required = false) Long acceptUser) {

        Long loggedInUserId = (Long) authentication.getPrincipal();

        List<Friends> sentAndDeclinedList = friendService.getSentRequestsDeclined(loggedInUserId);
        for (Friends request : sentAndDeclinedList) {
            if (request.getUser1().getId() == loggedInUserId && request.getUser2().getId() == acceptUser) {
                friendService.delete(request);
            }
        }

        Friends friendShip = friendService.getFriendship(loggedInUserId, acceptUser);

        if (friendShip != null) {
            friendShip.setStatus("accepted");
            friendService.save(friendShip);
        }

        return "redirect:/users/manageFriends";
    }

    /**
     * Handles the declining a friend request.
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details
     * @param declineUser    The ID of the user whose friend request is being
     *                       declined
     * @return A redirection to the "/users/manageFriends"
     */
    @PostMapping("users/manageFriends/decline")
    public String manageFriendsDecline(Authentication authentication,
            @RequestParam(name = "declineUser", required = false) Long declineUser) {

        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser loggedInUser = userService.getUserById(loggedInUserId);
        GardenUser receivedFrom = userService.getUserById(declineUser);

        if (loggedInUser != null && receivedFrom != null) {
            List<Friends> friendShip = friendService.getReceivedRequests(loggedInUserId);
            for (Friends user : friendShip) {
                if (user.getUser1().getId() == declineUser && user.getUser2().getId() == loggedInUserId) {
                    user.setStatus("declined");
                    friendService.save(user);
                }
            }
        }
        return "redirect:/users/manageFriends";
    }

    /**
     * Handles the search for users to add as friends
     *
     * @param authentication An Authentication object representing the current
     *                       user's authentication details
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
        Optional<GardenUser> checkMyself = userService.checkSearchMyself(searchUser, loggedInUserId);

        List<GardenUser> alreadyFriendsList = new ArrayList<GardenUser>();
        List<GardenUser> requestPendingList = new ArrayList<GardenUser>();
        List<GardenUser> alreadyFriendsDeclineSent = new ArrayList<GardenUser>();
        List<GardenUser> receivedRequestList = new ArrayList<GardenUser>();

        // so we dont get index out of range
        List<GardenUser> copyOfSearchResults = new ArrayList<>(searchResults);

        if (!searchResults.isEmpty()) {
            for (GardenUser user : copyOfSearchResults) {
                Friends requestPending = friendService.getSent(loggedInUserId, user.getId());
                List<Friends> declineSent = friendService.getSentRequestsDeclined(loggedInUserId);
                List<Friends> requestReceived = friendService.getReceivedRequests(loggedInUserId);
                Friends alreadyFriends = friendService.getAcceptedFriendship(loggedInUserId, user.getId());

                if (requestPending != null) {
                    if (Objects.equals(requestPending.getStatus(), "pending")) {
                        requestPendingList.add(user);
                        searchResults.remove(user);
                    }
                }

                
                if (!requestReceived.isEmpty()) {
                    for (Friends user2 : requestReceived) {
                        if (user == user2.getUser1()) {
                            receivedRequestList.add(user);
                            searchResults.remove(user);
                        }
                    }
                } else if (!declineSent.isEmpty()) {
                    for (Friends user2 : declineSent) {
                        if (user == user2.getUser2()) {
                            alreadyFriendsDeclineSent.add(user);
                            searchResults.remove(user);
                        }
                    }
                }
                

                if (alreadyFriends != null) {
                    alreadyFriendsList.add(user);
                    searchResults.remove(user);
                }
            }
        }

        if (checkMyself.isPresent())

        {
            rm.addFlashAttribute("mySelf", checkMyself.get());
        }

        rm.addFlashAttribute("alreadyFriends", alreadyFriendsList);
        rm.addFlashAttribute("requestPending", requestPendingList);
        rm.addFlashAttribute("declineSent", alreadyFriendsDeclineSent);
        rm.addFlashAttribute("requestReceived", receivedRequestList);
        rm.addFlashAttribute("searchResults", searchResults);

        return "redirect:/users/manageFriends";
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