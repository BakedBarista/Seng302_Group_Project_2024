package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private GardenUserService userService;


    @GetMapping("/users/authenticateEmail/{id}")
    public String authenticateEmail(@PathVariable("id") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "/authentication/emailAuthentication";
    }

    @PostMapping("/users/validateAuthenticationToken/{givenToken}")
    public String validateAuthenticationToken(@PathVariable("givenToken") Long givenToken, Model model)
}
