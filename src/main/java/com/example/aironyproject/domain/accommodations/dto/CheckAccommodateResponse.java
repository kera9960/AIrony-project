package com.example.aironyproject.domain.accommodations.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.entity.AccommodationStatus;

import lombok.Getter;


public record CheckAccommodateResponse (
		String price,
		String name,
		AccommodationStatus status
){
	public CheckAccommodateResponse(Accommodation accommodation){
		this(accommodation.getPrice() + " / 박",
			accommodation.getName(),
			accommodation.getStatus());
	}

	public CheckAccommodateResponse(int price, String name, AccommodationStatus status) {
		this(price + " / 박", name, status);
	}
}
