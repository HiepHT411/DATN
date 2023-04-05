package com.hoanghiep.hust.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    // register a websocket endpoint that the clients will use to connect to
    // websocket server
    // STOMP stands for Simple Text Oriented Messaging Protocol. It is a messaging
    // protocol that defines the format and rules for data exchange.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS(); // SockJS is used to enable fallback options for browsers that don’t
        // support websocket
    }

    // configuring a message broker that will be used to route messages from one
    // client to another.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // the messages whose destination starts with “/app” should
        // be routed to message-handling methods
        registry.enableSimpleBroker("/topic"); // Enables a simple in-memory broker
        // the messages whose destination starts with “/topic” should be routed to the
        // message broker

        // Use this for enabling a Full featured broker like RabbitMQ

        /*
         * registry.enableStompBrokerRelay("/topic") .setRelayHost("localhost")
         * .setRelayPort(61613) .setClientLogin("guest") .setClientPasscode("guest");
         */
    }
}
