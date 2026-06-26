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
	public List<CheckAccommodateResponse> findByOption(String region,Integer minPrice, Integer maxPrice, String name, AccommodationStatus status) {
		return jpaQueryFactory
			.select(Projections.constructor(CheckAccommodateResponse.class,
				accommodation.region,
				accommodation.price,
				accommodation.name,
				accommodation.status))
			.from(accommodation)
			.where(
				accommodationRegionEq(region), // 지역 추가
				accommodationPriceGoe(minPrice),// 이상, 이하 추가
				accommodationPriceLoe(maxPrice),
				accommodationNameContains(name),
				accommodationStatusEq(status)
			)
			.orderBy(accommodation.id.asc())
			.fetch();
	}

	// 지역 검색
	private BooleanExpression accommodationRegionEq(String region){
		return StringUtils.hasText(region) ? accommodation.region.eq(region) : null;
	}
	// 최소 가격 이상
	private BooleanExpression accommodationPriceGoe(Integer minPrice){
		return minPrice != null ? accommodation.price.goe(minPrice) : null;
	}

	// 최대 가격 이하
	private BooleanExpression accommodationPriceLoe(Integer maxPrice){
		return maxPrice != null ? accommodation.price.loe(maxPrice) : null;
	}

	private BooleanExpression accommodationNameContains(String name){
		return StringUtils.hasText(name) ? accommodation.name.contains(name) : null;
	}

	private BooleanExpression accommodationStatusEq(AccommodationStatus status){
		return status != null ? accommodation.status.eq(status) : null;
	}
}
