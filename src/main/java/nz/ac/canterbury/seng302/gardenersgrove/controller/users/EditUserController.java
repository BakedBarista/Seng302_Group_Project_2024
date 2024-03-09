package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.validation.UserRegoValidation;
import org.apache.catalina.UserDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import org.springframework.security.core.Authentication;


import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.UserRegoValidation;

/**
 * Controller for editing a exsiting user
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

        // Validation
        UserRegoValidation userRegoValidation = new UserRegoValidation();
        boolean valid = true;

        if (!userRegoValidation.userEmailValidation(email)){
            model.addAttribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’");
            valid = false;
        } else if (!userRegoValidation.userNameValidation(fname, lname, noLname)){
            model.addAttribute("incorrectName", "{First/Last} name cannot be empty and must only include letters, spaces,hyphens or apostrophes");
            valid = false;
        } else if (!userRegoValidation.userYoungDateValidation(dob)){
            model.addAttribute("youngDob", "You must be 13 years or older to create an account");
            valid = false;
        } else if (!userRegoValidation.userOldDateValidation(dob)){
            model.addAttribute("oldDob", "The maximum age allowed is 120 years");
            valid = false;
        } else if (!userRegoValidation.userInvalidDateValidation(dob)){
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

        model.addAttribute("fname", fname);
        model.addAttribute("lname", lname);
        model.addAttribute("noLname", noLname);
        model.addAttribute("email", email);
        model.addAttribute("address", address);
        model.addAttribute("dob", dob);

        return "users/editTemplate";
    }

    @PostMapping("/users/edit/password")
    public String submitPassoword(
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

}
