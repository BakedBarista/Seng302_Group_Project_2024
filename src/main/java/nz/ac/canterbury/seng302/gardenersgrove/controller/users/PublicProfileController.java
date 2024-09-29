package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.FavouritePlantDTO;
import nz.ac.canterbury.seng302.gardenersgrove.exceptions.ProfanityDetectedException;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.BirthFlowerService;
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
import java.util.*;

@Controller
public class PublicProfileController {
    private final Logger logger = LoggerFactory.getLogger(PublicProfileController.class);

    private final GardenUserService userService;
    private final ProfanityService profanityService;
    private final PlantService plantService;
    private final BirthFlowerService birthFlowerService;


    private static final String DEFAULT_PROFILE_BANNER_URL = "/img/default-banner.svg";

    private static final String DEFAULT_GARDEN_IMAGE_URL = "/img/default-garden.svg";

    private static final String USER_ID_ATTRIBUTE = "userId";

    private  static  final  String FAVOURITE_GARDEN = "favouriteGarden";

    private static final String DESCRIPTION = "description";

    private static final String FAVOURITE_PLANTS = "favouritePlants";
    private static final String BIRTH_FLOWER = "birthFlower";

    private static final Set<String> ACCEPTED_FILE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/svg");

    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Autowired
    public PublicProfileController(GardenUserService userService, ProfanityService profanityService, PlantService plantService, BirthFlowerService birthFlowerService) {
        this.userService = userService;
        this.profanityService = profanityService;
        this.plantService = plantService;
        this.birthFlowerService = birthFlowerService;
    }

    /**
     * gets the current user's public profile
     *
     * @return Returns the current user's public profile
     */
    @Transactional
    @GetMapping("/users/public-profile")
    public String viewPublicProfile(Authentication authentication, Model model) {
        logger.info("GET /users/public-profile");

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);
        Set<Plant> favouritePlants = user.getFavouritePlants();
        model.addAttribute(USER_ID_ATTRIBUTE, userId);
        model.addAttribute("currentUser", userId);
        model.addAttribute("name", user.getFullName());
        model.addAttribute(BIRTH_FLOWER, user.getBirthFlower());
        model.addAttribute(DESCRIPTION, user.getDescription());
        model.addAttribute(FAVOURITE_GARDEN, user.getFavoriteGarden());
        model.addAttribute(FAVOURITE_PLANTS, favouritePlants);

