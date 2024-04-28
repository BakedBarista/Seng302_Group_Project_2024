package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;

/**
 * Controller for registering new users
 */

@Controller
public class RegisterController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private GardenUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * Shows the user the registration form
     *
     * @return redirect to /demo
     */
    @GetMapping("/users/register")
    public String register(Model model) {
        logger.info("GET /users/register");
        model.addAttribute("registerDTO", new RegisterDTO());
        return "users/registerTemplate";
    }


    /**
     * Handles the submission of user registration form
     *
     * @param model Thymeleaf model
     * @param request HttpServletRequest object
     * @return  view name for the user registration template or a redirect URL
     */
    @PostMapping("/users/register")
    public String submitRegister(
            @Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {
        logger.info("POST /users/register");

        if (userService.getUserByEmail(registerDTO.getEmail()) != null) {
            bindingResult.rejectValue("email", null, "This email address is already in use");
        }

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", null, "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "users/registerTemplate";
        }

        GardenUser user = new GardenUser(registerDTO.getFname(), registerDTO.getLname(), registerDTO.getEmail(),
                registerDTO.getPassword(), registerDTO.getDOB());
        userService.addUser(user);

        try {
            request.logout();
        } catch (ServletException e) {
            logger.warn("User was not logged in");
        }

        try {
            request.login(registerDTO.getEmail(), registerDTO.getPassword());
        } catch (ServletException e) {
            logger.error("Error while login ", e);
            return "users/registerTemplate";
        }

        tokenService.addEmailTokenAndTimeToUser(user);
        userService.addUser(user);
        return "redirect:/users/user/"+user.getId()+"/authenticateEmail";
    }

    /**
     * Creates a new user for testing purposes
     */
    @PostConstruct
    public void createDummy() {
        try {
            GardenUser user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password",
                    "01/01/1970");
            userService.addUser(user);

            logger.info("Created dummy user for testing purposes");
        } catch (Exception e) {
            logger.error("Error while creating dummy user", e);
        }
    }
}
