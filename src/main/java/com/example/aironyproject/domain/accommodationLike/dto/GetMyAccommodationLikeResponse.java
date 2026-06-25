package com.example.aironyproject.domain.accommodationLike.dto;

import com.example.aironyproject.domain.accommodationLike.entity.AccommodationLike;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;

public record GetMyAccommodationLikeResponse(
        Long accommodationId,
        String accommodationName,
        int price
) {

    public static GetMyAccommodationLikeResponse from(AccommodationLike accommodationLike) {
        Accommodation accommodation = accommodationLike.getAccommodation();

        return new GetMyAccommodationLikeResponse(
                accommodation.getId(),
                accommodation.getName(),
                accommodation.getPrice()
        );
    }
}
