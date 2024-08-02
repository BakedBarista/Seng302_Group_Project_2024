package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class PublicProfileController {
    private final Logger logger = LoggerFactory.getLogger(PublicProfileController.class);

    private final GardenUserService userService;

    private static final String DEFAULT_PROFILE_BANNER_URL = "/img/default-profile.svg";
    
    private static final String USER_ID_ATTRIBUTE = "userId";

    @Autowired
    public PublicProfileController(GardenUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/public-profile")
    public String viewPublicProfile(Authentication authentication, Model model) {
        logger.info("GET /users/public-profile");

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);

        model.addAttribute(USER_ID_ATTRIBUTE, userId);
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


    /**
     * returns the edit-public-profile page
     *
     * @return A redirection to the "/users/edit-public-profile"
     */
    @GetMapping("users/edit-public-profile")
    public String editPublicProfile(Authentication authentication,
                                    Model model) {

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);
        EditUserDTO editUserDTO = new EditUserDTO();

        model.addAttribute(USER_ID_ATTRIBUTE, userId);
        editUserDTO.setDescription(user.getDescription());
        model.addAttribute("editUserDTO", editUserDTO);

        return "users/edit-public-profile";
    }

    /**
     * returns the edit-public-profile page
     *
     * @param profilePic the profile picture of the user
     * @param banner the banner of the user
     * @param description the description of the user
     * @return A redirection to the "/users/public-profile"
     */

    @PostMapping("users/edit-public-profile")
    public String publicProfileEditSubmit(Authentication authentication, 
        @RequestParam("image") MultipartFile profilePic,
        @RequestParam("bannerImage") MultipartFile banner,
        @RequestParam("description") String description,
        Model model) throws IOException {

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);

        EditUserDTO editUserDTO = new EditUserDTO();
        model.addAttribute("editUserDTO", editUserDTO);
        model.addAttribute(USER_ID_ATTRIBUTE, userId);
        
        // for submission of profile picture
        editProfilePicture(userId, profilePic);
        
        // for submission of banner
        editProfileBanner(userId, banner);
        

        user.setDescription(description);
        userService.addUser(user);
        return "redirect:/users/public-profile";
    }
    
    /**
     * Handles the submission of profile picture edits
     * @param userId id of the user to update picture
     * @param file the MultipartFile containing the new profile picture
     */
    
     public void editProfilePicture(Long userId, MultipartFile file) throws IOException{
        logger.info("POST /users/profile-picture");
        if(file.getSize() != 0){
            userService.setProfilePicture(userId, file.getContentType(), file.getBytes());
        }
    }

    /**
     * Handles the submission of profile picture edits
     * @param userId id of the user to update picture
     * @param file the MultipartFile containing the new profile picture
     * @throws IOException
     */
    
     public void editProfileBanner(Long userId, MultipartFile file) throws IOException{
        logger.info("POST /users/edit-banner-picture");
        if(file.getSize() != 0){
            userService.setProfileBanner(userId, file.getContentType(), file.getBytes());
        }
    }
}
