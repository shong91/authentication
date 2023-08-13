package com.shong91.app.controller;

import com.shong91.app.controller.dto.AuthDto;
import com.shong91.app.controller.dto.AuthDto.Response;
import com.shong91.app.service.AuthService;
import com.shong91.app.common.ResponseDto;
import com.shong91.app.common.ResultCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseDto<Response> signIn(@RequestBody @Valid AuthDto.Request request) {
    return new ResponseDto<>(ResultCode.OK, authService.signIn(request));
  }
}
