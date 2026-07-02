package com.example.aironyproject.domain.accommodationLike.dto;

import org.springframework.data.redis.core.ZSetOperations;

public record PopularAccommodationRanking(
        Long accommodationId,
        Long likeCount
) {

    public static PopularAccommodationRanking from(ZSetOperations.TypedTuple<String> tuple) {
        String value = tuple.getValue();
        Double score = tuple.getScore();

        if (value == null || score == null) {
            return null;
        }

        try {
            return new PopularAccommodationRanking(
                    Long.valueOf(value),
                    score.longValue()
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
