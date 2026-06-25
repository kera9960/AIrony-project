package com.example.aironyproject.domain.accommodationLike.repository;

import com.example.aironyproject.domain.accommodationLike.dto.PopularAccommodationDto;

import java.util.List;

public interface AccommodationLikeCustomRepository {

    List<PopularAccommodationDto> findPopularAccommodation();
}
