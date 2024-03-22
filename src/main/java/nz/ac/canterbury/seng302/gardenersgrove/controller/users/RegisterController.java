package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationSequence;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
     * Shows the user the registration form
     *
     * @return redirect to /demo
     */
    @GetMapping("/users/register")
    public String register(Model model) {
        logger.info("GET /users/register");
        model.addAttribute("user", new GardenUser());
        return "users/registerTemplate";
    }


    /**
     * Handles the submission of user registration form
     *
     * @param fname user's first name
     * @param lname user's last name
     * @param noLname Boolean, true if user has no last name
     * @param email user's email address
     * @param password user's password
     * @param confirmPassword confirmation of the user's password
     * @param dob user's date of birth
     * @param model Thymeleaf model
     * @param request HttpServletRequest object
     * @return  view name for the user registration template or a redirect URL
     */
    @PostMapping("/users/register")
    public String submitRegister(
            @Validated(ValidationSequence.class) @ModelAttribute("user") GardenUser user,
            BindingResult bindingResult,
            @RequestParam(name = "noLname", defaultValue = "false") boolean noLname,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            Model model,
            HttpServletRequest request) {
        logger.info("POST /users/register");


        // if (noLname) {
        //     user.setLname(null);
        // }

        if (user.getDOB().isEmpty()) {
            user.setDOB(null);
        }

        UserValidation userValidation = new UserValidation();
        boolean valid = true;


        // if ((!userValidation.userFirstNameValidation(fname))){
        //     model.addAttribute("incorrectFirstName", "First name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
        //     valid = false;
        // } else if ((fname.length() > maxNameLength)) {
        //     model.addAttribute("firstNameTooLong", "First name must be 64 characters long or less");
        //     valid = false;
        // }

        // if ((!userValidation.userLastNameValidation(lname, noLname))){
        //     model.addAttribute("incorrectLastName", "Last name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
        //     valid = false;
        // } else if (noLname==false && lname.length() > maxNameLength){
        //     model.addAttribute("lastNameTooLong", "Last name must be 64 characters long or less");
        //     valid = false;
        // }

        if (userService.getUserByEmail(user.getEmail()) != null) {
            model.addAttribute("emailInuse", "This email address is already in use");
            valid = false;
        } // else if (!userValidation.userEmailValidation(email)){
        //      model.addAttribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’");
        //     valid = false;
        // }

        if (!userValidation.userPasswordMatchValidation(password, confirmPassword)) {
            model.addAttribute("matchPassword", "Passwords do not match");
            valid = false;
        } else if (!userValidation.userPasswordStrengthValidation(password)){
             model.addAttribute("weakPassword", "Your password must beat least 8 characters long and include at least one uppercase letter, one lowercase letter, one number,and one special character");
             valid = false;
          }

        if (!userValidation.userInvalidDateValidation(user.getDOB())) {
            model.addAttribute("invalidDob", "Date is not in valid format, (DD/MM/YYYY)");
            valid = false;
        } else if (!userValidation.userYoungDateValidation(user.getDOB())) {
            model.addAttribute("youngDob", "You must be 13 years or older to create an account");
            valid = false;
        } else if (!userValidation.userOldDateValidation(user.getDOB())) {
            model.addAttribute("oldDob", "The maximum age allowed is 120 years");
            valid = false;
        }


        if (valid && !bindingResult.hasErrors()) {
            userService.addUser(new GardenUser(user.getFname(), user.getLname(), user.getEmail(), password, user.getDOB()));

            try {
                request.logout();
            } catch (ServletException e) {
                logger.warn("User was not logged in");
            }

            try {
                request.login(user.getEmail(), password);
                return "redirect:/users/user";
            } catch (ServletException e) {
                logger.error("Error while login ", e);
            }
        } else {
            if (bindingResult.hasErrors()) {
                model.addAttribute("user", user);
            }

            return "users/registerTemplate";

            // model.addAttribute("dob", dob);
        }

        return "users/registerTemplate";


    }

    /**
     * Creates a new user for testing purposes
     */
    @PostConstruct
    public void createDummy() {
        try {
            GardenUser user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password",
                    "01/01/1970");
            userService.addUser(user);

            logger.info("Created dummy user for testing purposes");
        } catch (Exception e) {
            logger.error("Error while creating dummy user", e);
        }
    }

}
