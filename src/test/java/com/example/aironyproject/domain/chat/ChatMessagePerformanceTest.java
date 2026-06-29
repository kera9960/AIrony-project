package com.example.aironyproject.domain.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.aironyproject.domain.chat.dto.request.SendChatMessageRequest;
import com.example.aironyproject.domain.chat.entity.ChatRoom;
import com.example.aironyproject.domain.chat.repository.ChatMessageRepository;
import com.example.aironyproject.domain.chat.repository.ChatRoomRepository;
import com.example.aironyproject.domain.chat.service.ChatRoomService;
import com.example.aironyproject.domain.user.entity.User;
import com.example.aironyproject.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Disabled("채팅 성능 확인용 테스트")
@SpringBootTest
public class ChatMessagePerformanceTest {

  @Autowired
  ChatRoomService chatRoomService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ChatRoomRepository chatRoomRepository;

  @Autowired
  ChatMessageRepository chatMessageRepository;

  // 회원 200명이 동시에 각 100개의 메시지를 전송하는 상황을 가정하여
  // 총 20,000건의 메시지 처리 시간과 저장 성공 여부를 측정
  @Test
  @DisplayName("회원 200명이 각자 100개씩 채팅 메시지 전송 성능 측정")
  void measureConcurrentMessageSendPerformanceWithMultipleMembers() throws InterruptedException {

    // given

    // 테스트에 사용할 관리자 더미 데이터 조회
    User admin = userRepository.findById(2L)
        .orElseThrow();

    // 테스트 규모 설정
    int memberCount = 200;
    int messagePerMember = 100;
    int totalMessageCount = memberCount * messagePerMember;

    // 테스트용 회원과 채팅방을 저장할 리스트
    List<User> members = new ArrayList<>();
    List<ChatRoom> chatRooms = new ArrayList<>();

    // 이메일 중복 방지를 위한 고유 값
    long uniqueValue = System.currentTimeMillis();

    // 회원 수만큼 테스트용 회원과 진행 중 채팅방 생성
    for (int i = 0; i < memberCount; i++) {
      User member = userRepository.save(
          new User(
              "performance-member-" + uniqueValue + "-" + i + "@test.com",
              "password",
              "성능테스트회원" + i,
              "010-9999-" + String.format("%04d", i)
          )
      );

      ChatRoom chatRoom = new ChatRoom(member, null, "성능 테스트 문의방 " + i);
      chatRoom.assignAdmin(admin);
      ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

      members.add(member);
      chatRooms.add(savedChatRoom);
    }

    // 회원 수만큼 스레드를 생성하여 동시에 메시지 전송
    ExecutorService executorService = Executors.newFixedThreadPool(memberCount);

    // 모든 스레드 작업이 끝날 때까지 기다리기 위한 장치
    CountDownLatch latch = new CountDownLatch(memberCount);

    long start = System.currentTimeMillis();

    // when

    // 각 회원이 본인의 채팅방에 메시지를 messagePerMember개씩 전송
    for (int i = 0; i < memberCount; i++) {
      int index = i;

      executorService.submit(() -> {
        try {
          User member = members.get(index);
          ChatRoom chatRoom = chatRooms.get(index);

          for (int j = 1; j <= messagePerMember; j++) {
            SendChatMessageRequest request =
                new SendChatMessageRequest("동시 전송 테스트 " + index + "-" + j);

            chatRoomService.sendMessage(
                chatRoom.getId(),
                member.getId(),
                request
            );
          }
        } finally {
          latch.countDown();
        }
      });
    }

    // 모든 스레드가 작업을 끝낼 때까지 대기
    latch.await();
    executorService.shutdown();

    long end = System.currentTimeMillis();

    // then
    long totalTime = end - start;
    double averageTime = (double) totalTime / totalMessageCount;

    // 각 채팅방에 저장된 메시지 수를 합산하여 유실 여부 확인
    long savedMessageCount = chatRooms.stream()
        .mapToLong(chatRoom -> chatMessageRepository.countByChatRoom_Id(chatRoom.getId()))
        .sum();

    System.out.println("회원 수 = " + memberCount);
    System.out.println("회원당 메시지 수 = " + messagePerMember);
    System.out.println("총 메시지 수 = " + totalMessageCount);
    System.out.println("총 소요 시간 = " + totalTime + "ms");
    System.out.println("평균 처리 시간 = " + averageTime + "ms");
    System.out.println("DB 저장 메시지 수 = " + savedMessageCount);

    assertEquals(totalMessageCount, savedMessageCount);
  }
}

