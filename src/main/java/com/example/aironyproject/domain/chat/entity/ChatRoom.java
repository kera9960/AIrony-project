package com.example.aironyproject.domain.chat.entity;

import com.example.aironyproject.common.entity.BaseTimeEntity;
import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private User member;

  /**
   * 문의를 담당하는 관리자
   * 문의 생성 시에는 null이며, 상담 시작 시 배정
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id")
  private User admin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "accommodation_id")
  private Accommodation accommodation;

  @Column(nullable = false, length = 100)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ChatRoomStatus status;

  public ChatRoom(User member, Accommodation accommodation, String title) {
    this.member = member;
    this.accommodation = accommodation;
    this.title = title;
    this.status = ChatRoomStatus.WAITING;
  }

  /**
   * 관리자를 배정하고 문의 상태를 상담 진행 중으로 변경
   */
  public void assignAdmin(User admin) {
    if (this.admin != null) {
      throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_ASSIGNED);
    }

    if (this.status != ChatRoomStatus.WAITING) {
      throw new CustomException(ErrorCode.INVALID_CHAT_ROOM_STATUS);
    }

    this.admin = admin;
    this.status = ChatRoomStatus.IN_PROGRESS;
  }

  /**
   * 문의 상태를 상담 완료로 변경
   */
  public void complete() {
    if (this.status != ChatRoomStatus.IN_PROGRESS) {
      throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_COMPLETED);
    }

    this.status = ChatRoomStatus.COMPLETED;
  }

  public void validateMessageSendable() {
    if (this.status != ChatRoomStatus.IN_PROGRESS) {
      throw new CustomException(ErrorCode.CHAT_MESSAGE_NOT_ALLOWED);
    }
  }
}
