# 인기 숙소 조회 고도화 리포트

## 캐싱 설계 및 의사결정

### 1. 캐싱 적용 배경

인기 숙소 Top 10은 대부분의 사용자에게 동일한 결과를 제공하는 조회 API입니다.  
하지만 V1에서는 요청이 들어올 때마다 `accommodation_likes` 테이블을 기준으로 `GROUP BY`, `COUNT`, `ORDER BY` 집계 쿼리가 반복 실행되었습니다.

찜 데이터가 늘어나거나 동시 요청이 증가하면 동일한 집계 쿼리가 계속 실행되어 DB 부하가 커질 수 있다고 판단했습니다.  
따라서 반복 조회 결과를 캐싱해 DB 집계 쿼리 실행 횟수를 줄이는 방향으로 고도화를 진행했습니다.

### 2. 캐싱 전략 선택

V2, V3에서는 Cache-aside 전략을 사용했습니다.

```text
캐시 조회 -> cache miss -> DB 조회 -> 캐시 저장 -> 응답 반환
캐시 조회 -> cache hit -> DB 조회 없이 캐시 응답 반환
```

인기 숙소 Top 10은 자주 조회되지만 매초 즉시 변경되어야 하는 데이터는 아니라고 판단해, 먼저 TTL 기반 캐싱을 적용했습니다.

다만 TTL 기반 캐싱은 찜 등록/취소가 발생해도 TTL이 만료되기 전까지 기존 결과가 반환될 수 있습니다.  
이 한계를 보완하기 위해 V4부터는 Redis Sorted Set을 사용해 찜 수를 score로 관리하고, 찜 등록/취소 시 score를 즉시 변경하는 방식으로 개선했습니다.

### 3. Redis Key 설계

Redis Key는 기능과 도메인이 드러나도록 prefix 기반으로 설계했습니다.

```text
V3: popular:accommodations:top10
V4: popular:accommodations:ranking:all
V5: popular:accommodations:ranking:daily:2026-07-02
V5: popular:accommodations:ranking:weekly:2026-W27
V5: popular:accommodations:ranking:monthly:2026-07
```

`popular`는 인기 데이터, `accommodations`는 숙소 도메인, `ranking`은 랭킹 데이터를 의미합니다.  
이렇게 key를 나누면 다른 Redis 데이터와 충돌 가능성을 줄이고, Redis CLI에서도 데이터를 확인하기 쉽습니다.

### 4. 로컬 캐시의 한계와 Redis 전환 이유

V2에서는 Caffeine Local Cache를 사용했습니다.  
반복 조회 시 응답 시간은 줄었지만, 캐시가 애플리케이션 내부 메모리에 저장된다는 한계가 있었습니다.

- 애플리케이션 재시작 시 캐시가 사라집니다.
- 서버가 여러 대일 경우 캐시를 공유할 수 없습니다.
- Scale-out 환경에서는 서버별로 동일한 DB 집계 쿼리가 다시 실행될 수 있습니다.

그래서 V3에서는 Redis를 외부 캐시 저장소로 도입했습니다.  
Redis는 여러 서버가 동일한 캐시 데이터를 공유할 수 있고, 서버 재시작 이후에도 TTL이 남아 있는 캐시를 재사용할 수 있습니다.

### 5. 캐시 데이터와 원본 데이터 동기화

V3 Redis TTL 캐시는 DB와 캐시 데이터가 일시적으로 달라질 수 있습니다.  
예를 들어 찜 등록/취소가 발생해도 TTL이 만료되기 전까지는 기존 인기 숙소 결과가 반환될 수 있습니다.

캐시 삭제 방식도 고려할 수 있지만, 찜 변경이 잦으면 캐시 삭제와 재생성이 반복되어 캐싱 효과가 떨어질 수 있습니다.

그래서 V4에서는 캐시 결과 전체를 삭제하지 않고 Redis Sorted Set의 score를 직접 갱신했습니다.  
찜 등록 시 score `+1`, 찜 취소 시 score `-1`을 반영해 랭킹 변화가 즉시 반영되도록 했습니다.

### 6. Redis 자료구조 선택 이유

V3에서는 DB에서 집계한 Top 10 결과 전체를 저장하는 것이 목적이었기 때문에 Redis String을 사용했습니다.

```text
key: popular:accommodations:top10
value: 인기 숙소 Top 10 JSON
```

V4 이후에는 찜 등록/취소에 따라 랭킹 score를 즉시 변경하고, score가 높은 순서로 조회해야 했습니다.  
따라서 member와 score를 함께 저장하고 score 기준 정렬을 제공하는 Redis Sorted Set을 선택했습니다.

