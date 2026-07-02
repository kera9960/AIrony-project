package com.example.aironyproject.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(

        @NotBlank(message = "이메일은 필수 입력입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
        message = "비밀번호는 8~15자의 영문, 숫자, 특수문자를 포함해야 합니다.")
        String password,

        @NotBlank(message = "이름은 필수 입력입니다.")
        String name,

        @NotBlank(message = "전화번호는 필수 입력입니다.")
        @Pattern(regexp = "^01\\d-\\d{3,4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
        String phoneNumber
) {
}
