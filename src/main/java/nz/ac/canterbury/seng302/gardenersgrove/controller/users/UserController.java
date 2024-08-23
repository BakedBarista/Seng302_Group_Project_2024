package nz.ac.canterbury.seng302.gardenersgrove.controller.users;


import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
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

import java.util.List;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.NZ_FORMAT_DATE;

@Controller
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private GardenUserService userService;

    @Autowired
    private TokenService tokenService;

    private static final String DEFAULT_PROFILE_PICTURE_URL = "/img/default-profile.svg";

    /**
     * Shows the user's profile page
     *
     * @param authentication authentication object representing the current user
     * @param model Thymeleaf model
     * @return view name for the user profile page
     */
    @GetMapping("users/settings")
    public String view(Authentication authentication, Model model) {
        logger.info("GET /users/user");

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("fname", user.getFname());
        model.addAttribute("lname", user.getLname());
        model.addAttribute("email", user.getEmail());

        if (user.getDateOfBirth() != null) {
            String dobString = user.getDateOfBirth().format(NZ_FORMAT_DATE);
            logger.info(dobString);
            model.addAttribute("dateOfBirth", dobString);
        }

        return "users/accountSettings";
    }


    /**
     * Shows a given user's profile
     *
     * @param id the id of the user
     * @return ResponseEntity with the profile picture bytes or a redirect to a default profile picture URL
     */
    @GetMapping("users/{id}/profile-picture")
    public ResponseEntity<byte[]> profilePicture(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.info("GET /users/" + id + "/profile-picture");

        GardenUser user = userService.getUserById(id);
        if (user.getProfilePicture() == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, request.getContextPath() + DEFAULT_PROFILE_PICTURE_URL).build();
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(user.getProfilePictureContentType()))
                .body(user.getProfilePicture());
    }

    @GetMapping("users/{id}/favourite-plants")
    public List<Plant> getFavouritePlants(@PathVariable("id") Long id) {
        GardenUser user = userService.getUserById(id);
        return user.getFavouritePlants();
    }
}
