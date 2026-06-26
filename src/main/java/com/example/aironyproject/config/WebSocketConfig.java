package com.example.aironyproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  // STOMP 메시지를 어떤 경로로 처리할지 설정
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {

    // /sub 로 시작하는 주소를 구독 주소로 사용
    registry.enableSimpleBroker("/sub");

    // /pub 로 시작하는 메시지는 @MessageMapping 메서드로 전달
    registry.setApplicationDestinationPrefixes("/pub");
  }

  // 클라이언트가 WebSocket에 처음 연결할 endpoint 설정
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {

    // 클라이언트는 /ws 주소로 WebSocket 연결을 시작
    registry.addEndpoint("/ws")

        // WebSocket 연결 요청의 Origin 헤더 값을 검사할 때,
        // 어떤 Origin에서 온 연결 요청을 허용할지 설정
        .setAllowedOriginPatterns("*");
  }
}