```text
key: popular:accommodations:ranking:all
member: accommodationId
score: likeCount
```

### 7. RDBMS와 NoSQL 역할 분리

원본 데이터는 정합성이 중요하므로 MySQL에 저장했습니다.  
사용자, 숙소, 찜 데이터는 관계와 트랜잭션이 중요하기 때문에 RDBMS가 적합하다고 판단했습니다.

반면 인기 숙소 랭킹은 빠른 조회와 score 기반 정렬이 중요합니다.  
따라서 캐시와 랭킹 데이터는 Redis에서 관리하도록 역할을 분리했습니다.

---

## 고도화 과정

## V1. DB 집계 기반 인기 숙소 조회

### 구현 내용

- `accommodation_likes` 테이블 기준으로 숙소별 찜 수를 집계했습니다.
- QueryDSL로 `GROUP BY`, `COUNT`, `ORDER BY`, `LIMIT` 기반 쿼리를 작성했습니다.
- 찜 수 내림차순, 동일 찜 수일 경우 숙소 ID 오름차순으로 정렬했습니다.
- `ACTIVE` 상태 숙소만 조회되도록 조건을 적용했습니다.

### 확인한 문제

- 요청마다 동일한 DB 집계 쿼리가 반복 실행되었습니다.
- 찜 데이터가 증가할수록 집계 연산 비용이 증가할 수 있습니다.
- 인기 숙소 Top 10은 대부분의 사용자에게 동일한 결과이므로 매번 DB에서 재계산할 필요성이 낮다고 판단했습니다.

### 테스트 결과

| 찜 데이터 수 | 평균 응답 시간 |
| --- | ---: |
| 100건 | 12ms |
| 1,000건 | 16ms |
| 100,000건 | 34ms |

---

## V2. Caffeine Local Cache 적용

### 개선 내용

- Spring Cache와 Caffeine Cache를 사용했습니다.
- 인기 숙소 조회 메서드에 `@Cacheable`을 적용했습니다.
- 캐시 이름은 `popularAccommodations`, key는 `top10`으로 설정했습니다.
- TTL은 5분으로 설정했습니다.

```java
@Cacheable(value = "popularAccommodations", key = "'top10'")
public List<PopularAccommodationResponse> getPopularAccommodations() {
    return accommodationLikeRepository.findPopularAccommodation();
}
```

### 테스트 결과

| 찜 데이터 수 | V1 평균 응답 시간 | V2 캐시 hit 평균 응답 시간 |
| --- | ---: | ---: |
| 100건 | 12ms | 7.2ms |
| 1,000건 | 16ms | 7.1ms |
| 100,000건 | 34ms | 7.2ms |

### k6 부하 테스트 결과

| 구분 | 요청 수 | 평균 응답 시간 | 중앙값 | p95 |
| --- | ---: | ---: | ---: | ---: |
| DB 집계 조회 | 1,120건 | 71.78ms | 67.11ms | 80.93ms |
| Caffeine Cache | 1,200건 | 8.75ms | 3.18ms | 10.99ms |

### 한계

- 로컬 메모리 캐시라 애플리케이션 재시작 시 캐시가 사라집니다.
- 서버가 여러 대일 경우 캐시를 공유할 수 없습니다.

---

## V3. Redis String 기반 Remote Cache 적용

### 개선 내용

- 인기 숙소 Top 10 결과를 Redis에 저장했습니다.
- Redis key는 `popular:accommodations:top10`으로 설정했습니다.
- TTL은 5분으로 설정했습니다.
- Redis에 캐시가 없으면 DB에서 조회한 뒤 Redis에 저장하는 Cache-aside 방식으로 구현했습니다.

### 테스트 결과

- 첫 요청에서는 Redis cache miss로 DB 집계 쿼리가 실행되었습니다.
- 두 번째 요청부터는 Redis cache hit로 DB 집계 쿼리가 실행되지 않았습니다.
- 애플리케이션 재시작 후에도 TTL이 남아 있는 Redis 캐시를 재사용했습니다.
- TTL 만료 후에는 다시 DB 집계 쿼리가 실행되고 Redis 캐시가 갱신되었습니다.

### 한계

- TTL이 만료되기 전까지는 찜 변경 사항이 즉시 반영되지 않습니다.
- 찜 등록/취소가 자주 발생하는 데이터에는 단순 TTL 캐싱만으로는 실시간성이 부족합니다.

---

## V4. Redis Sorted Set 기반 인기 숙소 랭킹

### 개선 내용

