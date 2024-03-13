package nz.ac.canterbury.seng302.gardenersgrove.security;

import java.security.Principal;

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
     * Adds attributes to the model indicating whether the user is authenticated and the current user's ID.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param model The model to which attributes will be added.
     */
    @ModelAttribute
    public void addAttributes(HttpServletRequest request, Model model) {
        Principal principal = request.getUserPrincipal();
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("currentUserId", isAuthenticated ? Integer.parseInt(principal.getName()) : null);
    }

}
