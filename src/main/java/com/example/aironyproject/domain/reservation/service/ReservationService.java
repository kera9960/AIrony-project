package com.example.aironyproject.domain.reservation.service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.enums.AccommodationStatus;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;
import com.example.aironyproject.domain.payment.entity.Payment;
import com.example.aironyproject.domain.payment.service.PaymentService;
import com.example.aironyproject.domain.reservation.dto.CreateReservationRequest;
import com.example.aironyproject.domain.reservation.dto.CreateReservationResponse;
import com.example.aironyproject.domain.reservation.dto.GetMyReservationResponse;
import com.example.aironyproject.domain.reservation.entity.Reservation;
import com.example.aironyproject.domain.reservation.repository.ReservationRepository;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import com.example.aironyproject.domain.userCoupon.entity.UserCoupon;
import com.example.aironyproject.domain.userCoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserCouponRepository  userCouponRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    @Transactional
    public CreateReservationResponse createReservation(Long userId, CreateReservationRequest request) {

        User user = userRepository.getReferenceById(userId);

        Accommodation accommodation = accommodationRepository.findById(request.accommodationId())
                .orElseThrow(()-> new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND));

        // 비활성 숙소 검증
        if (accommodation.getStatus() !=AccommodationStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ACCOMMODATION_INACTIVE);
        }

        validateReservationPeriod(
                accommodation.getId(),
                request.checkInDate(),
                request.checkOutDate()
        );

        // 사용자의 쿠폰인지 확인
        UserCoupon userCoupon = findUserCoupon(userId, request.userCouponId());

        // 숙박 일수 계산
        int nights = Math.toIntExact(
                ChronoUnit.DAYS.between(
                        request.checkInDate(),
                        request.checkOutDate()
                )
        );

        // 할인 전 가격 계산
        int originalPrice = Math.multiplyExact(accommodation.getPrice(), nights);

        int discountAmount = 0;

        // 쿠폰이 null인지 검증하고
        if (userCoupon != null) {
            discountAmount = userCoupon.calculateDiscount(originalPrice);

            // 있으면 사용 시간을 기록해서 사용 완료 처리 하기
            userCoupon.use(LocalDateTime.now());
        }

        Reservation reservation = new Reservation(
                generateReservationNumber(),
                user,
                accommodation,
                userCoupon,
                request.checkInDate(),
                request.checkOutDate(),
                originalPrice,
                discountAmount
        );

        Reservation savedReservation = reservationRepository.save(reservation);

        Payment payment = paymentService.createPayment(
                savedReservation,
                savedReservation.getFinalPrice()
        );

        savedReservation.confirm();

        return CreateReservationResponse.from(savedReservation, payment);
    }

    // 선택한 날짜에 이미 예약이 있는지 검증
    private void validateReservationPeriod(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        boolean duplicated =
                reservationRepository.existsOverlappingReservation(
                    accommodationId,
                    checkInDate,
                    checkOutDate
                );

        if (duplicated) {
            throw new CustomException(ErrorCode.ALREADY_RESERVED_DATE);
        }
    }

    // 쿠폰을 선택하지 않으면 null 반환, 선택했다면 사용자가 보유한 쿠폰인지 확인
    private UserCoupon findUserCoupon(Long userId, Long userCouponId) {

        if (userCouponId == null) {

            return null;
        }

        return userCouponRepository.findByIdAndUser_Id(userCouponId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_COUPON_NOT_FOUND));
    }

    // 예약번호 발행
    private String generateReservationNumber() {
        return "RSV-" + UUID.randomUUID()
                .toString()
                .replaceAll("-", "");
    }

    public List<GetMyReservationResponse> getMyReservations(Long userId) {

        // 예약을 최신순으로 조회
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);

        return reservations.stream()
                .map(GetMyReservationResponse::from)
                .toList();
    }
}
