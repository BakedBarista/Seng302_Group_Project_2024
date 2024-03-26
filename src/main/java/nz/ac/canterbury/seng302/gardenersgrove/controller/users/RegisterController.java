package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationSequence;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for registering new users
 */

@Controller
public class RegisterController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private GardenUserService userService;

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
            @Validated(ValidationSequence.class) @ModelAttribute("registerDTO") RegisterDTO registerDTO,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {
        logger.info("POST /users/register");

        UserValidation userValidation = new UserValidation();
        boolean valid = true;


        if (userService.getUserByEmail(registerDTO.getEmail()) != null) {
            model.addAttribute("emailInuse", "This email address is already in use");
            valid = false;
        }

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            model.addAttribute("matchPassword", "Passwords do not match");
            valid = false;
        }

        // TODO: validate using annotations in DTO
        // if (!userValidation.userInvalidDateValidation(user.getDOB())) {
        //     model.addAttribute("invalidDob", "Date is not in valid format, (DD/MM/YYYY)");
        //     valid = false;
        // } else if (!userValidation.userYoungDateValidation(user.getDOB())) {
        //     model.addAttribute("youngDob", "You must be 13 years or older to create an account");
        //     valid = false;
        // } else if (!userValidation.userOldDateValidation(user.getDOB())) {
        //     model.addAttribute("oldDob", "The maximum age allowed is 120 years");
        //     valid = false;
        // }


        if (valid && !bindingResult.hasErrors()) {
            userService.addUser(new GardenUser(registerDTO.getFname(), registerDTO.getLname(), registerDTO.getEmail(),
                    registerDTO.getPassword(), registerDTO.getDOB()));

            try {
                request.logout();
            } catch (ServletException e) {
                logger.warn("User was not logged in");
            }

            try {
                request.login(registerDTO.getEmail(), registerDTO.getPassword());
                return "redirect:/users/user";
            } catch (ServletException e) {
                logger.error("Error while login ", e);
            }
        } else {
            if (bindingResult.hasErrors()) {
                model.addAttribute("registerDTO", registerDTO);
            }

            return "users/registerTemplate";
        }

        return "users/registerTemplate";


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
