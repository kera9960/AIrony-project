package com.example.aironyproject.domain.accommodationLike.service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodationLike.dto.CreateAccommodationLikeResponse;
import com.example.aironyproject.domain.accommodationLike.dto.GetMyAccommodationLikeResponse;
import com.example.aironyproject.domain.accommodationLike.dto.PopularAccommodationResponse;
import com.example.aironyproject.domain.accommodationLike.entity.AccommodationLike;
import com.example.aironyproject.domain.accommodationLike.repository.AccommodationLikeRepository;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationLikeService {

    private final AccommodationLikeRepository accommodationLikeRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

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

        AccommodationLike accommodationLike = new AccommodationLike(user, accommodation);
        AccommodationLike savedLike = accommodationLikeRepository.save(accommodationLike);

        return CreateAccommodationLikeResponse.from(savedLike);
    }

    @Transactional
    public void cancelLike(Long userId, Long accommodationId) {
        AccommodationLike accommodationLike = accommodationLikeRepository
                .findByUser_IdAndAccommodation_Id(userId, accommodationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOMMODATION_LIKE_NOT_FOUND));

        accommodationLikeRepository.delete(accommodationLike);
    }

    public List<GetMyAccommodationLikeResponse> getMyAccommodationLike(Long userId) {

        List<AccommodationLike> accommodationLikes =
                accommodationLikeRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);

        return accommodationLikes.stream()
                .map(GetMyAccommodationLikeResponse::from)
                .toList();
    }

    public List<PopularAccommodationResponse> getPopularAccommodations() {

        return accommodationLikeRepository.findPopularAccommodation();
    }
}
