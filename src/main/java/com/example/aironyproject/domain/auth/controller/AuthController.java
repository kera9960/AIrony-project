package com.example.aironyproject.domain.auth.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.auth.dto.request.LoginRequest;
import com.example.aironyproject.domain.auth.dto.response.LoginResponse;
import com.example.aironyproject.domain.auth.service.AuthService;
import com.example.aironyproject.domain.auth.dto.request.SignupRequest;
import com.example.aironyproject.domain.auth.dto.response.SignupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<CommonApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonApiResponse.success(HttpStatus.CREATED, "회원가입이 완료되었습니다.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonApiResponse.success(HttpStatus.OK, "로그인 성공", response));
    }
}
