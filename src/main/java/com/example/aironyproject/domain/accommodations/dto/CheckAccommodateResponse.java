package com.example.aironyproject.domain.accommodations.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.enums.AccommodationStatus;



public record CheckAccommodateResponse (
		int price,
		String name,
		AccommodationStatus status
){
	public static CheckAccommodateResponse from(Accommodation accommodation){
		return new CheckAccommodateResponse(
			accommodation.getPrice(),
			accommodation.getName(),
			accommodation.getStatus());
	}
}
