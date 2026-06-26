package com.example.aironyproject.domain.accommodations.repository;

import java.util.List;

import com.example.aironyproject.domain.accommodations.dto.CheckAccommodateResponse;
import com.example.aironyproject.domain.accommodations.enums.AccommodationStatus;

public interface AccommodationCustomRepository {

	List<CheckAccommodateResponse> findByOption(String region, Integer minPrice, Integer maxPrice, String name, AccommodationStatus status);
}
