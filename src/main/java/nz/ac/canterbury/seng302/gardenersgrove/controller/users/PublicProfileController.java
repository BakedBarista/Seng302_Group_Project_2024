package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.exceptions.ProfanityDetectedException;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ProfanityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Controller
public class PublicProfileController {
    private final Logger logger = LoggerFactory.getLogger(PublicProfileController.class);

    private final GardenUserService userService;
    private final ProfanityService profanityService;

    private static final String DEFAULT_PROFILE_BANNER_URL = "/img/default-banner.svg";
    
    private static final String USER_ID_ATTRIBUTE = "userId";

    private static final Set<String> ACCEPTED_FILE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/svg");

    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;



    @Autowired
    public PublicProfileController(GardenUserService userService, ProfanityService profanityService) {
        this.userService = userService;
        this.profanityService = profanityService;
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
    public String editPublicProfile(Authentication authentication, Model model) {

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);
        EditUserDTO editUserDTO = new EditUserDTO();

        model.addAttribute(USER_ID_ATTRIBUTE, userId);
        model.addAttribute("name", user.getFname() + " " + user.getLname());
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
    public String publicProfileEditSubmit(
            Authentication authentication,
            @RequestParam("image") MultipartFile profilePic,
            @RequestParam("bannerImage") MultipartFile banner,
            @RequestParam("description") String description,
            @Valid @ModelAttribute("editUserDTO") EditUserDTO editUserDTO,
            BindingResult bindingResult,
            Model model) throws IOException {

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);
        model.addAttribute(USER_ID_ATTRIBUTE, userId);

        boolean errorFlag = false;

        try {
            isValidDescription(description);
        } catch (ProfanityDetectedException e) {
            errorFlag = true;
        }

        if (bindingResult.hasFieldErrors("description")) {errorFlag = true;}

        if (errorFlag) {
            model.addAttribute("name", user.getFname() + " " + user.getLname());
            model.addAttribute("editUserDTO", editUserDTO);
            model.addAttribute("profanity", "There cannot be any profanity in the 'About me' section");
            return "users/edit-public-profile";
        }

        user.setDescription(description);
        editProfilePicture(userId, profilePic);
        editProfileBanner(userId, banner);

        userService.addUser(user);

        return "redirect:/users/public-profile";
    }
    
    /**
     * Handles the submission of profile picture edits
     * @param userId id of the user to update picture
     * @param file the MultipartFile containing the new profile picture
     * @throws IOException
     */
     public void editProfilePicture(Long userId, MultipartFile file) throws IOException{
        logger.info("POST /users/profile-picture");
         if(file.getSize() != 0 && validateImage(file)){
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
        if(file.getSize() != 0 && validateImage(file)){
            userService.setProfileBanner(userId, file.getContentType(), file.getBytes());
        }
    }

    /**
     * Validate an image on the server side to be > 10MB
     * and a valid file type (png, svg, jpg, jpeg
     * @param image image to be validated
     * @return true if it is valid
     */
    public boolean validateImage(MultipartFile image) {
        return ACCEPTED_FILE_TYPES.contains(image.getContentType())
                && (image.getSize() <= MAX_FILE_SIZE);
    }
    
    /**
     * Validates the tag if it contains invalid characters and checks the length
     *
     * @param description The name of the tag.
     */
    public void isValidDescription(String description) throws ProfanityDetectedException {
        boolean profanityExists = !(profanityService.badWordsFound(description).isEmpty());
        if (profanityExists) {
            throw new ProfanityDetectedException();
        }
    }
}
