package com.example.aironyproject.domain.accommodations.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.entity.AccommodationStatus;



public record CheckAccommodateResponse (
		int price,
		String name,
		AccommodationStatus status
){
	public CheckAccommodateResponse(Accommodation accommodation){
		this(accommodation.getPrice(),
			accommodation.getName(),
			accommodation.getStatus());
	}
}
