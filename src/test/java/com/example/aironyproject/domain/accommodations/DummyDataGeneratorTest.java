package com.example.aironyproject.domain.accommodations;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
class DummyDataGeneratorTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 스프링 컨텍스트 로딩 시 실제 Redis 연결을 시도하지 않도록 Mocking 처리
    @MockitoBean
    private RedissonClient redissonClient;

    @Test
    @DisplayName("숙소 더미 데이터 100만 건 벌크 인서트")
    //@Disabled // 평소 빌드 시 실행되지 않도록 막아둠. 데이터 넣을 때만 주석 해제하고 수동 실행!
    void insertDummyAccommodations() {

        // 0. 외래 키 검사 임시 해제 후 테이블 비우기
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
        jdbcTemplate.execute("TRUNCATE TABLE accommodations;");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;"); // 다시 켜주기 (매우 중요)

        // 1. SQL 쿼리에 region 컬럼 추가 및 바인딩 파라미터(?) 1개 추가
        String sql = "INSERT INTO accommodations (region, name, address, description, price, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        int totalCount = 1000000;
        int batchSize = 1000; // 1,000건씩 끊어서 묶음 처리
        List<Object[]> batchArgs = new ArrayList<>();
        Random random = new Random();

        LocalDateTime now = LocalDateTime.now();
        String[] regions = {"제주", "서울", "부산", "수원", "강릉"};
        String[] statuses = {"ACTIVE", "INACTIVE"};

        for (int i = 1; i <= totalCount; i++) {
            String region = regions[random.nextInt(regions.length)];
            String status = statuses[random.nextInt(statuses.length)]; // ACTIVE or INACTIVE 랜덤
            // 30,000원 ~ 500,000원 사이의 랜덤 가격 (1,000원 단위)
            int price = (random.nextInt(48) + 3) * 10000;

            // 2. Object 배열의 첫 번째 요소로 region 데이터 삽입
            batchArgs.add(new Object[]{
                    region, // <-- 새로 추가된 region 독립 컬럼 데이터
                    region + " 럭셔리 숙소 " + i,
                    region + " 어딘가 " + i + "번지",
                    "테스트 더미 숙소입니다.",
                    price,
                    status,
                    now,
                    now
            });

            // batchSize만큼 쌓이면 DB에 한 번에 쏘고 리스트 비우기
            if (i % batchSize == 0) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
                batchArgs.clear();
                System.out.println(i + "건 인서트 완료...");
            }
        }
        // 출력 메세지도 10만 건에서 100만 건으로 수정
        System.out.println("✅ 100만 건 더미 데이터 적재 완료!");
    }
}