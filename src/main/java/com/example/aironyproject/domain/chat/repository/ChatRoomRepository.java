package com.example.aironyproject.domain.chat.repository;

import com.example.aironyproject.domain.chat.entity.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  /**
   * 특정 회원이 생성한 문의방 목록을 최신순으로 조회
   *
   * @param memberId 로그인한 회원 ID
   */
  List<ChatRoom> findAllByMember_IdOrderByCreatedAtDesc(Long memberId);
}
