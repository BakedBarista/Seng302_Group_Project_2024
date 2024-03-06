package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

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
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

/**
 * Controller for editing a exsiting user
 */
@Controller
public class EditUserController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private GardenUserService userService;

    /**
     * Shows the user the form
     */
    @GetMapping("/users/edit")
    public String edit() {
        logger.info("GET /users/edit");
        return "users/editTemplate";
    }

    /**
     * Submits the form
     */
    @PostMapping("/users/edit")
    public String submitUser(
            @RequestParam(name = "fname") String fname,
            @RequestParam(name = "lname") String lname,
            @RequestParam(name = "noLname", defaultValue = "false") boolean noLname,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            @RequestParam(name = "dob") String dob,
            Model model) {
        logger.info("POST /users/edit");

        // TODO: validation here
        boolean valid = password.equals(confirmPassword);
        if (!valid) {
            model.addAttribute("fname", fname);
            model.addAttribute("lname", lname);
            model.addAttribute("noLname", noLname);
            model.addAttribute("email", email);
            model.addAttribute("address", address);
            model.addAttribute("dob", dob);
            return "users/registerTemplate";
        }

        if (noLname) {
            lname = null;
        }
        GardenUser user = new GardenUser(fname, lname, email, address, password, dob);
        userService.addUser(user);

        return "redirect:/users/login";
    }

    /**
     * Shows the user the edit password form
     */
    @GetMapping("/users/edit/password")
    public String editPassword() {
        logger.info("GET /users/edit/password");
        return "users/editPassword";
    }

    @PostMapping("/users/edit/password")
    public String submitPassoword(
            @RequestParam(name = "oldPassword") String oldPassword,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            Model model) {
        logger.info("\n Post /users/edit/password \n");
        // to do validation here 
        logger.info(oldPassword);
        logger.info(newPassword);
        logger.info(confirmPassword);
        long id = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.print("\n" + id + "\n");

        GardenUser user = userService.getUserById(id);
        System.out.print("\n" + user + "\n");
        user.setPassword(newPassword);
        userService.addUser(user);
        return "users/editTemplate";
    }

}
