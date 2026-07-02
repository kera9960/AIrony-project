package com.example.aironyproject.domain.chat.repository;

import com.example.aironyproject.domain.chat.entity.ChatMessage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  /**
   * 특정 문의방의 최신 메시지 목록 조회
   *
   * cursor가 없는 첫 조회에서 사용
   * 메시지 ID를 기준으로 내림차순 정렬하여 최신 메시지부터 조회
   *
   * @param pageable 조회할 메시지 개수 정보
   */
  List<ChatMessage> findAllByChatRoom_IdOrderByIdDesc(Long chatRoomId, Pageable pageable);

  /**
   * 특정 문의방의 cursor 이전 메시지 목록 조회
   *
   * 위로 스크롤하여 이전 메시지를 불러올 때 사용
   * cursor로 전달된 메시지 ID보다 작은 ID를 가진 메시지를 최신순으로 조회
   *
   * @param cursor 기준이 되는 메시지 ID
   * @param pageable 조회할 메시지 개수 정보
   * @return cursor 이전의 메시지 목록
   */
  List<ChatMessage> findAllByChatRoom_IdAndIdLessThanOrderByIdDesc(
      Long chatRoomId,
      Long cursor,
      Pageable pageable
  );

  long countByChatRoom_Id(Long chatRoomId);
}
