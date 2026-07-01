package com.example.aironyproject.domain.chat.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

// 채팅 메세지 이벤트를 Redis Pub/Sub 채널에 발행하는 클래스
@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

  private final StringRedisTemplate redisTemplate;
  private final JsonMapper jsonMapper;

  public void publish(ChatMessageEvent event) {

    try {
      // Redis는 문자열 형태로 메세지를 발행
      // 이벤트 객체를 JSON 문자열로 변환
      String json = jsonMapper.writeValueAsString(event);

      // 모든 서버가 구독 중인 채팅 메세지 채널로 발행
      redisTemplate.convertAndSend(
          ChatRedisChannel.MESSAGE,
          json
      );
    } catch (JacksonException e) {
      throw new IllegalStateException("채팅 메세지 직렬화 실패", e);
    }
  }
}
