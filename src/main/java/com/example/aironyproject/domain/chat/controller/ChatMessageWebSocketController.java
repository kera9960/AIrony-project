package com.example.aironyproject.domain.chat.controller;

import com.example.aironyproject.domain.chat.dto.request.SendChatMessageRequest;
import com.example.aironyproject.domain.chat.dto.response.GetChatMessageResponse;
import com.example.aironyproject.domain.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageWebSocketController {

  private final ChatRoomService chatRoomService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/chat-rooms/{chatRoomId}/messages")
  public void sendMessage(
      @DestinationVariable Long chatRoomId,
      @Valid @Payload SendChatMessageRequest request,
      Principal principal
  ) {
    Long userId = Long.valueOf(principal.getName());

    GetChatMessageResponse response = chatRoomService.sendMessage(chatRoomId, userId, request);

    messagingTemplate.convertAndSend(
        "/sub/chat-rooms/" + chatRoomId,
        response
    );
  }
}
