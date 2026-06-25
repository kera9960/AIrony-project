package com.example.aironyproject.domain.reservation.controller;

import com.example.aironyproject.common.redisson.facade.ReservationLockFacade;
import com.example.aironyproject.domain.accommodations.entity.Accommodation;
import com.example.aironyproject.domain.accommodations.enums.AccommodationStatus;
import com.example.aironyproject.domain.accommodations.repository.AccommodationRepository;
import com.example.aironyproject.domain.reservation.dto.CreateReservationRequest;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationConcurrencyTest {

	@Autowired
	private ReservationLockFacade reservationLockFacade;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccommodationRepository accommodationRepository;

	@Test
	@DisplayName("100명이 동시에 같은 숙소, 같은 날짜를 예약하면 딱 1명만 성공해야 한다.")
	void reserveRoom_concurrency_test() throws InterruptedException {
		// =================================================================
		// 1. 테스트용 필수 데이터 준비
		// =================================================================

		// 1) 가짜 유저 생성 (email, password, name, phoneNumber 순서)
		// role은 엔티티 내부에서 UserRole.MEMBER로 자동 세팅됩니다.
		User user = new User(
			"test@test.com",
			"password123!",
			"테스터",
			"010-1234-5678"
		);
		User savedUser = userRepository.save(user);

		// 2) 가짜 숙소 생성 (name, address, description, price, status 순서)
		Accommodation accommodation = new Accommodation(
			"제주도 동시성 테스트 펜션",
			"제주특별자치도 제주시",
			"동시성 테스트를 위한 가짜 펜션입니다.",
			100000,
			AccommodationStatus.ACTIVE
		);
		Accommodation savedAccommodation = accommodationRepository.save(accommodation);


		// =================================================================
		// 2. 동시성 테스트 환경 세팅
		// =================================================================
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		// 방금 DB에 밀어넣은 숙소의 ID를 사용해서 요청서(Request)를 만듭니다.
		CreateReservationRequest request = new CreateReservationRequest(
			savedAccommodation.getId(),
			null,
			LocalDate.of(2026, 12, 25), // 체크인
			LocalDate.of(2026, 12, 26)  // 체크아웃
		);

		// 방금 DB에 밀어넣은 유저의 ID를 추출합니다.
		Long userId = savedUser.getId();

		// =================================================================
		// 3. 100개의 쓰레드가 출발선에서 동시에 탕! 하고 예약 시도
		// =================================================================
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					// 우리가 만든 자물쇠(Facade)를 통과하여 예약을 시도합니다.
					reservationLockFacade.reserveRoom(userId, request);
					successCount.incrementAndGet(); // 에러 없이 성공하면 +1
				} catch (Exception e) {
					// 락 획득 실패(CustomException) 등의 에러가 터지면 튕겨나가며 실패 +1
					failCount.incrementAndGet();
				} finally {
					// 성공하든 실패하든 자기 역할이 끝났으니 카운트다운을 줄입니다.
					latch.countDown();
				}
			});
		}

		// 100개의 쓰레드가 모두 끝날 때까지 메인 쓰레드는 기다려 줍니다.
		latch.await();

		// =================================================================
		// 4. 최종 결과 검증
		// =================================================================
		System.out.println("====== 최종 결과 ======");
		System.out.println("✅ 성공한 예약 횟수: " + successCount.get());
		System.out.println("❌ 실패한 예약 횟수: " + failCount.get());

		// 100명 중 단 1명만 성공하고, 99명은 실패해야 완벽한 자물쇠입니다!
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(99);
	}
}
