package com.example.aironyproject.domain.accommodations.repository;

import java.util.List;

import com.example.aironyproject.domain.accommodations.dto.CheckAccommodateResponse;
import com.example.aironyproject.domain.accommodations.entity.AccommodationStatus;

public interface AccommodationCustomRepository {

	List<CheckAccommodateResponse> findByOption(Integer price, String name, AccommodationStatus status);
}
