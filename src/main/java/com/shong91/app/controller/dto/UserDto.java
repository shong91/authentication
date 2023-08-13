package com.shong91.app.controller.dto;

import lombok.Getter;

@Getter
public class UserDto {
  private long id;
  private String email;
  private String username;

  public UserDto(long id, String email, String username) {
    this.id = id;
    this.email = email;
    this.username = username;
  }
}
