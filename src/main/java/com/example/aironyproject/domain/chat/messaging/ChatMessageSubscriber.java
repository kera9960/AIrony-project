package com.example.aironyproject.domain.chat.messaging;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageSubscriber implements MessageListener {

  private final JsonMapper jsonMapper;
  // 메세지 브로커에게 전송 요청을 보내는 도구
  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void onMessage(Message message, byte[] pattern) {

    try {
      // Redis에서 전달받은 JSON 문자열 추출
      // 한글 깨짐을 방지
      String json = new String(
          message.getBody(),
          StandardCharsets.UTF_8
      );

      // JSON 문자열을 ChatMessageEvent 객체로 역직렬화
      ChatMessageEvent event = jsonMapper.readValue(json, ChatMessageEvent.class);

      // 목적지와 보낼 데이터를 넣어서 Spring 내부 메세지 브로커에게 전송을 요청
      messagingTemplate.convertAndSend(
          "/sub/chat-rooms/" + event.chatRoomId(),
          event.message()
      );
    } catch (Exception e) {
      // 역직렬화 또는 WebSocket 전송 실패 로그
      log.error("Redis 채팅 메세지 처리 실패", e);
    }
  }
}
