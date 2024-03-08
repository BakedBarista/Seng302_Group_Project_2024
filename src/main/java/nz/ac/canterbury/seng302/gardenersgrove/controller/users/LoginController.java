package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
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

    @PostMapping("/users/login")
    public String authenticateLogin(@RequestParam(name = "email") String email,
                                    @RequestParam(name = "password") String password,
                                    HttpServletRequest request) {
        logger.info("POST /users/login");

        try {
            request.login(email, password);
            return "redirect:/users/user";
        } catch (ServletException e) {
            logger.error("Error while login ", e);
        }

        return "users/login";
    }
}
