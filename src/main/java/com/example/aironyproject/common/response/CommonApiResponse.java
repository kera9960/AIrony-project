package com.example.aironyproject.common.response;

import com.example.aironyproject.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
// null 값인 필드는 JSON 응답에서 제외합니다.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonApiResponse<T> {

  private final int status;
  private final String code;
  private final String message;
  private final T data;

  private CommonApiResponse(int status, String code, String message, T data) {
    this.status = status;
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> CommonApiResponse<T> success(HttpStatus status, String message, T data) {
    return new CommonApiResponse<>(status.value(), null, message, data);
  }

  /**
   * 에러 코드만 포함한 기본 실패 응답을 생성
   * 주로 리소스 없음, 권한 없음, 중복 요청처럼 추가 데이터가 필요 없는 예외에서 사용
   */
  public static CommonApiResponse<Void> error(ErrorCode errorCode) {
    return new CommonApiResponse<>(
        errorCode.getStatus(),
        errorCode.getCode(),
        errorCode.getMessage(),
        null
    );
  }

  /**
   * 에러 코드와 상세 데이터를 포함한 실패 응답을 생성
   * 주로 입력값 검증 실패처럼 어떤 필드에서 문제가 발생했는지 추가 정보가 필요할 때 사용
   */
  public static <T> CommonApiResponse<T> error(ErrorCode errorCode, T data) {
    return new CommonApiResponse<>(
        errorCode.getStatus(),
        errorCode.getCode(),
        errorCode.getMessage(),
        data
    );
  }
}
