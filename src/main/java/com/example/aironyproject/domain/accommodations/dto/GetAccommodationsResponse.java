package com.example.aironyproject.domain.accommodations.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;



public record GetAccommodationsResponse(
		Long id,
		String name,
		int price
) {
	public GetAccommodationsResponse(Accommodation accommodation) {
		this(
			accommodation.getId(),
			accommodation.getName(),
			accommodation.getPrice()
		);
	}
}
