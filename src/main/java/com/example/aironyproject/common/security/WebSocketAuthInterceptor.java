package com.example.aironyproject.common.security;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * WebSocket/STOMP 연결 요청에 대한 JWT 인증을 처리하는 인터셉터
 *
 * 클라이언트가 STOMP CONNECT 요청을 보낼 때 Authorization 헤더에서 JWT 추출
 * 토큰을 검증한 뒤 인증된 사용자 정보를 WebSocket 세션에 저장
 *
 * REST API: JwtAuthFilter가 HTTP 요청마다 인증을 처리
 * WebSocket: 연결 이후 여러 메시지가 같은 연결을 통해 오가기 때문에 CONNECT 시점에 인증을 처리
 */
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

  private final JwtUtil jwtUtil;

  /**
   * 클라이언트가 보낸 STOMP 메시지가 @MessageMapping 컨트롤러로 전달되기 전에 실행
   *
   * 모든 STOMP 메시지가 이 메서드를 지나가지만,
   * 인증은 최초 연결 단계인 CONNECT 메시지에서만 수행
   *
   * @param message 클라이언트가 보낸 STOMP 메시지
   * @param channel 메시지가 전달되는 채널
   * @return 인증에 성공하면 다음 단계로 전달할 메시지
   */
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    // Message<?>를 STOMP 전용 accessor로 감싸 command와 native header를 조회
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

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

      SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));

      // 인증 정보를 WebSocket 세션의 Principal로 저장
      accessor.setUser(authentication);
    }

    return message;
  }

}
