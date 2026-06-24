package com.example.aironyproject.domain.reservation.controller;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.reservation.dto.CreateReservationRequest;
import com.example.aironyproject.domain.reservation.dto.CreateReservationResponse;
import com.example.aironyproject.domain.reservation.entity.Reservation;
import com.example.aironyproject.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
