package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.Instant;

import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
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

    @Autowired
    private TokenService tokenService;

    private static final String DEFAULT_PROFILE_PICTURE_URL = "https://www.gravatar.com/avatar/5197c9706fccb18e1c912c43172fcf0b?s=100&d=identicon";

    /**
     * Shows the user's profile page
     *
     * @param authentication authentication object representing the current user
     * @param model Thymeleaf model
     * @return view name for the user profile page
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

        if (user.getDOB() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dob = LocalDate.parse(user.getDOB(), formatter);
            String dobString = dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            model.addAttribute("dob", dobString);
        }

        return "users/user";
    }


    /**
     * Shows a given user's profile
     *
     * @param id the id of the user
     * @return ResponseEntity with the profile picture bytes or a redirect to a default profile picture URL
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

    /**
     * adds a random token and this time instance to a given user in the DB
     * @param userId
     * @return
     */
    public ResponseEntity<Void> addEmailTokenAndTimeToUser(@PathVariable(name = "id") Long userId) {
        logger.info("called addTokenAndTimeToUser");
        String token = tokenService.createEmailToken();

        GardenUser user = userService.getUserById(userId);
        Instant time = Instant.now().plus(10, ChronoUnit.MINUTES);
        user.setEmailValidationToken(token);
        user.setEmailValidationTokenExpiryInstant(time);

        userService.addUser(user);

        return ResponseEntity.ok().build();
    }
}
