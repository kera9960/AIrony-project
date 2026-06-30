package com.example.aironyproject.domain.accommodationLike.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum PopularAccommodationRankingType {
    ALL(null), // TTL: 없음
    DAILY(Duration.ofDays(2)), // TTL: 2일
    WEEKLY(Duration.ofDays(14)), // TTL: 14일
    MONTHLY(Duration.ofDays(60)); // TTL: 60일

    private final Duration ttl;

    PopularAccommodationRankingType(Duration ttl) {
        this.ttl = ttl;
    }

    public boolean hasTtl() {
        return ttl != null;
    }
}
