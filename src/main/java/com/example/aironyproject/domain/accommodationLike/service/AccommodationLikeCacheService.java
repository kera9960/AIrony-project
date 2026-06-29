package com.example.aironyproject.domain.accommodationLike.service;

import com.example.aironyproject.domain.accommodationLike.dto.PopularAccommodationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationLikeCacheService {

    private final RedisTemplate<String, List<PopularAccommodationResponse>> redisTemplate;

    private static final String CACHE_POPULAR_ACCOMMODATIONS_KEY = "popular:accommodations:top10";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    // 캐시 조회
    public List<PopularAccommodationResponse> getTop10PopularAccommodations() {
        try {
            return redisTemplate.opsForValue()
                    .get(CACHE_POPULAR_ACCOMMODATIONS_KEY);
        } catch (RuntimeException e) {
            log.warn("인기 숙소 캐시 조회 실패", e);
            return null;
        }
    }

    // 캐시 저장
    public void savePopularAccommodations(List<PopularAccommodationResponse> popularAccommodations) {
        try {
            redisTemplate.opsForValue().set(
                    CACHE_POPULAR_ACCOMMODATIONS_KEY,
                    popularAccommodations,
                    CACHE_TTL
            );
        } catch (RuntimeException e) {
            log.warn("인기 숙소 캐시 저장 실패", e);
        }
    }
}
