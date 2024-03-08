package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
                                          AuthenticationConfiguration authConfiguration) throws Exception {
        logger.info("POST /users/login");

        AuthenticationManager authManager = authConfiguration.getAuthenticationManager();

        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authManager.authenticate(authReq);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);

        if (securityContext.getAuthentication().isAuthenticated()) {
            return "redirect:/users/user";
        }
        return "users/login";
    }
}
