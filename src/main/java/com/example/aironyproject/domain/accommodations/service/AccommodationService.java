package com.example.aironyproject.domain.accommodations.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.aironyproject.domain.accommodations.dto.GetAccommodationResponse;
import com.example.aironyproject.domain.accommodations.dto.GetOneAccommodationResponse;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationService {

	private final AccommodationRepository accommodationRepository;

	public List<GetAccommodationResponse> getAccommodations(){
		return accommodationRepository.findAll().stream()
			.map(accommodation -> new GetAccommodationResponse(
				accommodation.getName(),
				accommodation.getPrice()
			))
			.toList();
	}

	public GetOneAccommodationResponse getOneAccommodation(Long accommodationId){
		Accommodation accommodation = accommodationRepository.findById(accommodationId).orElseThrow(
			() -> new IllegalArgumentException("숙소를 찾을 수 없습니다.")
		);

		return GetOneAccommodationResponse.from(accommodation);
	}
}
