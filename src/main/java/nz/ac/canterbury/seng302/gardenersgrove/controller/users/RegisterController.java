package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.UserRegoValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

/**
 * Controller for registering new users
 */


@Controller
public class RegisterController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private GardenUserService userService;

    @Autowired
    private GardenUserService gardenUserService;

    /**
     * Shows the user the form
     * 
     * @return redirect to /demo
     */
    @GetMapping("/users/register")
    public String register() {
        logger.info("GET /users/register");
        return "users/registerTemplate";
    }

    /**
     * Submits the form
     */
    @PostMapping("/users/register")
    public String submitRegister(
            @RequestParam(name = "fname") String fname,
            @RequestParam(name = "lname") String lname,
            @RequestParam(name = "noLname", defaultValue = "false") boolean noLname,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            @RequestParam(name = "dob") String dob,
            Model model) {
        logger.info("POST /users/register");

        UserRegoValidation userRegoValidation = new UserRegoValidation();

        if (!userRegoValidation.userEmailValidation(email)){
            model.addAttribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’");
            return "users/registerTemplate";
        } else if (!userRegoValidation.userNameValidation(fname, lname, noLname)){
            model.addAttribute("incorrectName", "{First/Last} name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
            return "users/registerTemplate";
        } else if (!userRegoValidation.userPasswordMatchValidation(password, confirmPassword)){
            model.addAttribute("matchPassword", "Passwords do not match");
            return "users/registerTemplate";
        } else if (!userRegoValidation.userPasswordStrengthValidation(password)){
            model.addAttribute("weakPassword", "our password must beat least 8 characters long and include at least one uppercase letter, one lowercase letter, one number,and one special character");
            return "users/registerTemplate";
        } else if (!userRegoValidation.userYoungDateValidation(dob)){
            model.addAttribute("youndDob", "You must be 13 years orolder to create an account");
            return "users/registerTemplate";
        } else if (!userRegoValidation.userOldDateValidation(dob)){
            model.addAttribute("oldDob", "The maximum age allowed is 120 years");
            return "users/registerTemplate";
        } else if (!userRegoValidation.userInvalidDateValidation(dob)){
            model.addAttribute("invalidDob", "You have entered an invalid date. It must be in the format: DD/MM/YYYY");
            return "users/registerTemplate";
        } else {
            System.out.print("added");
            gardenUserService.addUser(new GardenUser(fname, lname, email, address, password, dob));
            return "redirect:/users/login";
        }

        if (noLname) {
            lname = null;
        }
        GardenUser user = new GardenUser(fname, lname, email, address, password, dob);
        userService.addUser(user);

        return "redirect:/users/login";
    }

    /**
     * Submits the form
     */
    @GetMapping("/users/dummy")
    public String createDummy() {
        logger.info("POST /users/register");

        GardenUser user = new GardenUser("John", "Doe", "john.doe@gmail.com", "Jack Erskine 133", "password",
                "1970-01-01");
        userService.addUser(user);

        return "redirect:/";
    }

}