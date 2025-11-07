package be.heh.stragram.adapter.in.websocket.config;

import be.heh.stragram.adapter.out.crypto.JwtTokenProviderAdapter;
import be.heh.stragram.application.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProviderAdapter tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            
            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);
                Optional<UserId> userIdOpt = tokenProvider.validateTokenAndGetUserId(token);
                
                if (userIdOpt.isPresent()) {
                    UserId userId = userIdOpt.get();
                    boolean isAdmin = tokenProvider.isAdmin(token);
                    
                    var authorities = isAdmin 
                        ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                    
                    var authentication = new UsernamePasswordAuthenticationToken(
                        userId.getValue().toString(),
                        null,
                        authorities
                    );
                    
                    accessor.setUser(authentication);
                    log.info("WebSocket connection authenticated for user: {}", userId);
                } else {
                    log.warn("Invalid WebSocket authentication token");
                    throw new IllegalArgumentException("Invalid authentication token");
                }
            } else {
                log.warn("WebSocket connection attempt without valid Authorization header");
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }
        }
        
        return message;
    }
}
