package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private GardenUserService userService;

    /**
     * Check to see if the user has a token, if they do take the user to the authentication page.
     * This will be used after creating a new account - before being authenticated
     * @param userId
     * @param model
     * @return authentication page
     */
    @GetMapping("/users/user/{userId}/authenticate-email")
    public String authenticateEmail(@PathVariable("userId") Long userId, Model model) {
        // If the user has an authentication token
        GardenUser user = userService.getUserById(userId);

        if (user != null  && user.getEmailValidationToken() != null) {
            logger.info("Displaying hidden email as {}", hideEmail(user.getEmail()));
            model.addAttribute("userId", userId);
            model.addAttribute("hiddenEmail", hideEmail(user.getEmail()));
            return "authentication/emailAuthentication";
        } else {
            return "error/404";
        }
    }

    /**
     * Check the given token against the user in DB, if their token is matching,
     * then authenticate the user (via removing the token and time instance from DB)
     * otherwise, send the user back to the authentication page
     * @param userId
     * @param authenticationToken
     * @return home page if authenticated, otherwise authentication page
     */
    @PostMapping("/users/user/{userId}/authenticate-email")
    public String validateAuthenticationToken(@PathVariable("userId") Long userId,
                                              @ModelAttribute("authenticationToken") String authenticationToken,
                                              RedirectAttributes redirectAttributes,
                                              Model model) {
        logger.info("authenticating token {} for user {}", authenticationToken, userId);

        // check if token matches token in DB
        GardenUser user = userService.getUserById(userId);

        // token has expired
        if (user == null) {
            model.addAttribute("tokenExpired", true);
            logger.info("User entered an expired token");
            return "authentication/emailAuthentication";
        }

        // User not null so show hidden email
        boolean authenticated = user.getEmailValidationToken().equals(authenticationToken);

        logger.info("authentication: {}", authenticated);

        if (authenticated) {
            // remove token and time instant
            user.setEmailValidationToken(null);
            user.setEmailValidationTokenExpiryInstant(null);
            userService.addUser(user);

            redirectAttributes.addFlashAttribute("justAuthenticated", true);
            return "redirect:/users/login";
        }
        else {
            model.addAttribute("hiddenEmail", hideEmail(user.getEmail()));
            model.addAttribute("tokenIncorrect", true);
            logger.info("User entered an incorrect token");
            return "authentication/emailAuthentication";
        }
    }

    /**
     * Converts the given email to *xxx*@gmail.com, hiding all characters but the first and last, replacing the rest
     * with stars.
     * @param email the email string to hide
     * @return a string with the hidden email
     */
    private String hideEmail(String email) {
        String startOfEmail = email.split("@")[0];
        String endOfEmail = "@" + email.split("@")[1];
        char firstChar = startOfEmail.charAt(0);

        if (startOfEmail.length() == 1) {
            return startOfEmail + endOfEmail;
        } else if (startOfEmail.length() == 2) {
            return firstChar + "*" + endOfEmail;
        } else {
            char lastChar = startOfEmail.charAt(startOfEmail.length() - 1);
            String middle = "*".repeat(startOfEmail.length() - 2);
            return firstChar + middle + lastChar + endOfEmail;
        }
    }
}
