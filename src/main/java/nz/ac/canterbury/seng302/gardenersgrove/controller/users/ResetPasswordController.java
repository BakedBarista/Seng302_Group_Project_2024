package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.LoginDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

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
    public String resetPassword() {
        logger.info("GET /users/reset-password");
        return "users/resetPassword";
    }

    /**
     * Sends a reset password email to the given email address
     *
     * @param email the email address to send the reset password email to
     * @return reset password confirmation page view
     */
    @PostMapping("/users/reset-password")
    public String resetPasswordConfirmation(
            @RequestParam(name = "email") String email,
            @Valid @ModelAttribute("resetPasswordDTO") ResetPasswordDTO resetPasswordDTO,
            BindingResult bindingResult,
            Model model)
            {
        logger.info("POST /users/reset-password");
        logger.info("Email entered: {}", email);

        if (Objects.equals(email, "")) {
            return "users/resetPassword";
        }
        for (FieldError errors : bindingResult.getFieldErrors()) {
            String fieldName = errors.getField();
            String errorMessage = errors.getDefaultMessage();
            logger.info("Validation error in field '" + fieldName + "': " + errorMessage);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("resetPasswordDTO", resetPasswordDTO);
            return "users/resetPassword";
        }
        // TODO: send email with reset password link

        return "users/resetPasswordConfirmation";
    }

    /**
     * Shows the reset password callback page
     *
     * This is the link that is included in the reset password email
     *
     * @param token the token that was sent in the reset password email
     * @param model used to embed the token in the form so that we still have it when the form is submitted
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
     * @param token the token that was sent in the reset password email
     * @param newPassword the new password
     * @param retypePassword the new password, retyped
     * @return redirects to the login page
     */
    @PostMapping("/users/reset-password/callback")
    public String resetPasswordCallbackPost(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "retypePassword") String retypePassword) {
        logger.info("GET /users/reset-password/callback");

        // TODO: verify token and reset password

        return "redirect:/users/login";
    }
}
