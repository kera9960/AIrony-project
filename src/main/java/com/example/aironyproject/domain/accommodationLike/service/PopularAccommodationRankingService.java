package com.example.aironyproject.domain.accommodationLike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularAccommodationRankingService {

    private static final String POPULAR_ACCOMMODATION_RANKING = "popular:accommodations:ranking";

    private final StringRedisTemplate stringRedisTemplate;

    public void increaseLikeCount(Long accommodationId) {
        stringRedisTemplate.opsForZSet().incrementScore(
                POPULAR_ACCOMMODATION_RANKING,
                accommodationId.toString(),
                1
        );
    }

    public void decreaseLikeCount(Long accommodationId) {
        stringRedisTemplate.opsForZSet().incrementScore(
                POPULAR_ACCOMMODATION_RANKING,
                accommodationId.toString(),
                -1
        );
    }
}
