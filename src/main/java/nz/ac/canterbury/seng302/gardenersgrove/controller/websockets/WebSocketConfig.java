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

import jakarta.validation.ValidatorFactory;
import nz.ac.canterbury.seng302.gardenersgrove.service.MessageService;

/**
 * Configures the WebSocket handlers for the application.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private MessageService messageService;
    private ObjectMapper objectMapper;
    private ValidatorFactory validatorFactory;
    private String serverOrigin;

    public WebSocketConfig(MessageService messageService, ObjectMapper objectMapper, ValidatorFactory validatorFactory,
                    @Value("${gardenersgrove.server.origin:*}") String serverOrigin) {
        this.messageService = messageService;
        this.objectMapper = objectMapper;
        this.validatorFactory = validatorFactory;
        this.serverOrigin = serverOrigin;
    }

    /**
     * Registers all of the WebSocket handlers for the application.
     * @param registry the registry to add the handlers to
     */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageWebSocketHandler(), "/api/messages")
                .setAllowedOrigins(serverOrigin)
                .addInterceptors(new HttpSessionHandshakeInterceptor());


	}

    /**
     * Constructs a new MessageWebSocketHandler.
     * @return a new MessageWebSocketHandler
     */
    @Bean
    public MessageWebSocketHandler messageWebSocketHandler() {
        return new MessageWebSocketHandler(messageService, objectMapper, validatorFactory);
    }
}
