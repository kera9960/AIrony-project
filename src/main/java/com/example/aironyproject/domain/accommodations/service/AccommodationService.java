package com.example.aironyproject.domain.accommodations.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodations.dto.CheckAccommodateResponse;
import com.example.aironyproject.domain.accommodations.dto.GetAccommodationsResponse;
import com.example.aironyproject.domain.accommodations.dto.GetOneAccommodationResponse;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.entity.AccommodationStatus;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationService {

	private final AccommodationRepository accommodationRepository;

	public List<GetAccommodationsResponse> getAccommodations(){
		return accommodationRepository.findAll().stream()
			.map(GetAccommodationsResponse::new)
			.toList();
	}

	public GetOneAccommodationResponse getOneAccommodation(Long accommodationId){
		Accommodation accommodation = accommodationRepository.findById(accommodationId).orElseThrow(
			() -> new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND)
		);

		return GetOneAccommodationResponse.from(accommodation);
	}

	public CheckAccommodateResponse checkingAccommodationWithQuery(int price, String name, AccommodationStatus status){

	}
}
