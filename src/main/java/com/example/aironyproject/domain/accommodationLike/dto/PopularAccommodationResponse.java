package com.example.aironyproject.domain.accommodationLike.dto;

import com.example.aironyproject.domain.accommodations.entity.Accommodation;

public record PopularAccommodationResponse(
        Long accommodationId,
        String accommodationName,
        Long likeCount
) {
    public static PopularAccommodationResponse from(Accommodation accommodation, Long likeCount) {
        return new PopularAccommodationResponse(
                accommodation.getId(),
                accommodation.getName(),
                likeCount
        );
    }
}
