package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordCallbackDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordController {
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final TokenService tokenService;

    private final GardenUserService userService;

    private final EmailSenderService emailSenderService;

    @Autowired
    public ResetPasswordController(GardenUserService userService, EmailSenderService emailSenderService, TokenService tokenService) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
        this.tokenService = tokenService;
    }

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
     * @param request the HTTP request
     * @return the reset password confirmation page
     */
    @PostMapping("/users/reset-password")
    public String resetPasswordConfirmation(
            HttpServletRequest request,
            @Valid @ModelAttribute("resetPasswordDTO") ResetPasswordDTO resetPasswordDTO,
            BindingResult bindingResult,
            Model model) {
        String email = resetPasswordDTO.getEmail();

        logger.info("POST /users/reset-password");
        logger.info("Email entered: {}", email);

        if (bindingResult.hasFieldErrors("email")) {
            for (FieldError errors : bindingResult.getFieldErrors()) {
                logger.info("Validation error in email field: {}", errors.getDefaultMessage());
                model.addAttribute("incorrectEmail", errors.getDefaultMessage());
                model.addAttribute("email", email);
            }
            return "users/resetPassword";
        }

        GardenUser user = userService.getUserByEmail(email);
        boolean emailExists = user != null;
        if (emailExists) {
            String token = tokenService.createAuthenticationToken();
            String resetPasswordLink = generateUrlString(request, token);
            logger.info("Reset password link: {}", resetPasswordLink);

            tokenService.addResetPasswordTokenAndTimeToUser(user, token);
            userService.addUser(user);

            String subject = "Reset your GardenersGrove Password";
            String body = "Click here " + resetPasswordLink + " to reset your password!" +
                    "\nThis link will expire after 10 minutes, you do not need to take action if this is not you.";
            emailSenderService.sendEmail(user, subject, body);
        }

        return "users/resetPasswordConfirmation";
    }

    /**
     * Shows the reset password callback page
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

        GardenUser user = userService.getUserByResetPasswordToken(token);
        if (user == null) {
            logger.error("Invalid token");
            return "redirect:/users/login?error=resetPasswordLinkExpired";
        }

        ResetPasswordCallbackDTO resetPasswordCallbackDTO = new ResetPasswordCallbackDTO();
        resetPasswordCallbackDTO.setToken(token);
        model.addAttribute("resetPasswordCallbackDTO", resetPasswordCallbackDTO);

        return "users/resetPasswordCallback";
    }

    /**
     * Submits the reset password callback form
     *
     * @return redirects to the login page
     */
    @PostMapping("/users/reset-password/callback")
    public String resetPasswordCallbackPost(
            @Valid @ModelAttribute("resetPasswordCallbackDTO") ResetPasswordCallbackDTO resetPasswordCallbackDTO,
            BindingResult bindingResult,
            Model model) {
        logger.info("GET /users/reset-password/callback");
        logger.info("");

        String token = resetPasswordCallbackDTO.getToken();
        String newPassword = resetPasswordCallbackDTO.getNewPassword();
        String confirmPassword = resetPasswordCallbackDTO.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            bindingResult.rejectValue("confirmPassword", null, "The new passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            logger.error("Error in password change form: {}", bindingResult.getAllErrors());
            model.addAttribute("resetPasswordCallbackDTO", resetPasswordCallbackDTO);

            return "users/resetPasswordCallback";
        }

        GardenUser user = userService.getUserByResetPasswordToken(token);
        if (user == null) {
            logger.error("Invalid token");
            return "redirect:/users/login?error=resetPasswordLinkExpired";
        }

        user.setPassword(newPassword);
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiryInstant(null);
        userService.addUser(user);

        emailSenderService.sendEmail(user, "Password Changed", "Your password has been updated");

        return "redirect:/users/login";
    }

    /**
     * Generates a URL string for the reset password link
     * @param request the HTTP request
     * @return the URL string
     */
    public String generateUrlString(HttpServletRequest request, String token) {
        // Get the URL they requested from (not the localhost)
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme()).append("://").append(request.getServerName());

        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            url.append(":").append(request.getServerPort());
        }

        url.append(request.getContextPath()); // This is the /test or /prod
        url.append("/users/reset-password/callback?token=").append(token);
        return url.toString();
    }
}
