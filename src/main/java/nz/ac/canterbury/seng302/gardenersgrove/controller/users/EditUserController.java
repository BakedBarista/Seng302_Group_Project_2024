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
import org.springframework.security.core.Authentication;


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

        if (user.getLname() == null) {
            model.addAttribute("noLname", true);
        }
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

        // TODO: validation here
        boolean valid = true;
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

        return "redirect:/users/user";
    }

}
