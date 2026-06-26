package com.example.aironyproject.domain.accommodationLike.dto;

public record PopularAccommodationResponse(
        Long accommodationId,
        String accommodationName,
        Long likeCount
) {
}
