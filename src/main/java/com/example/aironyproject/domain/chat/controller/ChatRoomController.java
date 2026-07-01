package com.example.aironyproject.domain.chat.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.chat.dto.request.CreateChatRoomRequest;
import com.example.aironyproject.domain.chat.dto.response.CreateChatRoomResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatMessagesResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatRoomDetailResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatRoomListResponse;
import com.example.aironyproject.domain.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  @PostMapping
  public ResponseEntity<CommonApiResponse<CreateChatRoomResponse>> createChatRoom(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody CreateChatRoomRequest request
  ) {
    CreateChatRoomResponse response = chatRoomService.createChatRoom(userId, request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(CommonApiResponse.success(HttpStatus.CREATED, "문의방 생성 성공", response));
  }

  @GetMapping("/me")
  public ResponseEntity<CommonApiResponse<List<GetChatRoomListResponse>>> getChatRoomList(
      @AuthenticationPrincipal Long userId
  ) {
    List<GetChatRoomListResponse> response = chatRoomService.getChatRoomList(userId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CommonApiResponse.success(HttpStatus.OK, "문의방 목록 조회 성공", response));
  }

  @GetMapping("/{chatRoomId}")
  public ResponseEntity<CommonApiResponse<GetChatRoomDetailResponse>> getChatRoomDetail(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId
  ) {
    GetChatRoomDetailResponse response = chatRoomService.getChatRoomDetail(userId, chatRoomId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CommonApiResponse.success(HttpStatus.OK, "문의방 상세 조회 성공", response));
  }

  @GetMapping("/{chatRoomId}/messages")
  public ResponseEntity<CommonApiResponse<GetChatMessagesResponse>> getChatMessages(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "20") int size
  ) {
    GetChatMessagesResponse response = chatRoomService.getChatMessages(userId, chatRoomId, cursor, size);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CommonApiResponse.success(HttpStatus.OK, "메세지 목록 조회 성공", response));
  }
}
