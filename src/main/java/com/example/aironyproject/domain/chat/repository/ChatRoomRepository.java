package com.example.aironyproject.domain.chat.repository;

import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  /**
   * 특정 회원이 생성한 문의방 목록을 최신순으로 조회
   *
   * @param memberId 로그인한 회원 ID
   */
  List<ChatRoom> findAllByMember_IdOrderByCreatedAtDesc(Long memberId);

  /**
   * 관리자 문의 전체 목록 조회
   * 모든 문의방을 생성일 기준 최신순으로 조회
   */
  List<ChatRoom> findAllByOrderByCreatedAtDesc();

  /**
   * 관리자 문의 상태별 목록 조회
   * 전달받은 상태에 해당하는 문의방을 생성일 기준 최신순으로 조회
   *
   * @param status 조회할 문의방 상태
   * @return 상태별 문의방 목록
   */
  List<ChatRoom> findAllByStatusOrderByCreatedAtDesc(ChatRoomStatus status);
}
