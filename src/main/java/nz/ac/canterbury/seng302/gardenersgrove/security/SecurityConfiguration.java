package nz.ac.canterbury.seng302.gardenersgrove.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Custom Security Configuration
 * Such functionality was previously handled by WebSecurityConfigurerAdapter
 */
@Configuration
@EnableWebSecurity
@ComponentScan("nz.ac.canterbury.seng302.gardenersgrove.security")
public class SecurityConfiguration {

    /**
     * Our Custom Authentication Provider {@link CustomAuthenticationProvider}
     */
    @Autowired
    private CustomAuthenticationProvider authProvider;

    /**
     * Create an Authentication Manager with our
     * {@link CustomAuthenticationProvider}
     * 
     * @param http http security configuration object from Spring
     * @return a new authentication manager
     * @throws Exception if the AuthenticationManager can not be built
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();

    }

    /**
     * Creates an AuthenticationManager bean with the custom authentication provider.
     *
     * @param http http security configuration object from Spring (beaned in)
     * @return Custom SecurityFilterChain
     * @throws Exception if the SecurityFilterChain can not be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Allow h2 console through security. Note: Spring 6 broke the nicer way to do
        // this (i.e. how the authorisation is handled below)
        // See https://github.com/spring-projects/spring-security/issues/12546
        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")).permitAll());
        http.headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable());
        http.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")));

        http.authorizeHttpRequests(auth -> {
            // Allow "/", "/register", and "/login" to anyone (permitAll)
            auth.requestMatchers("/", "/users/dummy",
                                "/users/register", "/users/login",
                                "/css/**", "/js/**")
                    .permitAll();
            // Any other request requires authentication
            auth.anyRequest()
                    .authenticated();
        });

        // Define logging out, a POST "/logout" endpoint now exists under the hood,
        // redirect to "/login", invalidate session and remove cookie
        http.logout(
                logout -> logout
                        // Used a RequestMatcher to accept GET requests
                        .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout"))
                        .logoutSuccessUrl("/users/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));
        return http.build();

    }

}
