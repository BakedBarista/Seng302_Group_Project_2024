package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class URLService {
    /**
     * The origin (scheme, host and port) of the testing and production server.
     * This is optionally specified in the application.properties file, and is
     * null otherwise.
     */
    public final String serverOrigin;

    public URLService(@Value("${gardenersgrove.server.origin:}") String serverOrigin) {
        if (serverOrigin != null && !serverOrigin.isEmpty()) {
            this.serverOrigin = serverOrigin;
        } else {
            this.serverOrigin = null;
        }
    }

    /**
     * Generates a URL string based upon the requesting URL
     * @param request the HTTP request
     * @return the URL string
     */
    public String generateUrlString(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();

        if (serverOrigin != null) {
            // First try to use the origin specified in the config file
            url.append(serverOrigin);
        } else {
            url.append(request.getScheme()).append("://").append(request.getServerName());

            if (request.getServerPort() != 80 && request.getServerPort() != 443) {
                url.append(":").append(request.getServerPort());
            }
        }

        url.append(request.getContextPath()); // This is the /test or /prod

        return url.toString();
    }

    /**
     * Generates a URL string with the reset password callback token
     * @param request the HTTP request
     * @param token the callback token for the reset password link
     * @return a URL containing all the reset password information.
     */
    public String generateResetPasswordUrlString(HttpServletRequest request, String token) {
        return generateUrlString(request) + "/users/reset-password/callback?token=" + token;
    }

    /**
     * Generates a URL string to authenticate the user's email
     * @param request the HTTP request
     * @param obfuscatedEmail the obfuscated email to include in the URL
     * @return a URL to authenticate the user's email
     */
    public String generateAuthenticateEmailUrlString(HttpServletRequest request, String obfuscatedEmail) {
        return generateUrlString(request) + "/users/user/" + obfuscatedEmail + "/authenticate-email";
    }
}
