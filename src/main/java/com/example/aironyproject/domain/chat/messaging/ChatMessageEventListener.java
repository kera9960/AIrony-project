package com.example.aironyproject.domain.chat.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
// 이벤트를 받아서 Publisher에게 발행 맡기기
public class ChatMessageEventListener {

  // Redis Pub/Sub 발행 담당 객체
  private final ChatMessagePublisher chatMessagePublisher;

  // 발행된 이벤트를 받는 역할
  // 트랜잭션 상태에 맞춰 실행 시점 변경
  @TransactionalEventListener(
      // 트랜잭션 커밋 성공 후에 실행
      phase = TransactionPhase.AFTER_COMMIT
  )
  // chatEventExecutor 쓰레드풀을 사용하라는 의미
  @Async("chatEventExecutor")
  public void handle(ChatMessageEvent event) {
    try {
      chatMessagePublisher.publish(event);
    } catch (Exception e) {
      log.error("채팅 메세지 Redis 발행 실패. chatRoomId={}", event.chatRoomId(), e);
    }
  }
}
