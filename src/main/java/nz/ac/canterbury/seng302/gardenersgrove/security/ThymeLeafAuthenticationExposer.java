package nz.ac.canterbury.seng302.gardenersgrove.security;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ThymeLeafAuthenticationExposer {

    @ModelAttribute
    public void addAttributes(HttpServletRequest request, Model model) {
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

}
