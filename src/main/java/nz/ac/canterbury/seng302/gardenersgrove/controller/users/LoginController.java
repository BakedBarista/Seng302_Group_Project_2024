package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private GardenUserService gardenUserService;


    /**
     * Shows the user the login form
     */
    @GetMapping("users/login")
    public String login() {
        logger.info("GET /users/login");
        return "users/login";
    }

    public String submitLogin(
            @RequestParam(name="email") String email,
            @RequestParam(name="password") String password,
            Model model) {
        logger.info("POST /users/login");

        model.addAttribute("email", email);
        model.addAttribute("password", password);

        GardenUser user = gardenUserService.getUserByEmailAndPassword(email, password);
        if (user == null) {
            model.addAttribute("invalidCredentials", "Invalid username or password.");
            return "users/login";
        }
        return "redirect:/users/userProfile";
    }
}
