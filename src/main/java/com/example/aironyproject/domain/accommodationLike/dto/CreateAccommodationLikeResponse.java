package com.example.aironyproject.domain.accommodationLike.dto;

import com.example.aironyproject.domain.accommodationLike.entity.AccommodationLike;

public record CreateAccommodationLikeResponse(
        Long likeId,
        Long userId,
        Long accommodationId
) {

    public static CreateAccommodationLikeResponse from(AccommodationLike accommodationLike) {
        return new CreateAccommodationLikeResponse(
                accommodationLike.getId(),
                accommodationLike.getUser().getId(),
                accommodationLike.getAccommodation().getId()
        );
    }
}
