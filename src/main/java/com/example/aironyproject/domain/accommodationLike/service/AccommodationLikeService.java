package com.example.aironyproject.domain.accommodationLike.service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodationLike.dto.AccommodationLikeCountResponse;
import com.example.aironyproject.domain.accommodationLike.dto.CreateAccommodationLikeResponse;
import com.example.aironyproject.domain.accommodationLike.dto.GetMyAccommodationLikeResponse;
import com.example.aironyproject.domain.accommodationLike.dto.PopularAccommodationResponse;
import com.example.aironyproject.domain.accommodationLike.entity.AccommodationLike;
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

    public List<PopularAccommodationResponse> getPopularAccommodations() {
        // Redis Sorted Set에서 찜 수(score)가 높은 순서로 인기 숙소 Top 10 조회
        Set<ZSetOperations.TypedTuple<String>> rankingTuples =
                popularAccommodationRankingService.getTop10PopularAccommodations();

        // Redis 랭킹 데이터가 없으면 기존 DB 집계 쿼리로 fallback
        if (rankingTuples == null || rankingTuples.isEmpty()) {
            return accommodationLikeRepository.findPopularAccommodation();
        }

        // Redis에서 조회한 값 중 숙소 ID(value) 또는 찜 수(score)가 없는 데이터는 제외
        List<ZSetOperations.TypedTuple<String>> validRankingTuples = rankingTuples.stream()
                .filter(tuple -> tuple.getValue() != null)
                .filter(tuple -> tuple.getScore() != null)
                .toList();

        // 유효한 Redis 랭킹 데이터가 없으면 기존 DB 집계 쿼리로 fallback
        if (validRankingTuples.isEmpty()) {
            return accommodationLikeRepository.findPopularAccommodation();
        }

        // Redis Sorted Set의 member(value)는 accommodationId 문자열이므로 Long 타입으로 변환
        List<Long> accommodationIds = validRankingTuples.stream()
                .map(tuple -> Long.valueOf(Objects.requireNonNull(tuple.getValue())))
                .toList();

        // Redis에는 숙소 ID와 찜 수만 있으므로, 숙소명 등 상세 정보는 DB에서 조회
        Map<Long, Accommodation> accommodationMap = accommodationRepository.findAllById(accommodationIds)
                .stream()
                .collect(Collectors.toMap(Accommodation::getId, Function.identity()));

        // Redis 랭킹 순서를 유지하면서 DB 숙소 정보와 Redis score를 조합해 응답 DTO 생성
        return validRankingTuples.stream()
                .map(tuple -> {
                    String value = Objects.requireNonNull(tuple.getValue());
                    Double score = Objects.requireNonNull(tuple.getScore());

                    Long accommodationId = Long.valueOf(value);
                    Accommodation accommodation = accommodationMap.get(accommodationId);

                    // Redis에는 숙소 ID가 있지만 DB에서 숙소를 찾지 못한 경우 응답에서 제외
                    if (accommodation == null || accommodation.getStatus() != AccommodationStatus.ACTIVE) {
                        return null;
                    }

                    Long likeCount = score.longValue();

                    return PopularAccommodationResponse.from(accommodation, likeCount);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public void initializePopularAccommodationRanking() {
        List<AccommodationLikeCountResponse> likeCounts =
                accommodationLikeRepository.findAccommodationLikeCounts();

        popularAccommodationRankingService.initializeRanking(likeCounts);
    }
}
