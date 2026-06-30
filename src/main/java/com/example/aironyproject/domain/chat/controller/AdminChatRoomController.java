package com.example.aironyproject.domain.chat.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.chat.dto.response.AcceptChatRoomResponse;
import com.example.aironyproject.domain.chat.dto.response.CompleteChatRoomResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatRoomListResponse;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import com.example.aironyproject.domain.chat.service.ChatRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/chat-rooms")
public class AdminChatRoomController {

  private final ChatRoomService chatRoomService;

  /**
   * 관리자 문의방 목록 조회
   *
   * status 값이 없으면 전체 문의방 조회
   * status 값이 있으면 해당 상태의 문의방만 조회
   */
  @GetMapping
  public ResponseEntity<CommonApiResponse<List<GetChatRoomListResponse>>> getAdminChatRooms(
      @RequestParam(required = false) ChatRoomStatus status
  ) {
      List<GetChatRoomListResponse> responses =
          chatRoomService.getAdminChatRooms(status);

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(CommonApiResponse.success(HttpStatus.OK, "관리자 문의 목록 조회 성공", responses));
  }

  @PatchMapping("/{chatRoomId}/accept")
  public ResponseEntity<CommonApiResponse<AcceptChatRoomResponse>> acceptChatRoom(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId
  ) {
    AcceptChatRoomResponse response = chatRoomService.acceptChatRoom(userId, chatRoomId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CommonApiResponse.success(HttpStatus.OK, "문의 수락 성공", response));
  }

  @PatchMapping("/{chatRoomId}/complete")
  public ResponseEntity<CommonApiResponse<CompleteChatRoomResponse>> completeChatRoom(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId
  ) {
    CompleteChatRoomResponse response = chatRoomService.completeChatRoom(userId, chatRoomId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(CommonApiResponse.success(HttpStatus.OK, "문의 완료 성공", response));
  }
}
