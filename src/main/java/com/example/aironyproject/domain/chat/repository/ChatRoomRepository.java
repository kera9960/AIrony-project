package com.example.aironyproject.domain.chat.repository;

import com.example.aironyproject.domain.chat.dto.projection.ChatRoomSendValidationProjection;
import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
  Page<ChatRoom> findAllByOrderByCreatedAtDesc(Pageable pageable);

  /**
   * 관리자 문의 상태별 목록을 페이지 단위로 조회
   * 전달받은 상태에 해당하는 문의방을 생성일 기준 최신순으로 조회
   *
   * @param status 조회할 문의방 상태
   * @param pageable 페이지 정보
   */
  Page<ChatRoom> findAllByStatusOrderByCreatedAtDesc(ChatRoomStatus status, Pageable pageable);

  /**
   * 메시지 전송 시 참여자 여부와 채팅방 상태를 검증하기 위해 사용
   *
   * @param chatRoomId 검증할 채팅방 ID
   * @return 메시지 전송 검증용 채팅방 정보
   */
  @Query("""
    SELECT new com.example.aironyproject.domain.chat.dto.projection.ChatRoomSendValidationProjection(
      cr.id,
      cr.member.id,
      cr.admin.id,
      cr.status
    )
    FROM ChatRoom cr
    WHERE cr.id = :chatRoomId
""")
  Optional<ChatRoomSendValidationProjection> findSendValidationProjectionById(@Param("chatRoomId") Long chatRoomId);

  /**
   * 관리자 문의 수락 시 같은 채팅방이 동시에 수정되는 것을 막기 위해
   * 조회 시점에 해당 채팅방 row에 비관적 락을 건다.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM ChatRoom c WHERE c.id = :chatRoomId")
  Optional<ChatRoom> findByIdWithPessimisticLock(Long chatRoomId);
}
