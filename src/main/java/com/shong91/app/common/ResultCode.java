package com.shong91.app.common;

import lombok.Getter;

@Getter
public enum ResultCode {
  // success
  OK(200, "OK"),
  // general error code
  BAD_REQUEST(3400, "잘못된 요청입니다."),
  NO_SUCH_ELEMENT(4004, "조회된 데이터가 없습니다."),
  INVALID_ARGUMENT(4001, "유효하지 않은 파라미터 값입니다."),
  SERVER_ERROR(5000, "내부 서버 오류가 발생하였습니다."),
  // token error code
  NOT_FOUND_USER(4000, "아이디 혹은 비밀번호가 잘못되었습니다."),
  NO_AUTHORIZATION_ROLE(4010, "인증 권한 정보가 없습니다. "),
  WRONG_TYPE_TOKEN(4011, "잘못된 JWT 서명입니다."),
  EXPIRED_TOKEN(4012, "만료된 JWT 토큰입니다."),
  UNSUPPORTED_TOKEN(4013, "지원되지 않는 JWT 토큰입니다."),
  ILLEGAL_ARGUMENT_TOKEN(4014, "JWT 토큰이 잘못되었습니다.");

  private final int code;
  private final String description;

  ResultCode(int code, String description) {
    this.code = code;
    this.description = description;
  }
}
