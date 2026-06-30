package com.example.aironyproject.domain.chat.service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;
import com.example.aironyproject.domain.chat.dto.projection.ChatRoomSendValidationProjection;
import com.example.aironyproject.domain.chat.dto.request.CreateChatRoomRequest;
import com.example.aironyproject.domain.chat.dto.request.SendChatMessageRequest;
import com.example.aironyproject.domain.chat.dto.response.AcceptChatRoomResponse;
import com.example.aironyproject.domain.chat.dto.response.CompleteChatRoomResponse;
import com.example.aironyproject.domain.chat.dto.response.CreateChatRoomResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatMessageResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatMessagesResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatRoomDetailResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatRoomListResponse;
import com.example.aironyproject.domain.chat.entity.ChatMessage;
import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import com.example.aironyproject.domain.chat.repository.ChatMessageRepository;
import com.example.aironyproject.domain.chat.repository.ChatRoomRepository;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;
  private final AccommodationRepository accommodationRepository;
  private final ChatMessageRepository chatMessageRepository;

  @Transactional
  public CreateChatRoomResponse createChatRoom(Long userId, CreateChatRoomRequest request) {
    User user = userRepository.findById(userId).orElseThrow(
        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
    );

    Accommodation accommodation = null;

    if (request.accommodationId() != null) {
      accommodation = accommodationRepository.findById(request.accommodationId()).orElseThrow(
          () -> new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND)
      );
    }

    ChatRoom chatRoom = new ChatRoom(user, accommodation, request.title());

    ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

    return CreateChatRoomResponse.from(savedChatRoom);
  }

  public List<GetChatRoomListResponse> getChatRoomList(Long userId) {

    return chatRoomRepository.findAllByMember_IdOrderByCreatedAtDesc(userId)
        .stream()
        .map(GetChatRoomListResponse::from)
        .toList();
  }

  /**
   * 로그인한 회원이 본인 문의방의 상세 정보를 조회
   */
  public GetChatRoomDetailResponse getChatRoomDetail(Long userId, Long chatRoomId) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
        () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
    );

    // 로그인한 회원이 생성한 문의방인지 확인
    if (!chatRoom.getMember().getId().equals(userId)) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }

    return GetChatRoomDetailResponse.from(chatRoom);
  }

  /**
   * 관리자 문의방 목록 조회
   * 로그인한 사용자가 ADMIN 권한인지 확인한 뒤,
   * 문의방 목록을 생성일 기준 최신순으로 조회
   *
   * status 값이 없으면 전체 문의방을 조회
   * status 값이 있으면 해당 상태의 문의방만 조회
   *
   * @param status 조회할 문의방 상태, 없으면 전체 조회
   * @return 관리자 문의방 목록
   */
  public List<GetChatRoomListResponse> getAdminChatRooms(ChatRoomStatus status) {
    List<ChatRoom> chatRooms;

    if (status == null) {
      chatRooms = chatRoomRepository.findAllByOrderByCreatedAtDesc();
    } else {
      chatRooms = chatRoomRepository.findAllByStatusOrderByCreatedAtDesc(status);
    }

    return chatRooms.stream()
        .map(GetChatRoomListResponse::from)
        .toList();
  }

  /**
   * 관리자 문의 수락
   *
   * 로그인한 사용자가 ADMIN 권한인지 확인한 뒤,
   * WAITING 상태의 문의방에 현재 관리자를 배정하고
   * 문의 상태를 IN_PROGRESS로 변경
   *
   * @param userId 로그인한 사용자 ID
   * @param chatRoomId 수락할 문의방 ID
   * @return 수락된 문의방 정보
   */
  @Transactional
  public AcceptChatRoomResponse acceptChatRoom(Long userId, Long chatRoomId) {
    User admin = userRepository.findById(userId).orElseThrow(
        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
    );

    ChatRoom chatRoom = chatRoomRepository.findByIdWithPessimisticLock(chatRoomId).orElseThrow(
        () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
    );

    chatRoom.assignAdmin(admin);

    return AcceptChatRoomResponse.from(chatRoom);
  }

  @Transactional
  public CompleteChatRoomResponse completeChatRoom(Long userId, Long chatRoomId) {
    User admin = userRepository.findById(userId).orElseThrow(
        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
    );

    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
        () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
    );

    if (chatRoom.getAdmin() == null ||
        !chatRoom.getAdmin().getId().equals(admin.getId())) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }

    chatRoom.complete();

    return CompleteChatRoomResponse.from(chatRoom);
  }

  /**
   * 커서 기반 메시지 목록 조회
   *
   * 채팅방에 접근 가능한 사용자인지 확인
   * cursor 기준으로 이전 메시지를 size 개수만큼 조회
   *
   * cursor가 없으면 채팅방 첫 진입으로 보고 최신 메시지를 조회
   * cursor가 있으면 해당 메시지 ID보다 오래된 메시지를 조회
   */
  public GetChatMessagesResponse getChatMessages(
      Long userId,
      Long chatRoomId,
      Long cursor,
      int size
  ) {
    size = normalizeSize(size);

    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
        () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
    );

    // 문의를 생성한 회원 또는 배정된 관리자만 메시지를 조회
    boolean isOwner = chatRoom.getMember().getId().equals(userId);

    boolean isAssignedAdmin = chatRoom.getAdmin() != null &&
                              chatRoom.getAdmin().getId().equals(userId);

    if (!isOwner && !isAssignedAdmin) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }


    // 한 번에 조회할 메세지 개수(size)보다 1개 더 조회
    // size보다 많이 조회되면 이전 메세지가 더 존재한다는 뜻으로 hasNext 판단 가능
    Pageable pageable = PageRequest.of(0, size + 1);

    List<ChatMessage> messages;

    // cursor가 없으면 채팅방 첫 진입으로 판단하여, 최신 메세지부터 size + 1개 조회
    if (cursor == null) {
      messages = chatMessageRepository.findAllByChatRoom_IdOrderByIdDesc(
          chatRoomId,
          pageable
      );
    } else {
      // cursor로 전달된 메세지 ID보다 작은 ID(더 오래된 메세지)를 조회
      messages = chatMessageRepository.findAllByChatRoom_IdAndIdLessThanOrderByIdDesc(
          chatRoomId,
          cursor,
          pageable
      );
    }


    // 전체 메세지 개수가 아니라 이번 조회 결과 개수를 기준으로 판단
    // size + 1개를 조회했을 때 size보다 많이 조회되면 이전 메세지가 더 있다는 의미
    boolean hasNext = messages.size() > size;

    // hasNext 판단을 위해 추가로 가져온 1개는 실제 응답에서 제외
    if (hasNext) {
      messages = messages.subList(0, size);
    }

    List<GetChatMessageResponse> responses = messages.stream()
        .map(GetChatMessageResponse::from)
        .toList();


    // 다음 조회에 사용할 cursor를 계산
    // 현재 응답 메세지 중 가장 오래된 메세지 ID를 nextCursor로 초기화
    Long nextCursor = messages.isEmpty() ? null : messages.get(messages.size() - 1).getId();

    return new GetChatMessagesResponse(responses, nextCursor, hasNext);
  }

  /**
   * 채팅방에 새로운 메시지 전송
   *
   * @param chatRoomId 메시지를 전송할 채팅방 ID
   * @param senderId 메시지를 전송한 회원 ID
   * @param request 메시지 전송 요청 정보
   * @return 저장된 메시지 정보
   */
  @Transactional
  public GetChatMessageResponse sendMessage(
      Long chatRoomId,
      Long senderId,
      SendChatMessageRequest request
  ) {
    ChatRoomSendValidationProjection validation =
        chatRoomRepository.findSendValidationProjectionById(chatRoomId).orElseThrow(
            () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        );

    // Projection으로 조회한 memberId/adminId를 기준으로 메시지 전송자가 채팅방 참여자인지 검증
    validateParticipant(validation, senderId);

    // Projection으로 조회한 status를 기준으로 메시지 전송 가능한 상태인지 검증
    validateMessageSendable(validation);

    // 채팅방 참여자와 상태 검증은 Projection으로 이미 완료
    // 메시지 저장에 필요한 chatRoomId는 프록시 객체로 참조하여 불필요한 조회를 줄임
    ChatRoom chatRoom = chatRoomRepository.getReferenceById(chatRoomId);

    // User 엔티티 전체 조회가 필요하지 않아 프록시 객체를 반환하는
    // getReferenceById()를 사용하여 DB 조회를 줄임
    User user = userRepository.getReferenceById(senderId);

    ChatMessage chatMessage = new ChatMessage(
        chatRoom,
        user,
        request.content()
    );

    ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

    return GetChatMessageResponse.from(savedChatMessage);
  }

  /**
   * 메시지를 전송하려는 사용자가 해당 채팅방의 참여자인지 검증
   * 문의 생성 회원 또는 배정된 관리자만 메시지 전송 가능
   *
   * @param validation 메시지 전송 검증에 필요한 채팅방 정보
   * @param senderId 메시지를 전송하려는 사용자
   */
  private void validateParticipant(ChatRoomSendValidationProjection validation, Long senderId) {

    boolean isMember = validation.memberId().equals(senderId);

    boolean isAdmin = validation.adminId() != null &&
                      validation.adminId().equals(senderId);

    if (!isMember && !isAdmin) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }
  }

  /**
   * Projection으로 조회한 채팅방 정보를 기준으로 메시지 전송 가능 상태인지 검증
   * 진행 중(IN_PROGRESS) 상태의 문의방에서만 메시지 전송 가능
   *
   * @param validation 메시지 전송 검증에 필요한 채팅방 정보
   */
  private void validateMessageSendable(ChatRoomSendValidationProjection validation) {
    if (validation.status() != ChatRoomStatus.IN_PROGRESS) {
      throw new CustomException(ErrorCode.CHAT_MESSAGE_NOT_ALLOWED);
    }
  }

  // 채팅 메세지 조회 size 범위 제한 메서드
  // 기본값: 20, 최소값: 20, 최대값: 50
  private int normalizeSize(int size) {
    if (size < 20) {
      return 20;
    }

    return Math.min(size, 50);
  }
}
