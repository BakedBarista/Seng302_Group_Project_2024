package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class URLServiceTest {
    private String token;

    private URLService urlService;

    @BeforeEach
    public void setUp() {
        urlService = new URLService();
        token = "abc123xyz";
    }

    @ParameterizedTest
    @CsvSource({
            "https,example.com,8080,/test,https://example.com:8080/test",
            "http,example.com,80,/test,http://example.com/test",
            "https,example.com,443,/prod,https://example.com/prod",
    })
    void whenGenerateUrlStringCalled_thenUrlIsGenerated(String scheme, String host, int port, String contextPath, String expectedUrl) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn(scheme);
        when(request.getServerName()).thenReturn(host);
        when(request.getServerPort()).thenReturn(port);
        when(request.getContextPath()).thenReturn(contextPath);

        String result = urlService.generateUrlString(request);
        assertEquals(expectedUrl, result);
    }

    @ParameterizedTest
    @CsvSource({
            "https,example.com,8080,/test,https://example.com:8080/test/users/reset-password/callback?token=abc123xyz",
            "http,example.com,80,/test,http://example.com/test/users/reset-password/callback?token=abc123xyz",
            "https,example.com,443,/prod,https://example.com/prod/users/reset-password/callback?token=abc123xyz",
    })
    void whenGenerateResetPasswordUrlCalled_thenUrlIsGeneratedWithToken(String scheme, String host, int port, String contextPath, String expectedUrl) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn(scheme);
        when(request.getServerName()).thenReturn(host);
        when(request.getServerPort()).thenReturn(port);
        when(request.getContextPath()).thenReturn(contextPath);

        String result = urlService.generateResetPasswordUrlString(request, token);
        assertEquals(expectedUrl, result);
    }
}
