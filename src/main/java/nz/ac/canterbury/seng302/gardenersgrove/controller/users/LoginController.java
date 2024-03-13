package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private GardenUserService userService;


    /**
     * Shows the login page
     * @param error error message, if there's any
     * @param model Thymeleaf model
     * @return login page view
     */
    @GetMapping("users/login")
    public String login(@RequestParam(required = false) String error,
                        Model model) {
        logger.info("GET /users/login");
        return "users/login";
    }

    /**
     * Authenticates user login
     * @param email user's email address
     * @param password user's password
     * @param error error message
     * @param model Thymeleaf model
     * @param request HttpServletRequest object
     * @return The view name for the login page or a redirect URL
     */
    @PostMapping("/users/login")
    public String authenticateLogin(@RequestParam(name = "email") String email,
                                    @RequestParam(name = "password") String password,
                                    @RequestParam(required = false) String error,
                                    Model model,
                                    HttpServletRequest request) {
        logger.info("POST /users/login");

        UserValidation userValidation = new UserValidation();

        if (!userValidation.userEmailValidation(email)) {
            model.addAttribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’");
            return "users/login";
        }

        GardenUser user = userService.getUserByEmailAndPassword(email, password);

        if (user == null) {
            model.addAttribute("invalidCredentials", "The email address is unknown, or the password is invalid");
            return "users/login";
        }

        try {
            request.logout();
        } catch (ServletException e) {
            logger.warn("User was not logged in");
        }

        try {
            request.login(email, password);
            return "redirect:/users/user";
        } catch (ServletException e) {
            logger.error("Error while login ", e);
        }

        return "users/login";
    }
}
