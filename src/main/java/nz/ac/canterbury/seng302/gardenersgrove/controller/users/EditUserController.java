package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

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
    private GardenUser user;

    /**
     * Shows the user the form
     * @param model thymeleaf model
     * @return redirect to /demo
     */

    @GetMapping("/users/edit")
    public String edit(Model model) {
        logger.info("GET /users/edit");

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GardenUser user = userService.getUserByEmail(email);

        model.addAttribute("fname", user.getFname());
        model.addAttribute("lname", user.getLname());
        model.addAttribute("noLname", user.getLname());
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
     * @param model thymleaf model
     * @return
     */
    @PostMapping("/users/edit")
    public String submitUser(
            @RequestParam(name = "fname") String fname,
            @RequestParam(name = "lname") String lname,
            @RequestParam(name = "noLname", defaultValue = "true") boolean noLname,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "dob") String dob,
            Model model) {
        logger.info("POST /users/edit");

        // TODO: validation here
        boolean valid = true;
        if (!valid) {
            model.addAttribute("fname", fname);
            model.addAttribute("lname", lname);
            model.addAttribute("noLname", noLname);
            model.addAttribute("email", email);
            model.addAttribute("address", address);
//            model.addAttribute("password", password);
//            model.addAttribute("confirmPassword", confirmPassword);
            model.addAttribute("dob", dob);

//            GardenUser user = userService.getUserByEmail(email);
//            user.setFname(fname);
//            return "users/registerTemplate";

        }

        if (noLname) {
            lname = null;
        }

//        GardenUser user = new GardenUser(fname, lname, email, address, password, dob);
        GardenUser user = userService.getUserByEmail(email);

        return "redirect:/users/login";
    }

}
