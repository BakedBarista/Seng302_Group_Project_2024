package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import nz.ac.canterbury.seng302.gardenersgrove.controller.DemoController;

/**
 * Controller for registering new users
 */
@Controller
public class RegisterController {
    Logger logger = LoggerFactory.getLogger(DemoController.class);

    /**
     * Shows the user the form
     * @return redirect to /demo
     */
    @GetMapping("/users/register")
    public String register() {
        logger.info("GET /users/register");
        return "users/registerTemplate";
    }

}
