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

	public List<CheckAccommodateResponse> checkingAccommodationWithQuery(Integer minPrice, Integer maxPrice, String name, AccommodationStatus status){
		return accommodationRepository.findByOption(minPrice, maxPrice, name, status);
	}
}
