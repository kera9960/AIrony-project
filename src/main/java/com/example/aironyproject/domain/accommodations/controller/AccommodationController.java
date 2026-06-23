package com.example.aironyproject.domain.accommodations.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aironyproject.common.response.CommonApiResponse;
import com.example.aironyproject.domain.accommodations.dto.GetAccommodationResponse;
import com.example.aironyproject.domain.accommodations.dto.GetOneAccommodationResponse;
import com.example.aironyproject.domain.accommodations.service.AccommodationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccommodationController {

	private final AccommodationService accommodationService;

	@GetMapping("/accommodations")
	public ResponseEntity<CommonApiResponse<List<GetAccommodationResponse>>> getAccommodations(){
		List<GetAccommodationResponse> data = accommodationService.getAccommodations();

		return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success(HttpStatus.OK, "숙소 목록 조회 성공", data));
	}

	@GetMapping("/accommodations/{accommodationId}")
	public ResponseEntity<CommonApiResponse<GetOneAccommodationResponse>> getOneAccommodation(@PathVariable Long accommodationId){
		GetOneAccommodationResponse data = accommodationService.getOneAccommodation(accommodationId);

		return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success(HttpStatus.OK, "숙소 상세 조회 성공",  data));
	}

}
