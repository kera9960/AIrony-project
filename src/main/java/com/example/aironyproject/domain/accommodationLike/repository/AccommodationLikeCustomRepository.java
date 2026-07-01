package com.example.aironyproject.domain.accommodationLike.repository;

import com.example.aironyproject.domain.accommodationLike.dto.AccommodationLikeCountResponse;
import com.example.aironyproject.domain.accommodationLike.dto.PopularAccommodationResponse;

import java.util.List;

public interface AccommodationLikeCustomRepository {

    List<PopularAccommodationResponse> findPopularAccommodation();

    List<AccommodationLikeCountResponse> findAccommodationLikeCounts();
}
