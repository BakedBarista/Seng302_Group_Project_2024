package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Friends;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

@Controller
public class MessageController {
    private GardenUserService userService;
    private FriendService friendService;


    @Autowired
    public MessageController(GardenUserService userService, FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;
    }



    /**
     * Method to return the message page
     * @param id friend's id to send message to
     * @param model may not need one
     * @return message page
     */
    @GetMapping("users/message")
    public String messageFriend(@RequestParam("id") Long requestedUserId, Authentication authentication,Model model) {
        
        Long loggedInUserId = (Long) authentication.getPrincipal();
        GardenUser sentToUser = userService.getUserById(requestedUserId);

        // we could add so they can send themselves messsages
//        boolean requestingMyself = loggedInUserId.equals(requestedUserId);
//        if (requestingMyself) {
//            return "redirect:/users/manage-friends";
//        }

        // need to be friends to send a message
        Friends isFriend = friendService.getFriendship(loggedInUserId, requestedUserId);
        if (isFriend == null) {
            return "redirect:/users/manage-friends";
        }

        model.addAttribute("sentToUser", sentToUser);
        return "users/message";
    }

}
