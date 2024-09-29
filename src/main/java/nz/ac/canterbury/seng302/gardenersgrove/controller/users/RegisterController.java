package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.RegisterDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Controller for registering new users
 */

@Controller
public class RegisterController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;
    private URLService urlService;

    private BirthFlowerService birthFlowerService;

    /**
     * Constructs a new RegisterController
     *
     * @param userService        The GardenUserService to use
     * @param tokenService       The TokenService to use
     * @param emailSenderService The EmailSenderService to use
     */
    public RegisterController(GardenUserService userService, TokenService tokenService,
            EmailSenderService emailSenderService, URLService urlService, BirthFlowerService birthFlowerService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailSenderService = emailSenderService;
        this.urlService = urlService;
        this.birthFlowerService = birthFlowerService;
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
            HttpServletRequest request,
            @Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
            BindingResult bindingResult,
            @RequestParam(value = "dateError", required = false) String dateValidity) {
        logger.info("POST /users/register");

        if (Objects.equals(dateValidity, "dateInvalid")) {
            bindingResult.rejectValue(
                    "dateOfBirth",
                    "dateOfBirth.formatError",
                    "Date is not in valid format, DD/MM/YYYY, or does not represent a real date"
            );
        }

        if (userService.getUserByEmail(registerDTO.getEmail()) != null) {
            bindingResult.rejectValue("email", null, "This email address is already in use");
        }

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", null, "Passwords do not match");
        }

        if (registerDTO.isNoLname()) {
            // Checking if noLname checkbox is ticked but a lastname is supplied. 
            if (registerDTO.getLname() != null && !registerDTO.getLname().isEmpty()){
                bindingResult.rejectValue("lname", null, "Cannot have a last name while no surname box ticked.");
            }
            registerDTO.setLname(null);
        }

        if ((registerDTO.getLname() == null || registerDTO.getLname().isEmpty()) && !registerDTO.isNoLname()) {
            bindingResult.rejectValue("lname", null, "Last name cannot be empty");
        }

        if (bindingResult.hasErrors()) {
            return "users/registerTemplate";
        }

        LocalDate dob = null;
        if (registerDTO.getDateOfBirth() != null && !registerDTO.getDateOfBirth().isEmpty()) {
            try {
                dob = LocalDate.parse(registerDTO.getDateOfBirth());
            } catch (DateTimeParseException e) {
                // shouldn't happen because of validation
                logger.info("cannot parse invalid date format");
            }
        }
        GardenUser user = new GardenUser(registerDTO.getFname(), registerDTO.getLname(), registerDTO.getEmail(),
                registerDTO.getPassword(), dob);
        user.setBirthFlower(birthFlowerService.getDefaultBirthFlower(dob));

        String token = tokenService.createEmailToken();
        tokenService.addEmailTokenAndTimeToUser(user, token);
        userService.addUser(user);


        sendRegisterEmail(request, user, token);
        String obfuscatedEmail = userService.obfuscateEmail(user.getEmail());
        return "redirect:/users/user/" + obfuscatedEmail + "/authenticate-email";
    }

    /**
     * Creates a new user for testing purposes
     */
    @PostConstruct
    public void createDummy() {
        try {
            var password = "password";

            GardenUser user = new GardenUser("John", "Doe", "john.doe@gmail.com", password,
                    LocalDate.of(1970, 1, 1));
            userService.addUser(user);
            GardenUser user3 = new GardenUser("Liam", "Doe", "liam2@gmail.com", password,
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
    public void sendRegisterEmail(HttpServletRequest request, GardenUser user, String token) {
        String obfuscatedEmail = userService.obfuscateEmail(user.getEmail());
        String url = urlService.generateAuthenticateEmailUrlString(request, obfuscatedEmail);

        StringBuilder body = new StringBuilder();
        body.append("Your account has been created!\n\n");
        body.append("Your token is: ").append(token).append("\n\n");
        body.append(
                "You should have been redirected when you registered. If you were not, please visit the following link to authenticate your email: ")
                .append(url).append("\n\n");
        body.append(
                "If this was not you, you can ignore this message and the account will be deleted after 10 minutes.");

        emailSenderService.sendEmail(user, "Welcome to Gardener's Grove", body.toString());

    }
}
