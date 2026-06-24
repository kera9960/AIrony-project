package com.example.aironyproject.domain.chat.entity;

import com.example.aironyproject.domain.chat.enums.MessageType;
import com.example.aironyproject.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoom chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Column(nullable = false, length = 1000)
  private String content;

  /**
   * 일반 메시지와 시스템 메시지를 구분
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "message_type", nullable = false, length = 20)
  private MessageType messageType;

  @Column(name = "read_at")
  private LocalDateTime readAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  /**
   * 일반 텍스트 메시지를 생성
   */
  public ChatMessage(
      ChatRoom chatRoom,
      User sender,
      String content
  ) {
    this.chatRoom = chatRoom;
    this.sender = sender;
    this.content = content;
    this.messageType = MessageType.TEXT;
    this.createdAt = LocalDateTime.now();
  }
}