        return "users/public-profile";
    }

    /**
     * gets the current favourite garden picture
     * @param id the id of the user
     * @param request the request
     * @return ResponseEntity with the garden image bytes or a redirect to a default garden image URL
     */
    @GetMapping("users/{id}/favourite-garden-image")
    public ResponseEntity<byte[]> favouriteGardenImage(@PathVariable("id") Long id, HttpServletRequest request) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format("GET /users/%d/favourite-garden-image", id));
        }

        GardenUser user = userService.getUserById(id);
        if (user.getFavoriteGarden() == null || user.getFavoriteGarden().getGardenImage() == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, request.getContextPath() + DEFAULT_GARDEN_IMAGE_URL).build();
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(user.getFavoriteGarden().getGardenImageContentType()))
                .body(user.getFavoriteGarden().getGardenImage());
    }

    /**
     * gets a user's public profile
     * @param id the requested user's id
     * @return Returns the selected user's public profile
     */
    @Transactional
    @GetMapping("/users/public-profile/{id}")
    public String viewOtherPublicProfile(@PathVariable("id") Long id, Authentication authentication, Model model) {
        logger.info("GET /users/public-profile/{} - display user's public profile", id);

        GardenUser user = userService.getUserById(id);
        // returns a 404 if id does not exist
        if (user == null) {
            return "error/404";
        }
        Long loggedInUserId = (Long) authentication.getPrincipal();
        boolean isCurrentUser = loggedInUserId.equals(id);
        if (isCurrentUser) {
            return viewPublicProfile(authentication, model);
        }
        String birthFlower = user.getBirthFlower();

        Set<Plant> favouritePlants = user.getFavouritePlants();
        logger.info("current user: {}",userService.getUserById(id).getFname());
        logger.info("logged in user {}",userService.getUserById(loggedInUserId).getFname());
        model.addAttribute(USER_ID_ATTRIBUTE, id);
        model.addAttribute("currentUser", loggedInUserId);
        model.addAttribute("name", user.getFullName());
        model.addAttribute(BIRTH_FLOWER, birthFlower);
        model.addAttribute(FAVOURITE_GARDEN, user.getFavoriteGarden());
        model.addAttribute(DESCRIPTION, user.getDescription());
        model.addAttribute(FAVOURITE_PLANTS, favouritePlants);

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
        logger.info("GET /users/{}/profile-banner", id);

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
    public String editPublicProfile(Authentication authentication, Model model) throws JsonProcessingException {
        logger.info("GET /users/edit-public-profile");
        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);
        EditUserDTO editUserDTO = new EditUserDTO();
        List<String> flowers = birthFlowerService.getFlowersByMonth(user.getDateOfBirth());

        model.addAttribute(USER_ID_ATTRIBUTE, userId);
        model.addAttribute("name", user.getFullName());
        model.addAttribute(BIRTH_FLOWER, user.getBirthFlower());
        editUserDTO.setDescription(user.getDescription());
        model.addAttribute("editUserDTO", editUserDTO);
        model.addAttribute(FAVOURITE_GARDEN, user.getFavoriteGarden());
        model.addAttribute("flowers", flowers);

        Set<Plant> favouritePlants = user.getFavouritePlants();
        model.addAttribute(FAVOURITE_PLANTS, favouritePlants);
        List<FavouritePlantDTO> favouritePlantDTOs = favouritePlants.stream()
                .map(this::convertToFavouritePlantDTO)
                .toList();
        ObjectMapper objectMapper = new ObjectMapper();
        String favouritePlantsJson = objectMapper.writeValueAsString(favouritePlantDTOs);
        model.addAttribute("favouritePlantsJson", favouritePlantsJson);

        return "users/edit-public-profile";
    }

    /**
     * Converts a plant to a FavouritePlantDTO
     * @param plant  the plant to convert
     * @return FavouritePlantDTO
     */
    private FavouritePlantDTO convertToFavouritePlantDTO(Plant plant) {
        return new FavouritePlantDTO(plant.getId(), plant.getName(), plant.getPlantImage());
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
            @RequestParam(DESCRIPTION) String description,
            @RequestParam("selectedFlower") String birthFlower,
            @Valid @ModelAttribute("editUserDTO") EditUserDTO editUserDTO,
            BindingResult bindingResult,
            Model model) throws IOException {
        logger.info("POST /users/edit-public-profile");
        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);
        model.addAttribute(USER_ID_ATTRIBUTE, userId);

        List<String> flowersByMonth = birthFlowerService.getFlowersByMonth(user.getDateOfBirth());

        boolean errorFlag = false;

        try {
            isValidDescription(description);
        } catch (ProfanityDetectedException e) {
            model.addAttribute("profanity", "There cannot be any profanity in the 'About me' section");
            errorFlag = true;
        }
        if (birthFlower.isBlank()) {
            birthFlower = user.getBirthFlower();
        } else if (!flowersByMonth.contains(birthFlower)) {
            birthFlower = birthFlowerService.getDefaultBirthFlower(user.getDateOfBirth());
        }

        if (bindingResult.hasFieldErrors(DESCRIPTION)) {errorFlag = true;}

        if (errorFlag) {
            model.addAttribute(FAVOURITE_GARDEN, user.getFavoriteGarden());
            Set<Plant> favouritePlants = user.getFavouritePlants();
            model.addAttribute(FAVOURITE_PLANTS, favouritePlants);
            List<FavouritePlantDTO> favouritePlantDTOs = favouritePlants.stream()
                    .map(this::convertToFavouritePlantDTO)
                    .toList();
            ObjectMapper objectMapper = new ObjectMapper();
            String favouritePlantsJson = objectMapper.writeValueAsString(favouritePlantDTOs);
            model.addAttribute("favouritePlantsJson", favouritePlantsJson);
            model.addAttribute("name", user.getFullName());
            model.addAttribute("editUserDTO", editUserDTO);
            return "users/edit-public-profile";
        }

        user.setDescription(description);
        editProfilePicture(userId, profilePic);
        editProfileBanner(userId, banner);
        user.setBirthFlower(birthFlower);

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
