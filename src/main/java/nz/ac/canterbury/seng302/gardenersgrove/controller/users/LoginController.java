package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.LoginDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.StrikeService;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    private GardenUserService userService;
    private StrikeService strikeService;

    public LoginController(GardenUserService userService, StrikeService strikeService) {
        this.userService = userService;
        this.strikeService = strikeService;
    }

    /**
     * Shows the login page
     *
     * @param model Thymeleaf model
     * @return login page view
     */
    @GetMapping("users/login")
    public String login(
            @RequestParam(name = "error", required = false) String error,
            Model model) {
        logger.info("GET /users/login");

        if (error != null && error.equals("resetPasswordLinkExpired")) {
            model.addAttribute("generalError", "Reset password link has expired.");
        }

        model.addAttribute("loginDTO", new LoginDTO());
        return "users/login";
    }

    /**
     * Authenticates user login
     *
     * @param model Thymeleaf model
     * @param request HttpServletRequest object
     * @return The view name for the login page or a redirect URL
     */
    @PostMapping("/users/login")
    public String authenticateLogin(
                                    @Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpServletRequest request) {
        logger.info("POST /users/login");

        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        GardenUser userDetails = userService.getUserByEmailAndPassword(email, password);

        for (FieldError errors : bindingResult.getFieldErrors()) {
            String fieldName = errors.getField();
            String errorMessage = errors.getDefaultMessage();
            logger.info("Validation error in field '" + fieldName + "': " + errorMessage);
        }

        if (userDetails == null) {
            bindingResult.rejectValue("password", null, "The email address is unknown, or the password is invalid");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginDTO", loginDTO);
            return "users/login";
        }

        try {
            request.logout();
        } catch (ServletException e) {
            logger.warn("User was not logged in");
        }

        if (!(userDetails.getEmailValidationToken() == null)) {
            return "redirect:/users/user/" + userDetails.getId() + "/authenticateEmail";
        }

        try {
            request.login(email, password);
            return "redirect:/";
        } catch (ServletException e) {
            logger.error("Error while login ", e);
        }

        return "users/login";
    }

    /**
     * Shows the user saying how many days they are blocked for and logs them out
     *
     * @param request The request object
     * @param authentication The authentication object
     * @param model The Thymeleaf model
     * @return The view name for the blocked page
     * @throws ServletException If there is an error logging out, but there shouldn't be as the user must be logged in to view this page
     */
    @GetMapping("/users/blocked")
    public String blocked(HttpServletRequest request, Authentication authentication, Model model) throws ServletException {
        GardenUser user = userService.getUserById((Long) authentication.getPrincipal());
        long daysLeft = strikeService.daysUntilUnblocked(user);
        model.addAttribute("daysLeft", daysLeft);

        request.logout();

        return "users/blocked";
    }
}
