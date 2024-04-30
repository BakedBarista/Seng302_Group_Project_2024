package nz.ac.canterbury.seng302.gardenersgrove.unittests.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ResetPasswordController;

public class ResetPasswordControllerTest {

    private HttpServletRequest request;
    private String token;
    private ResetPasswordController generator;
    private GardenUserService userService;
    private EmailSenderService emailSenderService;

    @BeforeEach
    public void setUp() {
        // Create a mock HttpServletRequest
        request = mock(HttpServletRequest.class);
        // Example token
        token = "abc123xyz";
        // ResetPasswordController instance
        generator = new ResetPasswordController(userService, emailSenderService);
    }

    @Test
    public void whenGenerateUrlStringCalled_thenUrlIsGenerated() {
        // Set up the expected behaviors of the mock object
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(8080); // Non-standard port

        String url = generator.generateUrlString(request, token);

        assertEquals("https://example.com:8080/users/reset-password/callback?token=abc123xyz", url);
    }

    @Test
    public void givenPort80InUse_whenGenerateUrlStringCalled_thenUrlOmitsPort() {
        // Testing with standard HTTP port
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(80); // Standard HTTP port

        String url = generator.generateUrlString(request, token);

        assertEquals("http://example.com/users/reset-password/callback?token=abc123xyz", url);
    }

    @Test
    public void givenPort443InUse_whenGenerateUrlStringCalled_thenUrlOmitsPort() {
        // Testing with standard HTTPS port
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443); // Standard HTTPS port

        String url = generator.generateUrlString(request, token);

        assertEquals("https://example.com/users/reset-password/callback?token=abc123xyz", url);
    }
}
