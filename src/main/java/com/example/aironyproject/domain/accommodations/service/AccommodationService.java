package com.example.aironyproject.domain.accommodations.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodations.dto.CheckAccommodateResponse;
import com.example.aironyproject.domain.accommodations.dto.GetAccommodationsResponse;
import com.example.aironyproject.domain.accommodations.dto.GetOneAccommodationResponse;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.enums.AccommodationStatus;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationService {

	private final AccommodationRepository accommodationRepository;

	public List<GetAccommodationsResponse> getAccommodations(){
		return accommodationRepository.findAll().stream()
			.map(GetAccommodationsResponse::from)
			.toList();
	}

	public GetOneAccommodationResponse getOneAccommodation(Long accommodationId){
		Accommodation accommodation = accommodationRepository.findById(accommodationId).orElseThrow(
			() -> new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND)
		);

		return GetOneAccommodationResponse.from(accommodation);
	}
	/**
	 * 가격이 음수인지 검증, 둘중 하나라도 0 미만이면 예외처리
	 * 최소 가격이 최대 가격보다 큰지 검증
	 * 검증을 통과 했을때만 조회 실행
	 */
	public List<CheckAccommodateResponse> checkingAccommodationWithQuery(Integer minPrice, Integer maxPrice, String name, AccommodationStatus status){

		if ((minPrice != null && minPrice < 0) || (maxPrice != null && maxPrice < 0)){
			throw new CustomException(ErrorCode.NEGATIVE_PRICE_NOT_ALLOWED);
		}

		if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
			throw new CustomException(ErrorCode.INVALID_PRICE_RANGE);
		}

		return accommodationRepository.findByOption(minPrice, maxPrice, name, status);
	}
}
