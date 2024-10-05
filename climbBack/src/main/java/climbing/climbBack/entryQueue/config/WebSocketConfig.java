package climbing.climbBack.entryQueue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Server -> Client 로 message 전송 prefix
        config.enableSimpleBroker("/queue");
        // Client -> Server 로 message 전송 prefix
        config.setApplicationDestinationPrefixes("/app");  // for endPoint
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결을 위한 EndPoint 설정
        registry.addEndpoint("/socket-entry")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
