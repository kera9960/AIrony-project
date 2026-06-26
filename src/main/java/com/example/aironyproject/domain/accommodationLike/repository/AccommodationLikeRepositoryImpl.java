package com.example.aironyproject.domain.accommodationLike.repository;

import com.example.aironyproject.domain.accommodationLike.dto.PopularAccommodationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.aironyproject.domain.accommodationLike.entity.QAccommodationLike.accommodationLike;
import static com.example.aironyproject.domain.accommodations.entity.QAccommodation.accommodation;

@RequiredArgsConstructor
public class AccommodationLikeRepositoryImpl implements AccommodationLikeCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PopularAccommodationResponse> findPopularAccommodation() {
        return jpaQueryFactory
                .select(Projections.constructor(PopularAccommodationResponse.class,
                        accommodation.id,
                        accommodation.name,
                        accommodationLike.id.count()))
                .from(accommodationLike)
                .join(accommodationLike.accommodation, accommodation)
                .groupBy(accommodation.id)
                .orderBy(
                        accommodationLike.id.count().desc(), // 찜 수가 많은 순서로 정렬
                        accommodation.id.asc() // 찜 수가 같을 때 id로 오름순으로 기준 정렬
                )
                .limit(10) // 상위 10개만 조회
                .fetch();
    }
}
