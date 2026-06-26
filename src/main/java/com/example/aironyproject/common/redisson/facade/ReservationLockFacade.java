package com.example.aironyproject.common.redisson.facade;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.reservation.dto.CreateReservationRequest;
import com.example.aironyproject.domain.reservation.dto.CreateReservationResponse;
import com.example.aironyproject.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationLockFacade {

	private final RedissonClient redissonClient;
	private final ReservationService reservationService;

	public CreateReservationResponse reserveRoom(Long userId, CreateReservationRequest request){
		List<LocalDate> dates = request.checkInDate().datesUntil(request.checkOutDate()).toList();

		RLock[] locks = new RLock[dates.size()];

		for(int i=0; i<dates.size(); i++){
			String lockKey = "lock:accommdation:" + request.accommodationId() + ":dates:" + dates.get(i);
			locks[i] = redissonClient.getLock(lockKey);
		}

		RLock multiLock = redissonClient.getMultiLock(locks);

		boolean avaliable = false;
		try{
			avaliable = multiLock.tryLock(5, TimeUnit.SECONDS);
		} catch (InterruptedException e){
			Thread.currentThread().interrupt();
			throw new RuntimeException("락 획득 중 인터럽트 발생");
		}

		if(!avaliable){
			throw new CustomException(ErrorCode.ALREADY_RESERVED_DATE);
		}

		try{
			return reservationService.createReservation(userId, request);
		} finally {
			try{
				multiLock.unlock();
			} catch (IllegalMonitorStateException e){
				log.error("락 해제 실패 숙소 ID: {}", request.accommodationId(), e);
			}
		}
	}
}
