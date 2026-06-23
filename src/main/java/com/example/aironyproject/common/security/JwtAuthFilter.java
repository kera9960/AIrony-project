package com.example.aironyproject.common.security;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.common.response.CommonApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 헤더에서 토큰 추출
        try {
            String token = resolveToken(request);

            // 2. 토큰이 있고 유효하다면 인증 처리
            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserId(token);
                String role = jwtUtil.getRoleFromToken(token);
                // 스프링 시큐리티 규격에 맞는 권한 객체 생성
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));

                // 전역 시큐리티 컨텍스트에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 3. 다음 필터로 이동
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            handleFilterException(response, e.getErrorCode());
        }
    }

    // 필터 영역에서 발생한 예외를 GlobalExceptionHandler와 동일한 규격의 JSON 응답으로 렌더링
    private void handleFilterException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // GlobalExceptionHandler의 handleCustomException과 동일한 응답 객체 생성
        CommonApiResponse<Void> errorResponse = CommonApiResponse.error(errorCode);

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
    // Authorization: Bearer <token> 형태에서 순수 토큰만 분리
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}