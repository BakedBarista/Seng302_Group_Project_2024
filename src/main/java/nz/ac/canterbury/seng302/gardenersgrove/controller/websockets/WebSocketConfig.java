package nz.ac.canterbury.seng302.gardenersgrove.controller.websockets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configures the WebSocket handlers for the application.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private ObjectMapper objectMapper;
    private String serverOrigin;

    public WebSocketConfig(ObjectMapper objectMapper, @Value("${gardenersgrove.server.origin:*}") String serverOrigin) {
        this.objectMapper = objectMapper;
        this.serverOrigin = serverOrigin;
    }

    /**
     * Registers all of the WebSocket handlers for the application.
     * @param registry the registry to add the handlers to
     */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(testWebSocketHandler(), "/api/messages")
                .setAllowedOrigins(serverOrigin)
                .addInterceptors(new HttpSessionHandshakeInterceptor());
	}

    /**
     * Constructs a new TestWebSocketHandler.
     * @return a new TestWebSocketHandler
     */
    @Bean
    public WebSocketHandler testWebSocketHandler() {
        return new TestWebSocketHandler(objectMapper);
    }
}
