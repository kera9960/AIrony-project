package com.example.aironyproject.domain.accommodationLike.repository;

import com.example.aironyproject.domain.accommodationLike.dto.PopularAccommodationResponse;

import java.util.List;

public interface AccommodationLikeCustomRepository {

    List<PopularAccommodationResponse> findPopularAccommodation();
}
