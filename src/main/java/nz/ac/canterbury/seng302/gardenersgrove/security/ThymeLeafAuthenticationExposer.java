package nz.ac.canterbury.seng302.gardenersgrove.security;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class is used to expose the authentication status of the user to the Thymeleaf templates.
 */
@ControllerAdvice
public class ThymeLeafAuthenticationExposer {

    /**
     * Adds the isAuthenticated attribute to the model.
     */
    @ModelAttribute
    public void addAttributes(HttpServletRequest request, Model model) {
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

}
