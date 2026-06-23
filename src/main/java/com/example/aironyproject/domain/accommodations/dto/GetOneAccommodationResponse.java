package com.example.aironyproject.domain.accommodations.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.entity.AccommodationStatus;

public record GetOneAccommodationResponse(
	String name,
	String address,
	String description,
	String price,
	AccommodationStatus status
) {
	public static GetOneAccommodationResponse from(Accommodation accommodation){
		return new GetOneAccommodationResponse(
			accommodation.getName(),
			accommodation.getAddress(),
			accommodation.getDescription(),
			accommodation.getPrice() + " / 박",
			accommodation.getStatus()
		);
	}
}
