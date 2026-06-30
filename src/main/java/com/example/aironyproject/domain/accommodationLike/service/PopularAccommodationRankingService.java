package com.example.aironyproject.domain.accommodationLike.service;

import com.example.aironyproject.domain.accommodationLike.dto.AccommodationLikeCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
        Double score = stringRedisTemplate.opsForZSet().incrementScore(
                POPULAR_ACCOMMODATION_RANKING,
                accommodationId.toString(),
                -1
        );

        if (score != null && score <= 0) {
            stringRedisTemplate.opsForZSet().remove(
                    POPULAR_ACCOMMODATION_RANKING,
                    accommodationId.toString());
        }
    }

    // 초기화
    public void initializeRanking(List<AccommodationLikeCountResponse> likeCounts) {
        stringRedisTemplate.delete(POPULAR_ACCOMMODATION_RANKING);

        for (AccommodationLikeCountResponse accommodationLikeCountResponse : likeCounts) {
            stringRedisTemplate.opsForZSet().add(
                    POPULAR_ACCOMMODATION_RANKING,
                    accommodationLikeCountResponse.accommodationId().toString(),
                    accommodationLikeCountResponse.likeCount()
            );
        }
    }

    // Top10 조회
    public Set<ZSetOperations.TypedTuple<String>> getTop10PopularAccommodations() {
        return stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(POPULAR_ACCOMMODATION_RANKING, 0, 9);
    }
}
