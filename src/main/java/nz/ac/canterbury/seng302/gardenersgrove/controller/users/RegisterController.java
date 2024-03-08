package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for registering new users
 */


@Controller
public class RegisterController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private GardenUserService userService;

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
            @RequestParam(name = "lname", required = false) String lname,
            @RequestParam(name = "noLname", defaultValue = "false") boolean noLname,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            @RequestParam(name = "dob", required = false) String dob,
            Model model) {
        logger.info("POST /users/register");

        if (noLname) {
            lname = null;
        }

        if (dob.isEmpty()) {
            dob = null;
        }

        UserValidation userValidation = new UserValidation();

        if (!userValidation.userEmailValidation(email)){
            model.addAttribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’");
            return "users/registerTemplate";
        } else if (!userValidation.userNameValidation(fname, lname, noLname)){
            model.addAttribute("incorrectName", "{First/Last} name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
            return "users/registerTemplate";
        } else if (!userValidation.userPasswordMatchValidation(password, confirmPassword)){
            model.addAttribute("matchPassword", "Passwords do not match");
            return "users/registerTemplate";
        } else if (!userValidation.userPasswordStrengthValidation(password)){
            model.addAttribute("weakPassword", "Your password must beat least 8 characters long and include at least one uppercase letter, one lowercase letter, one number,and one special character");
            return "users/registerTemplate";
        } else if (!userValidation.userYoungDateValidation(dob)){
            model.addAttribute("youngDob", "You must be 13 years or older to create an account");
            return "users/registerTemplate";
        } else if (!userValidation.userOldDateValidation(dob)){
            model.addAttribute("oldDob", "The maximum age allowed is 120 years");
            return "users/registerTemplate";
        } else if (!userValidation.userInvalidDateValidation(dob)){
            model.addAttribute("invalidDob", "Date in not in valid format, (DD/MM/YYYY)");
            return "users/registerTemplate";
        }

        userService.addUser(new GardenUser(fname, lname, email, address, password, dob));
        return "redirect:/users/user";
    }

    /**
     * Submits the form
     */
     @GetMapping("/users/dummy")
     public String createDummy() {
         logger.info("POST /users/dummy");

         GardenUser user = new GardenUser("John", "Doe", "john.doe@gmail.com", "Jack Erskine 133", "password",
                 "1970-01-01");
         userService.addUser(user);

         return "redirect:/";
     }

}
