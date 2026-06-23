package com.example.aironyproject.domain.accommodations.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;



public record GetAccommodationsResponse(
		Long id,
		String name,
		String price
) {
	public GetAccommodationsResponse(Accommodation accommodation) {
		this(
			accommodation.getId(),
			accommodation.getName(),
			accommodation.getPrice() + " / 박" // 문자열 조합
		);
	}
}
