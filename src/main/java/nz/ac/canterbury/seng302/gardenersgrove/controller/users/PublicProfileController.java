package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
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

import static nz.ac.canterbury.seng302.gardenersgrove.validation.DateTimeFormats.NZ_FORMAT_DATE;

@Controller
public class PublicProfileController {
    private final Logger logger = LoggerFactory.getLogger(PublicProfileController.class);

    private final GardenUserService userService;

    private static final String DEFAULT_PROFILE_BANNER_URL = "/img/default-banner.svg";

    @Autowired
    public PublicProfileController(GardenUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/public-profile")
    public String viewPublicProfile(Authentication authentication, Model model) {
        logger.info("GET /users/public-profile");

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("name", user.getFname() + " " + user.getLname());
        model.addAttribute("description", user.getDescription());

        return "users/public-profile";
    }

    /**
     * returns a given user's banner - this is useful for the public profile view and edit page
     *
     * @param id the id of the user
     * @return ResponseEntity with the profile banner bytes or a redirect to a default profile banner URL
     */
    @GetMapping("users/{id}/profile-banner")
    public ResponseEntity<byte[]> getPublicProfileBanner(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.info("GET /users/" + id + "/profile-banner");

        GardenUser user = userService.getUserById(id);
        if (user.getProfileBanner() == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, request.getContextPath() + DEFAULT_PROFILE_BANNER_URL).build();
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(user.getProfileBannerContentType()))
                .body(user.getProfileBanner());
    }
}
