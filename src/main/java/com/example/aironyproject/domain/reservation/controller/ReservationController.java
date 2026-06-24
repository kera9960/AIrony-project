package com.example.aironyproject.domain.reservation.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.reservation.dto.*;
import com.example.aironyproject.domain.reservation.entity.Reservation;
import com.example.aironyproject.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CommonApiResponse<CreateReservationResponse>> createReservation(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateReservationRequest request) {

        CreateReservationResponse response = reservationService.createReservation(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonApiResponse.success(HttpStatus.CREATED,"예약 생성 성공", response));

    }

    @GetMapping("/me")
    public ResponseEntity<CommonApiResponse<List<GetMyReservationResponse>>> getMyReservation(
            @AuthenticationPrincipal Long userId) {

        List<GetMyReservationResponse> responses = reservationService.getMyReservations(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonApiResponse.success(HttpStatus.OK,"내 예약 목록 조회 성공", responses));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<CommonApiResponse<GetDetailReservationResponse>> getDetailReservation(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reservationId) {

        GetDetailReservationResponse response = reservationService.getDetailReservation(userId, reservationId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonApiResponse.success(HttpStatus.OK,"예약 상세 조회 성공", response));
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<CommonApiResponse<CancelReservationResponse>> cancelReservation(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reservationId,
            @Valid @RequestBody CancelReservationRequest request) {

        CancelReservationResponse response = reservationService.cancelReservation(userId, reservationId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonApiResponse.success(HttpStatus.OK,"예약 취소 성공", response));
    }

}
