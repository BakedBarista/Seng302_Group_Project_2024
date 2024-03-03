package nz.ac.canterbury.seng302.gardenersgrove.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

/**
 * Custom Authentication Provider class, to allow for handling authentication in
 * any way we see fit.
 * In this case using our existing {@link User}
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    /**
     * Autowired user service for custom authentication using our own user objects
     */
    @Autowired
    private GardenUserService userService;

    public CustomAuthenticationProvider() {
        super();
    }

    /**
     * Custom authentication implementation
     * 
     * @param authentication An implementation object that must have non-empty email
     *                       (name) and password (credentials)
     * @return A new {@link UsernamePasswordAuthenticationToken} if email and
     *         password are valid with users authorities
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());

        logger.info("Authenticating user: " + email);

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new BadCredentialsException("Bad Credentials");
        }

        GardenUser u = userService.getUserByEmailAndPassword(email, password);
        if (u == null) {
            logger.warn("Invalid username or password");
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(u.getEmail(), null, u.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
