package com.example.aironyproject.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ChatAsyncConfig {

  // 비동기 실행할 때 chatEventExecutor라는 이름의 쓰레드풀을 사용
  // Executor는 비동기 작업을 어느 쓰레드에서 실행할지 관리하는 객체
  @Bean(name = "chatEventExecutor")
  public Executor chatEventExecutor() {

    // Spring에서 제공하는 쓰레드풀 Executor 구현체
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // 기본 작업자 4명 준비
    executor.setCorePoolSize(4);

    // 일 많아지면 최대 16명까지 늘릴 수 있음
    executor.setMaxPoolSize(16);

    // 쓰레드가 바쁘면 작업 1000개까지 줄 세워둘 수 있음
    executor.setQueueCapacity(1000);

    // 로그에 쓰레드 이름 형식 정하기
    // 예: chat-event-1, chat-event-2
    executor.setThreadNamePrefix("chat-event-");

    // 위의 설정값으로 쓰레드풀 준비하기
    executor.initialize();

    return executor;
  }
}
