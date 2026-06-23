package com.example.aironyproject.domain.accommodations.repository;

import com.example.aironyproject.domain.accommodations.dto.CheckAccommodateResponse;
import com.example.aironyproject.domain.accommodations.entity.AccommodationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccommodationRepositoryImpl implements AccommodationCustomRepository{

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public CheckAccommodateResponse findByOption(int price, String name, AccommodationStatus status) {
		return null;
	}
}
