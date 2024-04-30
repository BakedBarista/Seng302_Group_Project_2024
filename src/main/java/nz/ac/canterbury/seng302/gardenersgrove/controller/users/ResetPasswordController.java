package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;

@Controller
public class ResetPasswordController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private TokenService tokenService;

    /**
     * Handles the reset password get request
     * @return the reset password page
     */
    @GetMapping("/users/reset-password")
    public String resetPassword() {
        logger.info("GET /users/reset-password");
        return "users/resetPassword";
    }

    /**
     * Handles the reset password post request
     * @param email the email
     * @param request the HTTP request
     * @return the reset password confirmation page
     */
    @PostMapping("/users/reset-password")
    public String resetPasswordConfirmation(
            @RequestParam(name = "email") String email, HttpServletRequest request) {


        String token = tokenService.createAuthenticationToken();
        String resetPasswordLink = generateUrlString(request, token);
        logger.info("Reset password link: " + resetPasswordLink);
        // TODO: send email with reset password link

        return "users/resetPasswordConfirmation";
    }

    /**
     * Handles the reset password callback get request
     * @param token the token
     * @param model the model
     * @return the reset password callback page
     */
    @GetMapping("/users/reset-password/callback")
    public String resetPasswordCallback(
            @RequestParam(name = "token") String token, Model model) {
        logger.info("GET /users/reset-password/callback");
        model.addAttribute("token", token);
        return "users/resetPasswordCallback";
    }

    /**
     *  Handles the reset password callback post request
     * @param token the token
     * @param newPassword the new password
     * @param retypePassword the retyped password
     * @return the redirect to the login page
     */
    @PostMapping("/users/reset-password/callback")
    public String resetPasswordCallbackPost(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "retypePassword") String retypePassword) {
        logger.info("POST /users/reset-password/callback");
        // TODO: verify token and reset password

        return "redirect:/users/login";
    }

    /**
     * Generates a URL string for the reset password link
     * @param request the HTTP request
     * @return the URL string
     */
    public String generateUrlString(HttpServletRequest request, String token) {

        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }
        baseUrl += "/users/reset-password/callback?token=" + token;
        return baseUrl;

    }
}
