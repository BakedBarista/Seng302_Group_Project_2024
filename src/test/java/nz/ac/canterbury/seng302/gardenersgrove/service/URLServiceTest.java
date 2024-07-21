package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.ResetPasswordController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.ResetPasswordDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class URLServiceTest {
    private HttpServletRequest request;
    private String token;
    private ResetPasswordController controller;
    private GardenUserService userService;
    private TokenService tokenService;
    private EmailSenderService emailSenderService;

    private URLService urlService;
    private Model model;

    @BeforeEach
    public void setUp() {
        request = mock(HttpServletRequest.class);
        userService = mock(GardenUserService.class);
        tokenService = mock(TokenService.class);
        emailSenderService = mock(EmailSenderService.class);
        urlService = mock(URLService.class);
        controller = new ResetPasswordController(userService, emailSenderService, tokenService, urlService);

        resetPasswordDTO = mock(ResetPasswordDTO.class);
        token = "abc123xyz";
        user = new GardenUser("John", "Doe", "john.doe@gmail.com", "password", null);
    }

    @ParameterizedTest
    @CsvSource({
            "https,example.com,8080,/test,https://example.com:8080/test/users/reset-password/callback?token=abc123xyz",
            "http,example.com,80,/test,http://example.com/test/users/reset-password/callback?token=abc123xyz",
            "https,example.com,443,/prod,https://example.com/prod/users/reset-password/callback?token=abc123xyz",
    })
    void whenGenerateUrlStringCalled_thenUrlIsGenerated(String scheme, String host, int port, String contextPath, String expectedUrl) {
        // Set up the expected behaviors of the mock object
        String url = scheme + "://" + host + ":" + port + contextPath;

        when(request.getScheme()).thenReturn(scheme);
        when(request.getServerName()).thenReturn(host);
        when(request.getServerPort()).thenReturn(port);
        when(request.getContextPath()).thenReturn(contextPath);
        when(urlService.generateUrlString(any(HttpServletRequest.class))).thenReturn(url);

        String finalUrl = urlService.generateResetPasswordUrlString(request, token);

        assertEquals(expectedUrl, finalUrl);
    }
}
