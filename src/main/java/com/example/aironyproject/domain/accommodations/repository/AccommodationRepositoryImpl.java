package com.example.aironyproject.domain.accommodations.repository;

import static com.example.aironyproject.domain.accommodations.entity.QAccommodation.*;

import java.util.List;

import org.springframework.util.StringUtils;

import com.example.aironyproject.domain.accommodations.dto.CheckAccommodateResponse;
import com.example.aironyproject.domain.accommodations.enums.AccommodationStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccommodationRepositoryImpl implements AccommodationCustomRepository{

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<CheckAccommodateResponse> findByOption(Integer price, String name, AccommodationStatus status) {
		return jpaQueryFactory
			.select(Projections.constructor(CheckAccommodateResponse.class,
				accommodation.price,
				accommodation.name,
				accommodation.status))
			.from(accommodation)
			.where(
				accommodationPriceEq(price),
				accommodationNameContains(name),
				accommodationStatusEq(status)
			)
			.orderBy(accommodation.id.asc())
			.fetch();
	}

	private BooleanExpression accommodationPriceEq(Integer price){
		return price != null ? accommodation.price.eq(price) : null;
	}

	private BooleanExpression accommodationNameContains(String name){
		return StringUtils.hasText(name) ? accommodation.name.contains(name) : null;
	}

	private BooleanExpression accommodationStatusEq(AccommodationStatus status){
		return status != null ? accommodation.status.eq(status) : null;
	}
}
