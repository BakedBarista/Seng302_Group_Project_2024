package nz.ac.canterbury.seng302.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
@ComponentScan("com.baeldung.security")
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
        http.authorizeHttpRequests(auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")).permitAll())
                .headers(headers -> headers.frameOptions().disable())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2/**")))

                .authorizeHttpRequests()
                // Allow "/", "/register", and "/login" to anyone (permitAll)
                .requestMatchers("/", "/users/register", "/users/login")
                .permitAll()
                // Only allow admins to reach the "/admin" page
                .requestMatchers("/users/admin")
                // note we do not need the "ROLE_" prefix as we are calling "hasRole()"
                .hasRole("ADMIN")
                // Any other request requires authentication
                .anyRequest()
                .authenticated()
                .and()
                // Define logging in, a POST "/login" endpoint now exists under the hood, after
                // login redirect to user page
                .formLogin().loginPage("/users/login").loginProcessingUrl("/users/login")
                .defaultSuccessUrl("/users/user")
                .and()
                // Define logging out, a POST "/logout" endpoint now exists under the hood,
                // redirect to "/login", invalidate session and remove cookie
                .logout().logoutUrl("/users/logout").logoutSuccessUrl("/users/login").invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
        return http.build();

    }

}
