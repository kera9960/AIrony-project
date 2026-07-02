## 문제 상황

### 숙소 예약

여러 사용자가 동일 숙소의 겹치는 날짜를 동시에 예약하면, 각 요청이 모두 “예약 가능”으로 판단하여 중복 예약이 발생할 수 있습니다.

```text
요청 A: 중복 예약 없음 확인
요청 B: 중복 예약 없음 확인
요청 A: 예약 저장
요청 B: 예약 저장
```

### 쿠폰 발급

여러 사용자가 같은 쿠폰을 동시에 발급받으면 동일한 잔여 수량을 읽어 실제 수량보다 많은 쿠폰이 발급될 수 있습니다.

예약과 쿠폰은 보호 대상이 달라 서로 다른 락을 적용했습니다.

| 기능 | 적용 방식 |
|---|---|
| 예약 | Redisson 분산 MultiLock |
| 쿠폰 발급 | DB 비관적 쓰기 락 |

---

## 예약 분산 락

예약은 숙소와 숙박일별로 Redis 락 키를 생성합니다.

```text
lock:accommdation:{accommodationId}:dates:{date}
```

예를 들어 12월 25일부터 28일까지 예약하면 25일, 26일, 27일의 락을 생성하고 `MultiLock`으로 묶습니다.

```java
RLock multiLock = redissonClient.getMultiLock(locks);
multiLock.tryLock(5, TimeUnit.SECONDS);
```

이 구조를 통해:

- 날짜가 겹치는 예약은 하나씩 처리
- 날짜가 겹치지 않는 예약은 동시에 처리
- 여러 서버가 동일한 Redis 락을 공유

할 수 있습니다.

> 현재 키의 `accommdation`은 코드에 존재하는 오탈자로, 추후 `accommodation`으로 수정할 필요가 있습니다.

---

## 왜 Redisson을 사용했는가?

| 방식 | 검토 결과 |
|---|---|
| `synchronized` | 단일 JVM에서만 유효하여 다중 서버 환경에 부적합 |
| 비관적 락 | 숙소 행 전체를 잠그면 날짜가 다른 예약도 직렬화될 수 있음 |
| 낙관적 락 | 인기 날짜처럼 충돌이 많으면 재시도 비용 증가 |
| Redisson | 다중 서버에서 공유 가능하며 날짜별 락과 MultiLock 지원 |

예약은 여러 날짜를 하나의 작업으로 보호해야 하므로 Redisson MultiLock을 선택했습니다.

쿠폰은 하나의 DB 행에서 잔여 수량을 확인하고 감소시키므로 비관적 락을 사용했습니다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select c from Coupon c where c.id = :couponId")
Optional<Coupon> findByIdWithLock(Long couponId);
```

---

## 락 획득과 해제

락 획득은 최대 5초 동안 기다립니다.

```text
5초 내 획득 성공 → 예약 진행
5초 내 획득 실패 → 예외 반환
별도 재시도 없음
```

명시적인 lease time은 설정하지 않고 Redisson Watchdog을 사용합니다.

비즈니스 로직이 끝나면 `finally`에서 락을 해제합니다.

```java
try {
    return reservationService.createReservation(userId, request);
} finally {
    multiLock.unlock();
}
```

UUID와 Lua Script를 직접 구현하지 않고 락 소유권 확인과 안전한 해제는 Redisson에 위임했습니다.

---

## 테스트 결과

예약 동시성 테스트에서는 다음 시나리오를 검증했습니다.

| 시나리오 | 기대 결과 |
|---|---|
| 동일 숙소·동일 날짜 예약 100건 | 성공 1건, 실패 99건 |
| 일부 날짜가 겹치는 예약 2건 | 성공 1건, 실패 1건 |
| 날짜가 겹치지 않는 예약 2건 | 성공 2건, 실패 0건 |

현재 저장소에는 락 적용 후 테스트만 존재합니다. 락 적용 전 실패 테스트와 쿠폰 동시성 테스트는 추가 보완이 필요합니다.

---

## 결론

- 예약은 숙소와 날짜별 Redisson MultiLock으로 보호했습니다.
- 겹치는 날짜만 직렬화하여 불필요한 락 범위를 줄였습니다.
- 쿠폰 발급은 DB 비관적 락으로 잔여 수량의 정합성을 보호했습니다.
- 락 획득은 최대 5초 대기하며, 실패하면 재시도 없이 예외를 반환합니다.
- 안전한 락 해제와 Watchdog은 Redisson에 위임했습니다.