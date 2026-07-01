package com.example.aironyproject.domain.chat.controller;

import com.example.aironyproject.domain.chat.dto.request.SendChatMessageRequest;
import com.example.aironyproject.domain.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageWebSocketController {

  private final ChatRoomService chatRoomService;

  @MessageMapping("/chat-rooms/{chatRoomId}/messages")
  public void sendMessage(
      @DestinationVariable Long chatRoomId,
      @Valid @Payload SendChatMessageRequest request,
      Principal principal
  ) {
    Long userId = Long.valueOf(principal.getName());

    chatRoomService.sendMessage(chatRoomId, userId, request);
  }
}
