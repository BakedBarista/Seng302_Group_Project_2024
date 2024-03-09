package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

@Controller
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private GardenUserService userService;

    /**
     * Shows the user the login form
     */
    @GetMapping("users/user")
    public String view(Authentication authentication, Model model) {
        logger.info("GET /users/user");

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("fname", user.getFname());
        model.addAttribute("lname", user.getLname());
        model.addAttribute("email", user.getEmail());

        if (user.getLname() != null) {
            LocalDate dob = LocalDate.parse(user.getDOB());
            String dobString = dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            model.addAttribute("dob", dobString);
        }

        return "users/user";
    }

    private static final String DEFAULT_PROFILE_PICTURE_URL = "https://www.gravatar.com/avatar/5197c9706fccb18e1c912c43172fcf0b?s=100&d=identicon";

    /**
     * Shows a given user's profile picture
     */
    @GetMapping("users/{id}/profile-picture")
    public ResponseEntity<byte[]> profilePicture(@PathVariable("id") Long id) {
        logger.info("GET /users/" + id + "/profile-picture");

        GardenUser user = userService.getUserById(id);
        if (user.getProfilePicture() == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, DEFAULT_PROFILE_PICTURE_URL).build();
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(user.getProfilePictureContentType()))
                .body(user.getProfilePicture());
    }
}