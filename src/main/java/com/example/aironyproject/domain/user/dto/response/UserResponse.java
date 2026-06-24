package com.example.aironyproject.domain.user.dto.response;

import com.example.aironyproject.domain.user.entity.User;

public record UserResponse(
        Long id,
        String email,
        String name,
        String phoneNumber,
        String role
) {
   public static UserResponse from(User user) {
       return new UserResponse(
               user.getId(),
               user.getEmail(),
               user.getName(),
               user.getPhoneNumber(),
               user.getRole().name()
       );
   }
}
