package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class URLService {
    /**
     * Generates a URL string based upon the requesting URL
     * @param request the HTTP request
     * @return the URL string
     */
    public String generateUrlString(HttpServletRequest request) {
        // Get the URL they requested from (not the localhost)
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme()).append("://").append(request.getServerName());

        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            url.append(":").append(request.getServerPort());
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
}
