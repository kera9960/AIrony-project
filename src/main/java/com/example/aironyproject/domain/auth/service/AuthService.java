package com.example.aironyproject.domain.auth.service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.common.security.JwtUtil;
import com.example.aironyproject.domain.auth.dto.request.LoginRequest;
import com.example.aironyproject.domain.auth.dto.request.SignupRequest;
import com.example.aironyproject.domain.auth.dto.response.LoginResponse;
import com.example.aironyproject.domain.auth.dto.response.SignupResponse;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(
                request.email(),
                encodedPassword,
                request.name(),
                request.phoneNumber()
        );
        // 저장
        User saveUser = userRepository.save(user);
        return SignupResponse.from(saveUser);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 유저 조회
        User user = userRepository.findByEmail(request.email()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 토큰 발급
        String token = jwtUtil.createToken(user.getId(), user.getEmail(), user.getRole().name());

        return new LoginResponse(token);
    }
}
