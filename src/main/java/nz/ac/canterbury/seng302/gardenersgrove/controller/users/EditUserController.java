package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationSequence;

import org.springframework.web.multipart.MultipartFile;

import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

/**
 * Controller for editing an existing user
 */
@Controller
public class EditUserController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private GardenUserService userService;
    private GardenUser user;

    private Boolean isNoLname;

    private int maxNameLength = 64;

    /**
     * Setter method for userServer
     *
     * @param userService The GardenUserService to be set
     */
    public void setUserService(GardenUserService userService) {
        this.userService = userService;
    }


    /**
     * Shows the edit user form
     *
     * @param authentication authentication object representing the current user
     * @param model Thymeleaf model
     * @return The edit user template view
     */
    @GetMapping("/users/edit")
    public String edit(Authentication authentication, Model model) {
        logger.info("GET /users/edit");

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);
        model.addAttribute("user", new GardenUser());
        model.addAttribute("userId", userId);
        model.addAttribute("fname", user.getFname());
        model.addAttribute("lname", user.getLname());
        model.addAttribute("noLname", user.getLname() == null);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("dob", user.getDOB());

        return "users/editTemplate";
    }


    /**
     * Handles the submission of user edits
     *
     * @param fname user's current first name
     * @param lname user's current last name
     * @param noLname True if the user has no last name
     * @param email user's current email
     * @param dob user's current date of birth
     * @param authentication authentication object representing the current user
     * @param model the Thymeleaf model
     * @return The view name for the edit user template or a redirect URL
     */
    @PostMapping("/users/edit")
    public String submitUser(
            @Validated(ValidationSequence.class) @ModelAttribute("user") GardenUser userSubmit,
            BindingResult bindingResult,
            @RequestParam(name = "noLname", defaultValue = "false") boolean noLname,
            Authentication authentication, Model model) {
        logger.info("POST /users/edit");

        isNoLname = noLname;
        Long userId = (Long) authentication.getPrincipal();

        // if (noLname) {
        //     lname = null;
        // }
        if (user.getDOB().isEmpty()) {
            user.setDOB(null);
        }

        user = userService.getUserById(userId);
        // Validation
        String currentEmail = user.getEmail();
        UserValidation userValidation = new UserValidation();
        boolean valid = true;

        if (!userSubmit.getEmail().equalsIgnoreCase(currentEmail)) {
            if (userService.getUserByEmail(user.getEmail()) != null) {
                model.addAttribute("emailInuse", "This email address is already in use");
                valid = false;
            } 
        }

        if (!userValidation.userInvalidDateValidation(userSubmit.getDOB())){
            model.addAttribute("invalidDob", "Date is not in valid format, (DD/MM/YYYY)");
            valid = false;
        } else if (!userValidation.userYoungDateValidation(userSubmit.getDOB())){
            model.addAttribute("youngDob", "You must be 13 years or older to create an account");
            valid = false;
        } else if (!userValidation.userOldDateValidation(userSubmit.getDOB())){
            model.addAttribute("oldDob", "The maximum age allowed is 120 years");
            valid = false;
        }

        if (valid && !bindingResult.hasErrors()) {
            GardenUser userDetails = userService.getUserById(userId);
            user.setFname(userSubmit.getFname());
            user.setLname(userSubmit.getLname());
            user.setEmail(userSubmit.getEmail());
            user.setDOB(userSubmit.getDOB());
            userService.addUser(user);

            return "redirect:/users/user";

        } else {
            if (bindingResult.hasErrors()) {
                model.addAttribute("user", user);
            }
            return "users/editTemplate";
        }   

    }

    /**
     * Shows the user the form
     */
    @GetMapping("/users/edit/password")
    public String editPassword() {
        logger.info("GET /users/edit/password");

        return "users/editPassword";
    }

    /**
     * Handles submission of password edits
     *
     * @param oldPassword user's current password
     * @param newPassword user's new password
     * @param confirmPassword confirmation of the new password
     * @param model Thymeleaf model
     * @return edit user template
     */
    @PostMapping("/users/edit/password")
    public String submitPassword(
            @RequestParam(name = "oldPassword") String oldPassword,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            Model model) {

        long id = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserValidation userRegoValidation = new UserValidation();

        GardenUser user = userService.getUserById(id);

        boolean valid = true;

        if(!user.checkPassword(oldPassword)){
            model.addAttribute("incorrectOld", "Your old password is incorrect");
            valid = false;
        }

        if(!userRegoValidation.userPasswordMatchValidation(newPassword, confirmPassword)){
            model.addAttribute("incorrectMatch", "The new passwords do not match");
            valid = false;
        }else if(!userRegoValidation.userPasswordStrengthValidation(newPassword)){
            model.addAttribute("incorrectStrength", "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character");
            valid = false;
        }

        if (valid) {
            user.setPassword(newPassword);
            userService.addUser(user);
            return "users/editPassword";
        }

        return "users/editPassword";
    }


    /**
     * Handles the submission of profile picture edits
     *
     * @param authentication authentication object representing the current user
     * @param file the MultipartFile containing the new profile picture
     * @param referer the referer header value
     * @return A redirect URL
     * @throws IOException
     */
    @PostMapping("/users/profile-picture")
    public String editProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(HttpHeaders.REFERER) String referer) throws IOException {
        logger.info("POST /users/profile-picture");

        Long userId = (Long) authentication.getPrincipal();

        userService.setProfilePicture(userId, file.getContentType(), file.getBytes());

        return "redirect:" + referer;
    }

}
