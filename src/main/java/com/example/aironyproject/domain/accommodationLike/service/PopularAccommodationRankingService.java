package com.example.aironyproject.domain.accommodationLike.service;

import com.example.aironyproject.domain.accommodationLike.enums.PopularAccommodationRankingType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PopularAccommodationRankingService {

    private static final String POPULAR_ACCOMMODATION_RANKING_PREFIX = "popular:accommodations:ranking";

    private final StringRedisTemplate stringRedisTemplate;

    public void increaseLikeCount(Long accommodationId) {
        for (PopularAccommodationRankingType rankingType : PopularAccommodationRankingType.values()) {
            String key = createRankingKey(rankingType);

            stringRedisTemplate.opsForZSet().incrementScore(
                    key,
                    accommodationId.toString(),
                    1
            );

            setRankingKeyTtl(key, rankingType);
        }
    }

    public void decreaseLikeCount(Long accommodationId) {
        for (PopularAccommodationRankingType rankingType : PopularAccommodationRankingType.values()) {
            String key = createRankingKey(rankingType);

            Double score = stringRedisTemplate.opsForZSet().incrementScore(
                    key,
                    accommodationId.toString(),
                    -1
            );

            if (score != null && score <= 0) {
                stringRedisTemplate.opsForZSet().remove(
                        key,
                        accommodationId.toString()
                );
            }
        }
    }

    // Top10 조회 -> INACTIVE 숙소가 조회될 수 있으니 20개를 가져오고 10개로 제한
    public Set<ZSetOperations.TypedTuple<String>> getPopularAccommodationCandidates(PopularAccommodationRankingType rankingType) {
        String key = createRankingKey(rankingType);

        return stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, 19);
    }

    // 기간별 랭킹 key 설정
    private String createRankingKey (PopularAccommodationRankingType rankingType) {
        // 현재 날짜 기준
        LocalDate now = LocalDate.now();

        return switch (rankingType) {
            // 전체 누적 랭킹은 날짜가 필요 없으므로 고정 key를 사용
            case ALL -> POPULAR_ACCOMMODATION_RANKING_PREFIX + ":all";
            // 일간 랭킹은 날짜별로 key를 분리
            case DAILY -> POPULAR_ACCOMMODATION_RANKING_PREFIX + ":daily:" + now;
            // 주간 랭킹은 ISO 기준 주차별로 key를 분리
            case WEEKLY -> {
                WeekFields weekFields = WeekFields.ISO;
                // 현재 날짜가 ISO 기준 몇 번째 주인지 계산
                int week = now.get(weekFields.weekOfWeekBasedYear());
                // 연말/연초에는 달력 연도와 주차 기준 연도가 다를 수 있어 weekBasedYear를 사용
                int year = now.get(weekFields.weekBasedYear());
                yield  POPULAR_ACCOMMODATION_RANKING_PREFIX
                        + ":weekly:"
                        + year
                        + "-W"
                        + String.format("%02d", week);
            }
            // 월간 랭킹은 연-월 기준으로 key를 분리
            case MONTHLY ->  POPULAR_ACCOMMODATION_RANKING_PREFIX
                    + ":monthly:"
                    + now.getYear()
                    + "-"
                    + String.format("%02d", now.getMonthValue());
        };
    }

    // TTL 설정 메서드
    private void setRankingKeyTtl(String key, PopularAccommodationRankingType rankingType) {
        if(rankingType.hasTtl()) {
            stringRedisTemplate.expire(key, rankingType.getTtl());
        }
    }
}
