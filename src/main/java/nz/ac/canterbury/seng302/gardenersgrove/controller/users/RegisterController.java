package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import java.time.LocalDate;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for registering new users
 */

@Controller
public class RegisterController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;

    /**
     * Constructs a new RegisterController
     *
     * @param userService        The GardenUserService to use
     * @param tokenService       The TokenService to use
     * @param emailSenderService The EmailSenderService to use
     */
    public RegisterController(GardenUserService userService, TokenService tokenService,
            EmailSenderService emailSenderService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailSenderService = emailSenderService;
    }

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
     * @return view name for the user registration template or a redirect URL
     */
    @PostMapping("/users/register")
    public String submitRegister(
            @Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
            BindingResult bindingResult,
            @RequestParam(value = "dateError", required = false) String dateValidity) {
        logger.info("POST /users/register");

        if (Objects.equals(dateValidity, "dateInvalid")) {
            bindingResult.rejectValue("DOB", "DOB.formatError", "Date must be in the format DD-MM-YYYY");
        }

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

        String token = tokenService.createEmailToken();
        tokenService.addEmailTokenAndTimeToUser(user, token);
        userService.addUser(user);

        sendRegisterEmail(user, token);

        return "redirect:/users/user/" + user.getId() + "/authenticate-email";
    }

    /**
     * Creates a new user for testing purposes
     */
    @PostConstruct
    public void createDummy() {
        try {
            GardenUser user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password",
                    LocalDate.of(1970, 1, 1));
            userService.addUser(user);
            GardenUser user1 = new GardenUser("Immy", null, "immy@gmail.com", "password",
                    LocalDate.of(1970, 1, 1));
            userService.addUser(user1);
            GardenUser user2 = new GardenUser("Liam", "Doe", "liam@gmail.com", "password",
                    LocalDate.of(1970, 1, 1));
            userService.addUser(user2);
            GardenUser user3 = new GardenUser("Liam", "Doe", "liam2@gmail.com", "password",
                    LocalDate.of(1970, 1, 1));
            userService.addUser(user3);

            logger.info("Created dummy users for testing purposes");
        } catch (Exception e) {
            logger.error("Error while creating dummy user", e);
        }
    }

    /**
     * Deals with sending register email to user
     * @param user
     * @param token
     */
    public void sendRegisterEmail(GardenUser user, String token) {
        emailSenderService.sendEmail(user, "Welcome to Gardener's Grove",
                "Your account has been created!\n\n"
                        + "Your token is: " + token + "\n\n"
                        + "If this was not you, you can ignore this message and the account will be deleted after 10 minutes.");

    }
}
