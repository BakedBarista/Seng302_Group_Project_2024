package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for registering new users
 */
@Controller
public class RegisterController {
    Logger logger = LoggerFactory.getLogger(RegisterController.class);

    /**
     * Shows the user the form
     * @return redirect to /demo
     */
    @GetMapping("/users/register")
    public String register() {
        logger.info("GET /users/register");
        return "users/registerTemplate";
    }

    /**
     * Submits the form
     */
    @PostMapping("/users/register")
    public String submitRegister(
        @RequestParam(name="fname") String fname,
        @RequestParam(name="lname") String lname,
        @RequestParam(name="noLname", defaultValue = "false") boolean noLname,
        @RequestParam(name="email") String email,
        @RequestParam(name="address") String address,
        @RequestParam(name="password") String password,
        @RequestParam(name="confirmPassword") String confirmPassword,
        @RequestParam(name="dob") String dob,
        Model model
    ) {
        logger.info("POST /users/register");

        // TODO: validation here
        boolean valid = password.equals(confirmPassword);
        if (!valid) {
            model.addAttribute("fname", fname);
            model.addAttribute("lname", lname);
            model.addAttribute("noLname", noLname);
            model.addAttribute("email", email);
            model.addAttribute("address", address);
            model.addAttribute("dob", dob);
            return "users/registerTemplate";
        }

        // TODO: save to database

        return "redirect:/users/login";
    }

}
