package com.example.aironyproject.common.redisson.facade;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import com.example.aironyproject.domain.reservation.dto.CreateReservationRequest;
import com.example.aironyproject.domain.reservation.dto.CreateReservationResponse;
import com.example.aironyproject.domain.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationLockFacade {

	private final RedissonClient redissonClient;
	private final ReservationService reservationService;

	public CreateReservationResponse reserveRoom(Long userId, CreateReservationRequest request){
		String lockKey = "lock:accommodation" + request.accommodationId() + ":date:" + request.checkInDate();

		RLock lock = redissonClient.getLock(lockKey);

		try{
			boolean avaliable = lock.tryLock(5, 3, TimeUnit.SECONDS);

			if(!avaliable){
				throw new CustomException(ErrorCode.ALREADY_RESERVED_DATE);
			}

			return reservationService.createReservation(userId, request);
		}catch (InterruptedException e){
			throw new RuntimeException("락 획득 중 인터럽트 발생");
		} finally {
			if(lock.isLocked() && lock.isHeldByCurrentThread()){
				lock.unlock();
			}
		}
	}
}
