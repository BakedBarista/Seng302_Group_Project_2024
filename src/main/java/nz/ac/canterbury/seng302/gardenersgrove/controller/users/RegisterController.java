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
    Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private GardenUserService gardenUserService;

    /**
     * Shows the user the form
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
        @RequestParam(name="fname") String fname,
        @RequestParam(name="lname") String lname,
        @RequestParam(name="noLname", defaultValue = "false") boolean noLname,
        @RequestParam(name="email") String email,
        @RequestParam(name="address") String address,
        @RequestParam(name="password") String password,
        @RequestParam(name="confirmPassword") String confirmPassword,
        @RequestParam(name="dob") String dob,
        Model model
    ) {
        logger.info("POST /users/register");

        UserValidation userValidation = new UserValidation();

        if (!userValidation.userEmailValidation(email)){
            model.addAttribute("incorrectEmail", "Email address must bein the form ‘jane@doe.nz’");
            System.out.print("wronngngngngng email");
            return "users/registerTemplate";
        } else if (!userValidation.userNameValidation(fname, lname)){
            model.addAttribute("incorrectName", "{First/Last} name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
            System.out.print("wronngngngngng name");
            return "users/registerTemplate";
        } else if (!userValidation.userPasswordMatchValidation(password, confirmPassword)){
            model.addAttribute("matchPassword", "Passwords do not match");
            System.out.print("wronngngngngng match");
            return "users/registerTemplate";
        } else if (!userValidation.userPasswordStrengthValidation(password)){
            model.addAttribute("weakPassword", "our password must beat least 8 characters long and include at least one uppercase letter, one lowercase letter, one number,and one special character");
            System.out.print("wronngngngngng strengh");
            return "users/registerTemplate";
        } else if (!userValidation.userYoungDateValidation(dob)){
            model.addAttribute("youndDob", "You must be 13 years orolder to create an account");
            System.out.print("wronngngngngng young");
            return "users/registerTemplate";
        } else if (!userValidation.userOldDateValidation(dob)){
            model.addAttribute("oldDob", "The maximum age allowed is 120 years");
            System.out.print("wronngngngngng age");
            return "users/registerTemplate";
        } else {
            System.out.print("added");
            gardenUserService.addUser(new GardenUser(fname, lname, email, address, password, dob));
            return "redirect:/users/login";
        }

       }   
}