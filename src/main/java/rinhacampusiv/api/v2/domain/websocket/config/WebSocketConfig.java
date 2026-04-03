package rinhacampusiv.api.v2.domain.websocket.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        //local onde o REACT tem que estar conectado para receber mensagens
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*") //http://localhost:3000
                //ativa o suporte para navegadores antigos
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        //local onde o REACT tem que estar inscrito para receber mensagens
        config.enableSimpleBroker("/topic");

        //local onde o REACT pode mandar mensagens para o back
        config.setApplicationDestinationPrefixes("/app");

    }
}
