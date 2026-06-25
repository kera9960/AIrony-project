package com.example.aironyproject.domain.accommodationLike.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.accommodationLike.dto.CreateAccommodationLikeResponse;
import com.example.aironyproject.domain.accommodationLike.service.AccommodationLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodationLikes")
public class AccommodationLikeController {

    private final AccommodationLikeService accommodationLikeService;

    @PostMapping("/{accommodationId}")
    public ResponseEntity<CommonApiResponse<CreateAccommodationLikeResponse>>  createAccommodationLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long accommodationId) {

        CreateAccommodationLikeResponse response = accommodationLikeService.createLike(userId, accommodationId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonApiResponse.success(HttpStatus.CREATED,"찜 생성 성공", response));
    }
}
