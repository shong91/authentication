package com.shong91.app.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class AuthDto {

  @Getter
  public static class Request {

    @Email
    private String email;

    @NotBlank(message = "비밀번호 정보가 없습니다.")
    private String password;
  }

  @Getter
  public static class Response {

    private UserDto user;
    private TokenDto token;

    public Response(UserDto user, TokenDto token) {
      this.user = user;
      this.token = token;
    }
  }
}
