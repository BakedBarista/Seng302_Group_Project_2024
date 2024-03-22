package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Shows the reset password page
     * 
     * This is linked to from the login page
     *
     * @return reset password page view
     */
    @GetMapping("/users/reset-password")
    public String resetPassword(Model model) {
        logger.info("GET /users/reset-password");
        return "users/resetPassword";
    }

    /**
     * Sends a reset password email to the given email address
     *
     * @return reset password confirmation page view
     */
    @PostMapping("/users/reset-password")
    public String resetPasswordConfirmation(
            @RequestParam(name = "email") String email,
            Model model) {
        logger.info("POST /users/reset-password");

        // TODO: send email with reset password link

        return "users/resetPasswordConfirmation";
    }

    /**
     * Shows the reset password callback page
     * 
     * This is the link that is included in the reset password email
     *
     * @return reset password callback page view
     */
    @GetMapping("/users/reset-password/callback")
    public String resetPasswordCallback(
            @RequestParam(name = "token") String token,
            Model model) {
        logger.info("GET /users/reset-password/callback");
        model.addAttribute("token", token);
        return "users/resetPasswordCallback";
    }

    /**
     * Submits the reset password callback form
     * 
     * @return redirects to the login page
     */
    @PostMapping("/users/reset-password/callback")
    public String resetPasswordCallbackPost(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "retypePassword") String retypePassword,
            Model model) {
        logger.info("GET /users/reset-password/callback");

        // TODO: verify token and reset password

        return "redirect:/users/login";
    }
}
