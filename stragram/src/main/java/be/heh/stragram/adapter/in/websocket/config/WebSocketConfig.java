package be.heh.stragram.adapter.in.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Active un simple broker en mémoire pour envoyer des messages aux clients
        config.enableSimpleBroker("/topic", "/queue");
        
        // Préfixe pour les messages envoyés par les clients vers le serveur
        config.setApplicationDestinationPrefixes("/app");
        
        // Préfixe pour les messages personnels
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Enregistre le endpoint WebSocket avec SockJS comme fallback
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8080", "http://localhost:3000", "http://localhost:5173")
                .setAllowedOriginPatterns("http://localhost:*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
