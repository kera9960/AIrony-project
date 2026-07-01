package com.example.aironyproject.common.config;

import com.example.aironyproject.domain.chat.messaging.ChatMessageSubscriber;
import com.example.aironyproject.domain.chat.messaging.ChatRedisChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

  // Redis에 문자열 데이터를 보내는 도구 등록
  // 현재 publisher에서 사용함
  @Bean
  public StringRedisTemplate stringRedisTemplate(
      // Redis 연결 담당
      // Redis 서버와 연결하는 방법을 알고 있는 객체
      RedisConnectionFactory connectionFactory
  ) {
    return new StringRedisTemplate(connectionFactory);
  }

  // Subscriber가 어떤 Redis 채널을 구독할지 알려주는 객체
  @Bean
  public ChannelTopic chatMessageTopic() {
    return new ChannelTopic(ChatRedisChannel.MESSAGE);
  }

  // Redis 채널을 구독하고 있다가 메세지가 오면 지정한 Subscriber를 실행해주는 객체
  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory,
      ChatMessageSubscriber subscriber,
      ChannelTopic chatMessageTopic
  ) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();

    // 컨테이너가 Redis 서버에 연결할 수 있도록 연결 정보 설정
    container.setConnectionFactory(connectionFactory);

    // chatMessageTopic 채널에 메세지가 오면 특정 subscriber 메서드를 호출
    container.addMessageListener(subscriber, chatMessageTopic);

    return container;
  }
}
