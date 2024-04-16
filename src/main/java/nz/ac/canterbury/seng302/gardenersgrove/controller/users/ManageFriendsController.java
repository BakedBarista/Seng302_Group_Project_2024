package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.FriendService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageFriendsController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final FriendService friendService;

    @Autowired
    public ManageFriendsController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * Shows the login page
     *
     * @param error error message, if there's any
     * @param model Thymeleaf model
     * @return login page view
     */
    @GetMapping("users/manageFriends")
    public String login(@RequestParam(required = false) String error,
                        Model model) {
        logger.info("users/manageFriends");
        List<GardenUser> Friends = friendService.getAllFriends(1);
        model.addAttribute("friends", Friends);
        return "users/manageFriends";
    }

}