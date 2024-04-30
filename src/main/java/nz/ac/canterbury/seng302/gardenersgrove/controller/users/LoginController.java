package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.LoginDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private GardenUserService userService;


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

        if (error.equals("resetPasswordLinkExpired")) {
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
}
