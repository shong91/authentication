package com.shong91.app.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {

  private String accessToken;
  private String refreshToken;
}
