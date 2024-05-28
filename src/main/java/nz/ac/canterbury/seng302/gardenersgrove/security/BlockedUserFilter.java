package nz.ac.canterbury.seng302.gardenersgrove.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

@Component
public class BlockedUserFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(BlockedUserFilter.class);

    private GardenUserService userService;

    public BlockedUserFilter(GardenUserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        GardenUser user = userService.getCurrentUser();
        if (user != null && user.isAccountDisabled()
                && !request.getRequestURL().toString().contains("/users/blocked")) {
            logger.info("Redirecting to blocked message");
            response.setStatus(302);
            response.addHeader("Location", SecurityConfiguration.getBasePath() + "/users/blocked");
            response.flushBuffer();
            return;
        }
        filterChain.doFilter(request, response);
    }
}
