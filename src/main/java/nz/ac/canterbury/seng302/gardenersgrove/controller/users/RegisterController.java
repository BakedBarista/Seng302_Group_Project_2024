package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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

    private GardenUser user;

    private int maxNameLength = 64;



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
            Model model,
            HttpServletRequest request) {
        logger.info("POST /users/register");

        if (noLname) {
            lname = null;
        }

        if (dob.isEmpty()) {
            dob = null;
        }

        UserValidation userValidation = new UserValidation();
        boolean valid = true;


        if ((!userValidation.userFirstNameValidation(fname))){
            model.addAttribute("incorrectFirstName", "First name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
            valid = false;
        } else if ((fname.length() > maxNameLength)) {
            model.addAttribute("firstNameTooLong", "First name must be 64 characters long or less");
            valid = false;
        }

        if ((!userValidation.userLastNameValidation(lname, noLname))){
            model.addAttribute("incorrectLastName", "Last name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
            valid = false;
        } else if (noLname==false && lname.length() > maxNameLength){
            model.addAttribute("lastNameTooLong", "Last name must be 64 characters long or less");
            valid = false;
        }

        if (userService.getUserByEmail(email) != null) {
             model.addAttribute("emailInuse", "This email address is already in use");
            valid = false;
        }else if (!userValidation.userEmailValidation(email)){
             model.addAttribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’");
            valid = false;
        } else if (!userValidation.userPasswordMatchValidation(password, confirmPassword)){
            model.addAttribute("matchPassword", "Passwords do not match");
            valid = false;
        } else if (!userValidation.userPasswordStrengthValidation(password)){
            model.addAttribute("weakPassword", "Your password must beat least 8 characters long and include at least one uppercase letter, one lowercase letter, one number,and one special character");
            valid = false;
         } else if (!userValidation.userInvalidDateValidation(dob)){
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
            userService.addUser(new GardenUser(fname, lname, email, address, password, dob));

            try {
                request.logout();
            } catch (ServletException e) {
                logger.warn("User was not logged in");
            }

            try {
                request.login(email, password);
                return "redirect:/users/user";
            } catch (ServletException e) {
                logger.error("Error while login ", e);
            }
        }

        model.addAttribute("fname", fname);
        model.addAttribute("lname", lname);
        model.addAttribute("noLname", noLname);
        model.addAttribute("email", email);
        model.addAttribute("address", address);
        model.addAttribute("password", password);
        model.addAttribute("confirmPassword", confirmPassword);
        model.addAttribute("dob", dob);

        return "users/registerTemplate";
    }

    /**
     * Submits the form
     */
    @GetMapping("/users/dummy")
    public String createDummy() {
        logger.info("POST /users/dummy");

        GardenUser user = new GardenUser("John", "Doe", "john.doe@gmail.com", "Jack Erskine 133", "password",
                "01/01/1970");
        userService.addUser(user);

        return "redirect:/";
    }

}
