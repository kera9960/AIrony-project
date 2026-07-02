package com.example.aironyproject.domain.accommodationLike.service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodationLike.dto.*;
import com.example.aironyproject.domain.accommodationLike.entity.AccommodationLike;
import com.example.aironyproject.domain.accommodationLike.enums.PopularAccommodationRankingType;
import com.example.aironyproject.domain.accommodationLike.repository.AccommodationLikeRepository;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.enums.AccommodationStatus;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationLikeService {

    private final AccommodationLikeRepository accommodationLikeRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final PopularAccommodationRankingService popularAccommodationRankingService;

    @Transactional
    public CreateAccommodationLikeResponse createLike(Long userId, Long accommodationId) {

        // 유저와 숙소 확인 후 없으면 예외
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        // 이미 찜 한 숙소면 예외
        if (accommodationLikeRepository.existsByUser_IdAndAccommodation_Id(userId, accommodationId)) {
            throw new CustomException(ErrorCode.ACCOMMODATION_ALREADY_LIKED);
        }

        if (accommodation.getStatus() != AccommodationStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ACCOMMODATION_NOT_ACTIVE);
        }
        AccommodationLike accommodationLike = new AccommodationLike(user, accommodation);
        AccommodationLike savedLike = accommodationLikeRepository.save(accommodationLike);

        // 찜 등록 성공 후 Redis Sorted Set의 해당 숙소 찜 수 score +1
        popularAccommodationRankingService.increaseLikeCount(accommodationId);

        return CreateAccommodationLikeResponse.from(savedLike);
    }

    @Transactional
    public void cancelLike(Long userId, Long accommodationId) {
        AccommodationLike accommodationLike = accommodationLikeRepository
                .findByUser_IdAndAccommodation_Id(userId, accommodationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOMMODATION_LIKE_NOT_FOUND));

        accommodationLikeRepository.delete(accommodationLike);

        // 찜 취소 성공 후 Redis Sorted Set의 해당 숙소 찜 수 score -1
        popularAccommodationRankingService.decreaseLikeCount(accommodationId);
    }

    public List<GetMyAccommodationLikeResponse> getMyAccommodationLike(Long userId) {

        List<AccommodationLike> accommodationLikes =
                accommodationLikeRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);

        return accommodationLikes.stream()
                .map(GetMyAccommodationLikeResponse::from)
                .toList();
    }

    public List<PopularAccommodationResponse> getPopularAccommodations(PopularAccommodationRankingType rankingType) {
        // Redis Sorted Set에서 인기 숙소 후보를 조회
        // 실제 응답은 ACTIVE 숙소만 내려야 하므로, Redis에서는 20개를 조회
        Set<ZSetOperations.TypedTuple<String>> rankingTuples =
                popularAccommodationRankingService.getPopularAccommodationCandidates(rankingType);

        // Redis 랭킹 데이터가 없으면 기존 DB 집계 쿼리로 fallback
        if (rankingTuples == null || rankingTuples.isEmpty()) {
            return getPopularAccommodationsFromDatabase(rankingType);
        }

        // Redis에서 조회한 member/value와 score를 서비스에서 사용하기 쉬운 DTO로 변환
        // value가 숫자가 아니거나 score가 없는 잘못된 데이터는 from() 내부에서 null로 처리
        List<PopularAccommodationRanking> rankings = rankingTuples.stream()
                .map(PopularAccommodationRanking::from)
                .filter(Objects::nonNull)
                .toList();

        // 유효한 Redis 랭킹 데이터가 없으면 기존 DB 집계 쿼리로 fallback
        if (rankings.isEmpty()) {
            return getPopularAccommodationsFromDatabase(rankingType);
        }

        // Redis에는 숙소 ID와 찜 수만 저장되어 있으므로, 숙소명 등 응답에 필요한 정보를 조회하기 위해 ID 목록을 만들기
        List<Long> accommodationIds = rankings.stream()
                .map(PopularAccommodationRanking::accommodationId)
                .toList();

        // 숙소 정보는 DB에서 조회한 뒤, Redis 랭킹 순서를 유지하기 위해 id 기준 Map으로 변환
        Map<Long, Accommodation> accommodationMap = accommodationRepository.findAllById(accommodationIds)
                .stream()
                .collect(Collectors.toMap(Accommodation::getId, Function.identity()));

        // Redis 랭킹 순서를 기준으로 응답 DTO를 생성
        // DB에서 찾을 수 없거나 ACTIVE 상태가 아닌 숙소는 응답에서 제외
        List<PopularAccommodationResponse> responses = rankings.stream()
                .map(ranking -> {
                    Accommodation accommodation = accommodationMap.get(ranking.accommodationId());

                    if (accommodation == null || accommodation.getStatus() != AccommodationStatus.ACTIVE) {
                        return null;
                    }

                    return PopularAccommodationResponse.from(accommodation, ranking.likeCount());
                })
                .filter(Objects::nonNull)
                .limit(10)
                .toList();
        // Redis 랭킹은 있었지만 응답 가능한 숙소가 없으면 DB 집계 쿼리로 fallback
        if (responses.isEmpty()) {
            return getPopularAccommodationsFromDatabase(rankingType);
        }

        return responses;
    }

    // fallback 메서드
    private List<PopularAccommodationResponse> getPopularAccommodationsFromDatabase(PopularAccommodationRankingType rankingType) {

        // DB에서 ACTIVE 숙소별 찜 개수 집계
        List<AccommodationLikeCountResponse> counts = getLikeCountsByRankingType(rankingType);

        // Redis가 비어 있던 상황이므로 DB 집계 결과를 해당 기간 랭킹 key에 재적재
        popularAccommodationRankingService.replaceRanking(rankingType, counts);

        return getPopularAccommodations(rankingType);
    }

    private List<AccommodationLikeCountResponse> getLikeCountsByRankingType(
            PopularAccommodationRankingType rankingType
    ) {
        // 현재 날짜를 기준으로 일간/주간/월간 집계 범위를 계산
        LocalDate today = LocalDate.now();

        return switch (rankingType) {
            // 전체 랭킹은 기간 제한 없이 전체 찜 데이터를 집계
            case ALL -> accommodationLikeRepository.findAccommodationLikeCounts();

            // 일간 랭킹은 오늘 00:00:00부터 내일 00:00:00 전까지 집계
            case DAILY -> accommodationLikeRepository.findAccommodationLikeCountsBetween(
                    today.atStartOfDay(),
                    today.plusDays(1).atStartOfDay()
            );

            // 주간 랭킹은 ISO 기준 월요일 00:00:00부터 다음 주 월요일 00:00:00 전까지 집계
            case WEEKLY -> {
                LocalDate start = today.with(DayOfWeek.MONDAY);
                yield accommodationLikeRepository.findAccommodationLikeCountsBetween(
                        start.atStartOfDay(),
                        start.plusWeeks(1).atStartOfDay()
                );
            }

            // 월간 랭킹은 이번 달 1일 00:00:00부터 다음 달 1일 00:00:00 전까지 집계
            case MONTHLY -> {
                LocalDate start = today.withDayOfMonth(1);
                yield accommodationLikeRepository.findAccommodationLikeCountsBetween(
                        start.atStartOfDay(),
                        start.plusMonths(1).atStartOfDay()
                );
            }
        };
    }
}
