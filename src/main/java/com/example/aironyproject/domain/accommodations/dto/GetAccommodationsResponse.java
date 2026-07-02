package com.example.aironyproject.domain.accommodations.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;



public record GetAccommodationsResponse(
		Long id,
		String name,
		int price
) {
	public static GetAccommodationsResponse from(Accommodation accommodation) {
		return new GetAccommodationsResponse(
			accommodation.getId(),
			accommodation.getName(),
			accommodation.getPrice()
		);
	}
}
