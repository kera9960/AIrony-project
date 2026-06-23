package com.example.aironyproject.domain.accommodations.dto;

import jakarta.validation.constraints.NotNull;

public record GetAccommodationResponse(
		String name,
		int price
) {
}
