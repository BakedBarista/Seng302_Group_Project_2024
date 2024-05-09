package nz.ac.canterbury.seng302.gardenersgrove.unittests.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.security.ThymeLeafAuthenticationExposer;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

public class ThymeLeafAuthenticationExposerTests {
    private GardenUserService userService;
    private ThymeLeafAuthenticationExposer authenticationExposer;
    private HttpServletRequest request;
    private Principal principal;
    private Model model;
    private GardenUser user;

    @BeforeEach
    void setUp() {
        userService = mock(GardenUserService.class);
        authenticationExposer = new ThymeLeafAuthenticationExposer(userService);

        request = mock(HttpServletRequest.class);
        principal = mock(Principal.class);
        model = mock(Model.class);

        user = new GardenUser();
        user.setFname("John");
        user.setLname("Doe");
    }

    @Test
    void givenUserNotLoggedIn_whenAddAttributes_thenNotAuthenticated() {
        when(request.getUserPrincipal()).thenReturn(null);
    
        authenticationExposer.addAttributes(request, model);

        verify(model).addAttribute("isAuthenticated", false);
        verify(model).addAttribute("currentUserId", null);
        verify(model).addAttribute("currentUserFullName", null);
    }

    @Test
    void givenUserLoggedIn_whenAddAttributes_thenAuthenticated() {
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(user);
    
        authenticationExposer.addAttributes(request, model);

        verify(model).addAttribute("isAuthenticated", true);
        verify(model).addAttribute("currentUserId", 1L);
        verify(model).addAttribute("currentUserFullName", "John Doe");
    }
}
