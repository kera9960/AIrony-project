package com.example.aironyproject.domain.accommodationLike.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.accommodationLike.dto.CreateAccommodationLikeResponse;
import com.example.aironyproject.domain.accommodationLike.dto.GetMyAccommodationLikeResponse;
import com.example.aironyproject.domain.accommodationLike.service.AccommodationLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("/{accommodationId}")
    public ResponseEntity<CommonApiResponse<Void>> cancelAccommodationLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long accommodationId) {

        accommodationLikeService.cancelLike(userId, accommodationId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonApiResponse.success(HttpStatus.OK,"찜 취소 성공", null));
    }

    @GetMapping("/me")
    public ResponseEntity<CommonApiResponse<List<GetMyAccommodationLikeResponse>>> getMyAccommodationLike(
            @AuthenticationPrincipal Long userId) {

        List<GetMyAccommodationLikeResponse> responses = accommodationLikeService.getMyAccommodationLike(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonApiResponse.success(HttpStatus.OK,"내 찜 목록 조회 성공", responses));
    }
}