- Redis Sorted Set을 사용해 인기 숙소 랭킹을 관리했습니다.
- 숙소 ID를 member로, 찜 수를 score로 저장했습니다.
- 찜 등록 시 score를 증가시키고, 찜 취소 시 score를 감소시켰습니다.
- 인기 숙소 조회 시 `ZREVRANGE`로 score가 높은 숙소를 조회했습니다.

### 사용한 Redis 명령

- `ZADD`: 기존 DB 찜 수 기준으로 랭킹 초기화
- `ZINCRBY`: 찜 등록/취소 시 score 증가 또는 감소
- `ZREVRANGE`: score가 높은 순서로 랭킹 조회
- `ZSCORE`: 특정 숙소의 현재 score 확인
- `ZREM`: 숙소 삭제 또는 비활성화 시 랭킹에서 제거할 때 사용 가능

### 테스트 결과

- 찜 등록/취소 후 Redis score가 변경되는 것을 확인했습니다.
- Redis CLI에서 score 기준 정렬 결과를 확인했습니다.
- 인기 숙소 조회 API에서 Redis Sorted Set의 순서대로 결과가 반환되는 것을 확인했습니다.
- 조회 시점에 DB에서 숙소 정보를 조회하고, `ACTIVE` 숙소만 최종 응답에 포함했습니다.

### 한계

- Redis에는 숙소 ID와 score만 있으므로 숙소 상태 변경은 DB 조회를 통해 확인해야 합니다.
- DB 저장과 Redis score 갱신이 하나의 트랜잭션으로 묶여 있지 않습니다.
- 추후 `afterCommit` 또는 이벤트 기반 Redis 갱신을 고려할 수 있습니다.

---

## V5. 전체/일간/주간/월간 인기 숙소 랭킹

### 개선 내용

- 전체 누적 찜 수 기준 랭킹의 한계를 보완하기 위해 기간별 랭킹을 추가했습니다.
- Redis Sorted Set key를 전체/일간/주간/월간으로 분리했습니다.
- `rankingType` 파라미터로 원하는 랭킹 기준을 선택할 수 있도록 했습니다.
- 기간별 key에는 TTL을 적용했고, 전체 랭킹 key에는 TTL을 적용하지 않았습니다.

### Redis Key 설계

```text
전체 랭킹: popular:accommodations:ranking:all
일간 랭킹: popular:accommodations:ranking:daily:2026-07-02
주간 랭킹: popular:accommodations:ranking:weekly:2026-W27
월간 랭킹: popular:accommodations:ranking:monthly:2026-07
```

### TTL 정책

| 랭킹 타입 | TTL |
| --- | ---: |
| 전체 | 없음 |
| 일간 | 2일 |
| 주간 | 14일 |
| 월간 | 60일 |

### 테스트 결과

- 랭킹 타입별로 서로 다른 Redis Sorted Set key를 사용하는 것을 확인했습니다.
- 같은 숙소라도 기간별 score가 다르면 조회 결과 순서가 다르게 반환되는 것을 확인했습니다.
- 일간/주간/월간 key에는 TTL이 적용되고, 전체 key에는 TTL이 적용되지 않는 것을 확인했습니다.
- 인기 숙소 화면에서 전체/일간/주간/월간 탭 변경 시 해당 기준의 랭킹을 조회하도록 구현했습니다.

### 한계

- 날짜가 바뀌면 새로운 일간 key가 생성되므로, 해당 key에 데이터가 없으면 일간 랭킹이 비어 있을 수 있습니다.
- 테스트 환경에서는 Redis ZADD 주입 또는 별도 초기화 로직이 필요합니다.
- 동일 score를 가진 숙소의 보조 정렬 기준은 추가 정책 정의가 필요합니다.

---

## 전체 흐름 요약

| 단계 | 방식 | 개선한 점 | 남은 한계 |
| --- | --- | --- | --- |
| V1 | DB 집계 쿼리 | 찜 수 기반 인기 숙소 기능 구현 | 매 요청마다 집계 쿼리 실행 |
| V2 | Caffeine Local Cache | 반복 조회 시 DB 집계 쿼리 제거 | 서버 재시작/Scale-out 한계 |
| V3 | Redis String Cache | 외부 캐시 저장소로 분리 | TTL 동안 오래된 결과 반환 가능 |
| V4 | Redis Sorted Set | 찜 등록/취소를 score에 즉시 반영 | DB/Redis 동기화 관리 필요 |
| V5 | 기간별 Sorted Set | 전체/일간/주간/월간 랭킹 제공 | 기간별 key 초기화/정책 관리 필요 |

---

자세한 과정! >> https://velog.io/@gksekqls21/기록-보드