package com.example.aironyproject.common.security;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.repository.ChatRoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * WebSocket/STOMP 연결 요청에 대한 JWT 인증을 처리하는 인터셉터
 * REST API: JwtAuthFilter가 HTTP 요청마다 인증을 처리
 * WebSocket: 연결 이후 여러 메시지가 같은 연결을 통해 오가기 때문에 CONNECT 시점에 인증을 처리
 */
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

  private final JwtUtil jwtUtil;
  private final ChatRoomRepository chatRoomRepository;

  /**
   * 클라이언트가 보낸 STOMP 메시지가 @MessageMapping 컨트롤러로 전달되기 전에 실행
   * 모든 STOMP 메시지가 이 메서드를 지나가지만, 인증은 최초 연결 단계인 CONNECT 메시지에서만 수행
   *
   * @param message 클라이언트가 보낸 STOMP 메시지
   * @param channel 메시지가 전달되는 채널
   * @return 인증에 성공하면 다음 단계로 전달할 메시지
   */
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    // 현재 메세지에서 STOMP 명령어와 헤더 정보를 꺼냄
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    // STOMP 헤더 정보를 읽을 수 없는 메세지는 인터셉터에서 검증 대상이 아니라서 그대로 전달
    // 채팅 SEND 요청은 destination이 있어야 하므로, 이런 메세지는 채팅 처리로 이어지지 않음
    if (accessor == null) {
      return message;
    }

    // STOMP 최초 연결 요청(CONNECT)일 때만 JWT 인증 처리
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      // 클라이언트가 CONNECT 프레임에 Authorization 헤더를 담아 보냈는지 확인하고 값을 조회
      String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

      if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        throw new CustomException(ErrorCode.UNAUTHORIZED);
      }

      String token = authorizationHeader.substring(7);

      // JWT 서명, 만료 시간 등을 검증
      if (!jwtUtil.validateToken(token)) {
        throw new CustomException(ErrorCode.INVALID_TOKEN);
      }

      Long userId = jwtUtil.getUserId(token);
      String role = jwtUtil.getRoleFromToken(token);

      // Spring Security 권한 형식으로 객체 생성
      SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

      // 첫 번째 인자인 userId는 Authentication의 principal로 저장
      // 이때 principal.getName()을 호출하면 principal로 저장한 userId가 문자열로 반환
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));

      // 인증 정보를 WebSocket 세션의 Principal로 저장
      accessor.setUser(authentication);
    }

    // 채팅방 구독 요청일 때 접근 권한 검증
    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      if (accessor.getUser() == null) {
        throw new CustomException(ErrorCode.UNAUTHORIZED);
      }

      // 클라이언트가 구독하려는 주소
      String destination = accessor.getDestination();

      // 구독 destination 경로 형식 검증
      if (destination == null ||
          !destination.matches("^/sub/chat-rooms/\\d+$")) {
        throw new CustomException(ErrorCode.FORBIDDEN);
      }

      // destination 마지막 숫자를 chatRoomId로 추출
      Long chatRoomId = Long.valueOf(
          destination.substring(destination.lastIndexOf('/') + 1)
      );

      ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
          .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

      // 현재 WebSocket 세션 로그인 사용자 id 추출
      Long userId = Long.valueOf(accessor.getUser().getName());

      // 채팅방을 만든 회원인지
      boolean isMember =
          chatRoom.getMember().getId().equals(userId);

      // 채팅방에 배정된 관리자인지
      boolean isAdmin =
          chatRoom.getAdmin() != null &&
              chatRoom.getAdmin().getId().equals(userId);

      if (!isMember && !isAdmin) {
        throw new CustomException(ErrorCode.FORBIDDEN);
      }
    }

    // 클라이언트가 서버로 메세지를 전송하는 요청일 때 destination 검증
    if (StompCommand.SEND.equals(accessor.getCommand())) {
      if (accessor.getUser() == null) {
        throw new CustomException(ErrorCode.UNAUTHORIZED);
      }

      // 클라이언트가 메세지를 보내려는 주소
      String destination = accessor.getDestination();

      // @MessageMapping으로 들어가는 주소만 허용
      if (destination == null ||
          !destination.matches("^/pub/chat-rooms/\\d+/messages$")) {
        throw new CustomException(ErrorCode.FORBIDDEN);
      }
    }

    return message;
  }
}
