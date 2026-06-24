package com.example.aironyproject.domain.user.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.user.UserService;
import com.example.aironyproject.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<CommonApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal Long userId) {
        UserResponse response = userService.getMe(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonApiResponse.success(HttpStatus.OK, "내 정보 조회 성공", response));
    }
}
