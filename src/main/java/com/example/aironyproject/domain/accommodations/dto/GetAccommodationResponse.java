package com.example.aironyproject.domain.accommodations.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;

import jakarta.validation.constraints.NotNull;

public record GetAccommodationResponse(
		String name,
		String price
) {
	public GetAccommodationResponse(Accommodation accommodation) {
		this(
			accommodation.getName(),
			accommodation.getPrice() + " / 박" // 문자열 조합
		);
	}
}
