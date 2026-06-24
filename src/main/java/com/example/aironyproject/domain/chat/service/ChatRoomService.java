package com.example.aironyproject.domain.chat.service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;
import com.example.aironyproject.domain.chat.dto.request.CreateChatRoomRequest;
import com.example.aironyproject.domain.chat.dto.response.GetChatRoomListResponse;
import com.example.aironyproject.domain.chat.dto.response.CreateChatRoomResponse;
import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.repository.ChatRoomRepository;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;
  private final AccommodationRepository accommodationRepository;

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
}
