package com.example.aironyproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // 400 BAD_REQUEST
  VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "입력값이 올바르지 않습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
  INVALID_RESERVATION_DATE(HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_DATE", "체크인/체크아웃 날짜가 올바르지 않습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다."),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
  INVALID_RESERVATION_PRICE(HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_PRICE", "예약 금액이 올바르지 않습니다."),

  // 403 FORBIDDEN
  FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),

  // 404 NOT_FOUND
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "회원을 찾을 수 없습니다."),
  ACCOMMODATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOMMODATION_NOT_FOUND", "숙소를 찾을 수 없습니다."),
  RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다."),
  PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다."),
  COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."),
  COUPON_NOT_ISSUABLE_PERIOD(HttpStatus.CONFLICT, "COUPON_NOT_ISSUABLE_PERIOD", "쿠폰 발급 가능 기간이 아닙니다."),
  USER_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_COUPON_NOT_FOUND", "보유 쿠폰을 찾을 수 없습니다."),
  CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_ROOM_NOT_FOUND", "문의방을 찾을 수 없습니다."),

  // 409 CONFLICT
  EMAIL_DUPLICATE(HttpStatus.CONFLICT, "EMAIL_DUPLICATE", "이미 사용 중인 이메일입니다."),
  ACCOMMODATION_INACTIVE(HttpStatus.CONFLICT, "ACCOMMODATION_INACTIVE", "예약할 수 없는 숙소입니다."),
  ALREADY_RESERVED_DATE(HttpStatus.CONFLICT, "ALREADY_RESERVED_DATE", "선택한 날짜에 이미 예약이 있습니다."),
  RESERVATION_ALREADY_CANCELED(HttpStatus.CONFLICT, "RESERVATION_ALREADY_CANCELED", "이미 취소된 예약입니다."),
  PAYMENT_ALREADY_REFUNDED(HttpStatus.CONFLICT, "PAYMENT_ALREADY_REFUNDED", "이미 환불된 결제입니다."),
  COUPON_INACTIVE(HttpStatus.CONFLICT, "COUPON_INACTIVE", "발급할 수 없는 쿠폰입니다."),
  COUPON_EXPIRED(HttpStatus.CONFLICT, "COUPON_EXPIRED", "쿠폰 발급 기간이 아닙니다."),
  COUPON_SOLD_OUT(HttpStatus.CONFLICT, "COUPON_SOLD_OUT", "쿠폰이 모두 소진되었습니다."),
  COUPON_ALREADY_ISSUED(HttpStatus.CONFLICT, "COUPON_ALREADY_ISSUED", "이미 발급받은 쿠폰입니다."),
  USER_COUPON_ALREADY_USED(HttpStatus.CONFLICT, "USER_COUPON_ALREADY_USED", "이미 사용한 쿠폰입니다."),
  USER_COUPON_EXPIRED(HttpStatus.CONFLICT, "USER_COUPON_EXPIRED", "만료된 쿠폰입니다."),
  INVALID_CHAT_ROOM_STATUS(HttpStatus.CONFLICT, "INVALID_CHAT_ROOM_STATUS", "변경할 수 없는 문의 상태입니다."),
  INVALID_RESERVATION_STATUS(HttpStatus.CONFLICT, "INVALID_RESERVATION_STATUS", "변경할 수 없는 예약 상태입니다."),
  CHAT_ROOM_ALREADY_ASSIGNED(HttpStatus.CONFLICT, "CHAT_ROOM_ALREADY_ASSIGNED", "이미 담당자가 배정된 문의입니다."),
  CHAT_ROOM_ALREADY_COMPLETED(HttpStatus.CONFLICT, "CHAT_ROOM_ALREADY_COMPLETED", "이미 완료된 문의입니다."),
  CHAT_MESSAGE_NOT_ALLOWED(HttpStatus.CONFLICT, "CHAT_MESSAGE_NOT_ALLOWED", "메시지를 보낼 수 없는 문의 상태입니다."),

  // 500 INTERNAL_SERVER_ERROR
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus httpStatus, String code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }

  /**
   * 공통 응답 객체에서 사용하기 위해 HttpStatus를 정수형 상태 코드로 변환하여 반환
   * @return HTTP 상태 코드 값 (예: 400, 404, 500)
   */
  public int getStatus() {
    return httpStatus.value();
  }
}
