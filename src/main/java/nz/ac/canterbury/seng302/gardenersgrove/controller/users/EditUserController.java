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
        model.addAttribute("address", user.getAddress());
        model.addAttribute("dob", user.getDOB());

        return "users/editTemplate";
    }

    /**
     *
     * @param fname user's current first name
     * @param lname user's current last name
     * @param noLname true if user has no last name
     * @param email user's current email
     * @param address user's current address
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
            @RequestParam(name = "address") String address,
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

        // Validation
        UserValidation userValidation = new UserValidation();
        boolean valid = true;

        if (!userValidation.userFirstNameValidation(fname)){
            model.addAttribute("incorrectFirstName", "First name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
            valid = false;
        } else if (!userValidation.userLastNameValidation(lname, noLname)){
            model.addAttribute("incorrectLastName", "Last name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
            valid = false;
        } else if (!userValidation.userEmailValidation(email)){
            model.addAttribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’");
            valid = false;
        } else if (!userValidation.userYoungDateValidation(dob)){
            model.addAttribute("youngDob", "You must be 13 years or older to create an account");
            valid = false;
        } else if (!userValidation.userOldDateValidation(dob)){
            model.addAttribute("oldDob", "The maximum age allowed is 120 years");
            valid = false;
        } else if (!userValidation.userInvalidDateValidation(dob)){
            model.addAttribute("invalidDob", "You have entered an invalid date. It must be in the format: DD/MM/YYYY");
            valid = false;
        }

        if (valid) {
            GardenUser user = userService.getUserById(userId);
            user.setFname(fname);
            user.setLname(lname);
            user.setEmail(email);
            user.setAddress(address);
            user.setDOB(dob);
            userService.addUser(user);

            return "redirect:/users/user";

        }

        model.addAttribute("userId", userId);
        model.addAttribute("fname", fname);
        model.addAttribute("lname", lname);
        model.addAttribute("noLname", noLname);
        model.addAttribute("email", email);
        model.addAttribute("address", address);
        model.addAttribute("dob", dob);

        return "users/editTemplate";
    }

    @PostMapping("/users/edit/password")
    public String submitPassword(
            @RequestParam(name = "oldPassword") String oldPassword,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            Model model) {
        long id = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        GardenUser user = userService.getUserById(id);
        user.setPassword(newPassword);
        userService.addUser(user);
        return "users/editTemplate";
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
