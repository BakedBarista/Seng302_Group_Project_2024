package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Shows the user the login form
     */
    @GetMapping("users/user")
    public String login() {
        logger.info("GET /users/user");
        return "users/user";
    }
}
