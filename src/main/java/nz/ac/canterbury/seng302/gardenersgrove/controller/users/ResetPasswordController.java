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

    @GetMapping("/users/reset-password")
    public String resetPassword() {
        logger.info("GET /users/reset-password");
        return "users/resetPassword";
    }

    @PostMapping("/users/reset-password")
    public String resetPasswordConfirmation(
            @RequestParam(name = "email") String email, HttpServletRequest request) {
        logger.info("POST /users/reset-password");
        String token = UUID.randomUUID().toString();
        logger.info("Reset password token: " + token);

        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }
        String resetPasswordLink = baseUrl + "/users/reset-password/callback?token=" + token;
        logger.info("Reset password link: " + resetPasswordLink);
        // TODO: send email with reset password link

        return "users/resetPasswordConfirmation";
    }

    @GetMapping("/users/reset-password/callback")
    public String resetPasswordCallback(
            @RequestParam(name = "token") String token, Model model) {
        logger.info("GET /users/reset-password/callback");
        model.addAttribute("token", token);
        return "users/resetPasswordCallback";
    }

    @PostMapping("/users/reset-password/callback")
    public String resetPasswordCallbackPost(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "retypePassword") String retypePassword) {
        logger.info("POST /users/reset-password/callback");
        // TODO: verify token and reset password

        return "redirect:/users/login";
    }
}
