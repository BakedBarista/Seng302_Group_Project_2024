package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.security.BlockedUserFilter;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

class BlockedUserFilterTests {
    private GardenUserService userService;
    private BlockedUserFilter blockedUserFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private Principal principal;
    private ServletUriComponentsBuilder uriComponentsBuilder;
    private UriComponents uriComponents;

    private GardenUser user;

    @BeforeEach
    void setUp() {
        userService = mock(GardenUserService.class);
        blockedUserFilter = new BlockedUserFilter(userService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        principal = mock(Principal.class);
        uriComponentsBuilder = mock(ServletUriComponentsBuilder.class);
        uriComponents = mock(UriComponents.class);

        user = new GardenUser();
        user.setFname("John");
        user.setLname("Doe");
    }

    @Test
    void givenNotLoggedIn_whenFilterCalled_thenNotRedirected() throws ServletException, IOException {
        when(request.getUserPrincipal()).thenReturn(null);

        blockedUserFilter.doFilter(request, response, filterChain);

        verify(response, times(0)).setStatus(anyInt());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenLoggedInButNotBlocked_whenFilterCalled_thenNotRedirected() throws ServletException, IOException {
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(user);

        blockedUserFilter.doFilter(request, response, filterChain);

        verify(response, times(0)).setStatus(anyInt());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenUserBlockedAndOnBlockedMessagePage_whenFilterCalled_thenNotRedirected() throws ServletException, IOException {
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(user);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/users/blocked"));

        user.setAccountDisabled(true);

        blockedUserFilter.doFilter(request, response, filterChain);

        verify(response, times(0)).setStatus(anyInt());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenUserBlockedAndNotOnBlockedMessagePage_whenFilterCalled_thenRedirected() throws ServletException, IOException {
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(user);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));

        when(uriComponentsBuilder.build()).thenReturn(uriComponents);
        when(uriComponents.getPath()).thenReturn("/test");

        user.setAccountDisabled(true);

        try (MockedStatic<ServletUriComponentsBuilder> mocked = mockStatic(ServletUriComponentsBuilder.class)) {
            mocked.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(uriComponentsBuilder);

            blockedUserFilter.doFilter(request, response, filterChain);
        }

        verify(response).setStatus(302);
        verify(response).addHeader("Location", "/test/users/blocked");
        verify(response).flushBuffer();
        verify(filterChain, times(0)).doFilter(any(), any());
    }
}
