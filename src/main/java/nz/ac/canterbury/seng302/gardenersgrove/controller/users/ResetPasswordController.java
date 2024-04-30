package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordDTO;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
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
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private TokenService tokenService;

    private GardenUserService userService;

    private EmailSenderService emailSenderService;

    public ResetPasswordController(GardenUserService userService, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
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
     * @param email the email
     * @param request the HTTP request
     * @return the reset password confirmation page
     */
    @PostMapping("/users/reset-password")
    public String resetPasswordConfirmation(
            @RequestParam(name = "email") String email, HttpServletRequest request, @Valid @ModelAttribute("resetPasswordDTO") ResetPasswordDTO resetPasswordDTO,
            BindingResult bindingResult,
            Model model) {

        logger.info("POST /users/reset-password");
        logger.info("Email entered: {}", email);

        boolean emailExists = true;
        if (emailExists) {
            GardenUser user = userService.getUserByEmail(email);
            String token = tokenService.createAuthenticationToken();
            String resetPasswordLink = generateUrlString(request, token);
            logger.info("Reset password link: " + resetPasswordLink);

        if (bindingResult.hasFieldErrors("email")) {
            for (FieldError errors : bindingResult.getFieldErrors()) {
                logger.info("Validation error in email field: {}", errors.getDefaultMessage());
                model.addAttribute("incorrectEmail", errors.getDefaultMessage());
            }
            return "users/resetPassword";
        }

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
