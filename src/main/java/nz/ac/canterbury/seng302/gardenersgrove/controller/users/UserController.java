package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

@Controller
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private GardenUserService userService;

    /**
     * Shows the user the login form
     */
    @GetMapping("users/user")
    public String login(Principal principal, Model model) {
        logger.info("GET /users/user");

        String email = principal.getName();
        GardenUser user = userService.getUserByEmail(email);

        model.addAttribute("fname", user.getFname());
        model.addAttribute("lname", user.getLname());
        model.addAttribute("email", user.getEmail());

        if (user.getLname() != null) {
            LocalDate dob = LocalDate.parse(user.getDOB());
            String dobString = dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            model.addAttribute("dob", dobString);
        }

        return "users/user";
    }
}
