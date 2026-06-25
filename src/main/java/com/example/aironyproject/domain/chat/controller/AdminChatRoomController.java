package com.example.aironyproject.domain.chat.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.chat.dto.response.GetChatRoomListResponse;
import com.example.aironyproject.domain.chat.enums.ChatRoomStatus;
import com.example.aironyproject.domain.chat.service.ChatRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
      @AuthenticationPrincipal Long userId,
      @RequestParam(required = false) ChatRoomStatus status
  ) {
      List<GetChatRoomListResponse> responses =
          chatRoomService.getAdminChatRooms(userId, status);

      return ResponseEntity
          .status(HttpStatus.OK)
          .body(CommonApiResponse.success(HttpStatus.OK, "관리자 문의 목록 조회 성공", responses));
  }
}
