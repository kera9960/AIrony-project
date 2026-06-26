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
		User user = new User(
			"test@test.com",
			"password123!",
			"테스터",
			"010-1234-5678"
		);
		User savedUser = userRepository.save(user);

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

		CreateReservationRequest request = new CreateReservationRequest(
			savedAccommodation.getId(),
			null,
			LocalDate.of(2026, 12, 25), // 체크인
			LocalDate.of(2026, 12, 26)  // 체크아웃
		);

		Long userId = savedUser.getId();

		// =================================================================
		// 3. 100개의 쓰레드가 출발선에서 동시에 탕! 하고 예약 시도
		// =================================================================
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					reservationLockFacade.reserveRoom(userId, request);
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// =================================================================
		// 4. 최종 결과 검증
		// =================================================================
		System.out.println("====== 100명 단일 날짜 테스트 결과 ======");
		System.out.println("✅ 성공한 예약 횟수: " + successCount.get());
		System.out.println("❌ 실패한 예약 횟수: " + failCount.get());

		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(99);
	}

	@Test
	@DisplayName("서로 다른 날짜(연박)로 예약해도 중간 날짜가 겹치면 1명만 성공해야 한다.")
	void reserveRoom_overlapping_concurrency_test() throws InterruptedException {
		// =================================================================
		// 1. 유저 2명과 테스트용 숙소 준비
		// =================================================================
		User user1 = userRepository.save(new User("test1@test.com", "pw123!", "유저1", "010-1111-1111"));
		User user2 = userRepository.save(new User("test2@test.com", "pw123!", "유저2", "010-2222-2222"));

		Accommodation accommodation = accommodationRepository.save(
			new Accommodation("연박 겹침 테스트 펜션", "주소", "설명", 100000, AccommodationStatus.ACTIVE)
		);

		// =================================================================
		// 2. 교묘하게 겹치는 요청서 2개 생성
		// =================================================================
		// 유저1: 12월 25일 ~ 12월 28일 (3박: 25, 26, 27일 락 획득 시도)
		CreateReservationRequest request1 = new CreateReservationRequest(
			accommodation.getId(), null, LocalDate.of(2026, 12, 25), LocalDate.of(2026, 12, 28)
		);

		// 유저2: 12월 26일 ~ 12월 27일 (1박: 26일 락 획득 시도 - 유저1과 겹침!)
		CreateReservationRequest request2 = new CreateReservationRequest(
			accommodation.getId(), null, LocalDate.of(2026, 12, 26), LocalDate.of(2026, 12, 27)
		);

		int threadCount = 2;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		// =================================================================
		// 3. 두 유저가 동시에 예약 버튼 클릭!
		// =================================================================
		executorService.submit(() -> {
			try {
				reservationLockFacade.reserveRoom(user1.getId(), request1);
				successCount.incrementAndGet();
			} catch (Exception e) {
				failCount.incrementAndGet();
			} finally {
				latch.countDown();
			}
		});

		executorService.submit(() -> {
			try {
				reservationLockFacade.reserveRoom(user2.getId(), request2);
				successCount.incrementAndGet();
			} catch (Exception e) {
				failCount.incrementAndGet();
			} finally {
				latch.countDown();
			}
		});

		latch.await();

		// =================================================================
		// 4. 최종 결과 검증
		// =================================================================
		System.out.println("====== 연박 겹침 테스트 결과 ======");
		System.out.println("✅ 성공한 예약 횟수: " + successCount.get());
		System.out.println("❌ 실패한 예약 횟수: " + failCount.get());

		// 시작 날짜가 달라도 MultiLock이 겹치는 날짜(26일)를 감지하여 1명은 튕겨내야 함!
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(1);
	}

	@Test
	@DisplayName("서로 전혀 겹치지 않는 날짜로 동시에 예약하면 방해받지 않고 둘 다 성공해야 한다.")
	void reserveRoom_non_overlapping_concurrency_test() throws InterruptedException {
		// =================================================================
		// 1. 유저 2명과 테스트용 숙소 준비
		// =================================================================
		User user1 = userRepository.save(new User("test3@test.com", "pw123!", "유저3", "010-3333-3333"));
		User user2 = userRepository.save(new User("test4@test.com", "pw123!", "유저4", "010-4444-4444"));

		Accommodation accommodation = accommodationRepository.save(
			new Accommodation("비연박 독립 테스트 펜션", "주소", "설명", 100000, AccommodationStatus.ACTIVE)
		);

		// =================================================================
		// 2. 전혀 겹치지 않는 요청서 2개 생성
		// =================================================================
		// 유저1: 12월 25일 ~ 12월 28일 (3박: 25, 26, 27일 락 획득 시도)
		CreateReservationRequest request1 = new CreateReservationRequest(
			accommodation.getId(), null, LocalDate.of(2026, 12, 25), LocalDate.of(2026, 12, 28)
		);

		// 유저2: 12월 29일 ~ 12월 31일 (2박: 29, 30일 락 획득 시도 - 유저1과 전혀 안 겹침!)
		CreateReservationRequest request2 = new CreateReservationRequest(
			accommodation.getId(), null, LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31)
		);

		int threadCount = 2;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		// =================================================================
		// 3. 두 유저가 동시에 예약 버튼 클릭!
		// =================================================================
		executorService.submit(() -> {
			try {
				reservationLockFacade.reserveRoom(user1.getId(), request1);
				successCount.incrementAndGet();
			} catch (Exception e) {
				failCount.incrementAndGet();
			} finally {
				latch.countDown();
			}
		});

		executorService.submit(() -> {
			try {
				reservationLockFacade.reserveRoom(user2.getId(), request2);
				successCount.incrementAndGet();
			} catch (Exception e) {
				failCount.incrementAndGet();
			} finally {
				latch.countDown();
			}
		});

		latch.await();

		// =================================================================
		// 4. 최종 결과 검증
		// =================================================================
		System.out.println("====== 겹치지 않는 날짜 테스트 결과 ======");
		System.out.println("✅ 성공한 예약 횟수: " + successCount.get());
		System.out.println("❌ 실패한 예약 횟수: " + failCount.get());

		// 날짜가 전혀 겹치지 않으므로 MultiLock이 서로 방해하지 않고 둘 다 성공해야 함!
		assertThat(successCount.get()).isEqualTo(2);
		assertThat(failCount.get()).isEqualTo(0);
	}
}
