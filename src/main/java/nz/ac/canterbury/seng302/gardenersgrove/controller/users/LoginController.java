package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Shows the user the login form
     */
    @GetMapping("users/login")
    public String login(@RequestParam(required = false) String error,
                        Model model) {
        logger.info("GET /users/login");

        if (error != null) {
            model.addAttribute("invalidCredentials", "The email address is unknown, or the password is invalid");
            return "users/login";
        }
        return "users/login";
    }
}
