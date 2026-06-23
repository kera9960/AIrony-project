package com.example.aironyproject.domain.accommodations.repository;

import com.example.aironyproject.domain.accommodations.dto.CheckAccommodateResponse;
import com.example.aironyproject.domain.accommodations.entity.AccommodationStatus;

public interface AccommodationCustomRepository {

	CheckAccommodateResponse findByOption(int price, String name, AccommodationStatus status);
}
