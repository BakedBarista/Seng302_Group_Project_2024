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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

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

    public void setUserService(GardenUserService userService) {
        this.userService = userService;
    }

    /**
     * Shows the user the form
     * @param model thymeleaf model
     * @return redirect to /demo
     */
    @GetMapping("/users/edit")
    public String edit(Authentication authentication, Model model) {
        logger.info("GET /users/edit");

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("fname", user.getFname());
        model.addAttribute("lname", user.getLname());
        model.addAttribute("noLname", user.getLname() == null);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("dob", user.getDOB());

        return "users/editTemplate";
    }

    /**
     *
     * @param fname user's current first name
     * @param lname user's current last name
     * @param noLname true if user has no last name
     * @param email user's current email
     * @param dob user's current date of birth
     * @param model thymeleaf model
     * @return
     */
    @PostMapping("/users/edit")
    public String submitUser(
            @RequestParam(name = "fname") String fname,
            @RequestParam(name = "lname", required = false) String lname,
            @RequestParam(name = "noLname", defaultValue = "false") boolean noLname,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "dob") String dob,
            Authentication authentication, Model model) {
        logger.info("POST /users/edit");

        isNoLname = noLname;
        Long userId = (Long) authentication.getPrincipal();

        if (noLname) {
            lname = null;
        }
        if (dob.isEmpty()) {
            dob = null;
        }

        user = userService.getUserById(userId);
        // Validation
        String currentEmail = user.getEmail();
        UserValidation userValidation = new UserValidation();
        boolean valid = true;

        if (!email.equalsIgnoreCase(currentEmail)) {
            if (userService.getUserByEmail(email) != null) {
                model.addAttribute("emailInuse", "This email address is already in use");
                valid = false;
            } else if (!userValidation.userEmailValidation(email)) {
                model.addAttribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’");
                valid = false;
            }
        }

        if ((!userValidation.userFirstNameEmptyValidation(fname))){
            model.addAttribute("emptyFirstName", "First name cannot be empty");
            valid = false;
        } else if (!userValidation.userFirstNameWrongCharactersValidation(fname)){
            model.addAttribute("wrongCharFirstName", "First name must only include letters, spaces,hyphens or apostrophes");
            valid = false;
        } else if ((fname.length() > maxNameLength)) {
            model.addAttribute("firstNameTooLong", "First name must be 64 characters long or less");
            valid = false;
        }

        if ((!userValidation.userLastNameEmptyValidation(lname, noLname))){
            model.addAttribute("emptyLastName", "Last name cannot be empty");
            valid = false;
        } else if (!userValidation.userLastNameWrongCharactersValidation(lname, noLname)){
            model.addAttribute("wrongCharLastName", "Last name must only include letters, spaces,hyphens or apostrophes");
            valid = false;
        } else if (noLname==false && lname.length() > maxNameLength){
            model.addAttribute("lastNameTooLong", "Last name must be 64 characters long or less");
            valid = false;
        }

        if (!userValidation.userInvalidDateValidation(dob)){
            model.addAttribute("invalidDob", "Date is not in valid format, (DD/MM/YYYY)");
            valid = false;
        } else if (!userValidation.userYoungDateValidation(dob)){
            model.addAttribute("youngDob", "You must be 13 years or older to create an account");
            valid = false;
        } else if (!userValidation.userOldDateValidation(dob)){
            model.addAttribute("oldDob", "The maximum age allowed is 120 years");
            valid = false;
        }

        if (valid) {
            GardenUser user = userService.getUserById(userId);
            user.setFname(fname);
            user.setLname(lname);
            user.setEmail(email);
            user.setDOB(dob);
            userService.addUser(user);

            return "redirect:/users/user";

        }

        model.addAttribute("userId", userId);
        model.addAttribute("fname", fname);
        model.addAttribute("lname", lname);
        model.addAttribute("noLname", noLname);
        model.addAttribute("email", email);
        model.addAttribute("dob", dob);

        return "users/editTemplate";
    }

    /**
     * Shows the user the form
     */
    @GetMapping("/users/edit/password")
    public String editPassword() {
        logger.info("GET /users/edit/password");

        return "users/editPassword";
    }

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
     * Shows the user the edit password form
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
