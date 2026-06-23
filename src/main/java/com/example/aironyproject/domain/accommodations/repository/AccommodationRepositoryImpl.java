package com.example.aironyproject.domain.accommodations.repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccommodationRepositoryImpl implements AccommodationCustomRepository{

	private final JPAQueryFactory jpaQueryFactory;
}
